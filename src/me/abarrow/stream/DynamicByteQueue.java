package me.abarrow.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import me.abarrow.core.CryptoUtils;

public class DynamicByteQueue {

  private static final int CHUNK_SIZE = 1024;

  private ConcurrentLinkedQueue<byte[]> byteQueue;
  private volatile int lastChunkIndex;
  private int firstChunkIndex;
  private byte[] front;

  private AtomicBoolean doneAdding;
  private AtomicLong chunkCount;
  private Object readLock;
  private Object writeLock;

  public DynamicByteQueue() {
    byteQueue = new ConcurrentLinkedQueue<byte[]>();
    lastChunkIndex = 0;
    firstChunkIndex = 0;
    doneAdding = new AtomicBoolean(false);
    readLock = new Object();
    writeLock = new Object();
    chunkCount = new AtomicLong(0);
    front = new byte[CHUNK_SIZE];
  }

  public void write(byte[] bytes) {
    write(bytes, 0, bytes.length);
  }

  public void write(byte[] bytes, int start, int length) {
    synchronized (writeLock) {
      if (doneAdding.get()) {
        return;
      }
      int srcPos = start;
      int remaining = length;
      while (remaining > 0) {
        int bufferRemaining = CHUNK_SIZE - lastChunkIndex;
        int bytesWritten = remaining > bufferRemaining ? bufferRemaining : remaining;
        System.arraycopy(bytes, srcPos, front, lastChunkIndex, bytesWritten);
        srcPos += bytesWritten;
        remaining -= bytesWritten;
        lastChunkIndex += bytesWritten;
        if (lastChunkIndex == CHUNK_SIZE) {
          lastChunkIndex = 0;
          byteQueue.add(front);
          front = new byte[CHUNK_SIZE];
          chunkCount.incrementAndGet();
        }
      }
    }
  }

  public void doneWriting() {
    synchronized (writeLock) {
      if (!doneAdding.get()) {
        chunkCount.incrementAndGet();
        byteQueue.add(front);
        doneAdding.set(true);
      }
    }
  }

  public void readTo(OutputStream out, int chunkSize) throws IOException {
    byte[] block = new byte[chunkSize];
    try {
      while(true) {
        int dataRead = read(block);
        if (dataRead == chunkSize) {
          out.write(block);
        } else if (dataRead == -1) {
          break;
        } else {
          out.write(block, 0, dataRead);
        }
      }
    } finally {
      CryptoUtils.fillWithZeroes(block);
    }
  }
  
  public int read(byte[] bytes) {
    return read(bytes, 0, bytes.length);
  }

  public int read(byte[] bytes, int start, int length) {
    synchronized (readLock) {
      int dataRead = 0;
      int destPos = start;
      int remaining = length;
      while (dataRead < length) {
        if (chunkCount.longValue() >= 2) {
          int bytesRead = innerRead(bytes, remaining, destPos, CHUNK_SIZE - firstChunkIndex, byteQueue.peek());
          remaining -= bytesRead;
          dataRead += bytesRead;
          destPos += bytesRead;
          if (firstChunkIndex == CHUNK_SIZE) {
            firstChunkIndex = 0;
            byteQueue.poll();
            chunkCount.decrementAndGet();
          }
        } else if (doneAdding.get()) {
          if (firstChunkIndex == lastChunkIndex) {
            return dataRead == 0 ? -1 : dataRead;
          }
          return dataRead + innerRead(bytes, remaining, destPos, lastChunkIndex - firstChunkIndex, byteQueue.peek());
        } else {
          try {
            Thread.sleep(0); // wait for more data to be written
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      return dataRead;
    }
  }

  private int innerRead(byte[] bytes, int length, int destPos, int bufferRemaining, byte[] buffer) {
    int bytesRead = length > bufferRemaining ? bufferRemaining : length;
    System.arraycopy(buffer, firstChunkIndex, bytes, destPos, bytesRead);
    Arrays.fill(buffer, firstChunkIndex, firstChunkIndex + bytesRead, CryptoUtils.ZERO_BYTE);
    firstChunkIndex += bytesRead;
    return bytesRead;
  }

}
