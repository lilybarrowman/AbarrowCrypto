package me.abarrow.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import me.abarrow.core.CryptoUtils;

public class DynamicByteQueue {

  private final int chunkSize;

  private ConcurrentLinkedQueue<byte[]> byteQueue;
  private volatile int lastChunkIndex;
  private int firstChunkIndex;
  private byte[] front;

  private AtomicBoolean doneAdding;
  private boolean doneRemoving;
  private AtomicLong chunkCount;
  private Object readLock;
  private Object writeLock;

  private OutputStream out = new OutputStream() {

    @Override
    public void write(int b) {
      byte[] bytes = new byte[]{ (byte)(b & 0xff) };
      write(bytes);
    }
    
    @Override
    public void write(byte[] bytes) {
      DynamicByteQueue.this.write(bytes);
    }
    
    @Override
    public void write(byte[] bytes, int off, int len) {
      DynamicByteQueue.this.write(bytes, off, len);
    }
    
    public void close() {
      doneWriting();
    }
  };

  private InputStream in = new InputStream() {
    @Override
    public int read() {
      byte[] single = new byte[1];
      if (DynamicByteQueue.this.read(single) == -1) {
        return -1;
      } else {
        return single[0] & 0xff;
      }
    }

    @Override
    public int read(byte[] in) {
      return DynamicByteQueue.this.read(in);
    }

    @Override
    public int read(byte[] in, int off, int len) {
      return DynamicByteQueue.this.read(in, off, len);
    }

    @Override
    public boolean markSupported() {
      return false;
    }

    @Override
    public long skip(long n) {
      return DynamicByteQueue.this.skip(n);
    }

    @Override
    public int available() {
      return DynamicByteQueue.this.available();
    }
    
    public void close() {
      doneReading();
    }
  };

  public DynamicByteQueue() {
    this(1024);
  }

  public DynamicByteQueue(int sizeOfChunks) {
    byteQueue = new ConcurrentLinkedQueue<byte[]>();
    lastChunkIndex = 0;
    firstChunkIndex = 0;
    doneAdding = new AtomicBoolean(false);
    readLock = new Object();
    writeLock = new Object();
    chunkCount = new AtomicLong(0);
    chunkSize = sizeOfChunks;
    front = new byte[chunkSize];
    doneRemoving = false;
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
        int bufferRemaining = chunkSize - lastChunkIndex;
        int bytesWritten = remaining > bufferRemaining ? bufferRemaining : remaining;
        System.arraycopy(bytes, srcPos, front, lastChunkIndex, bytesWritten);
        srcPos += bytesWritten;
        remaining -= bytesWritten;
        lastChunkIndex += bytesWritten;
        if (lastChunkIndex == chunkSize) {
          lastChunkIndex = 0;
          byteQueue.add(front);
          front = new byte[chunkSize];
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
  
  public void doneReading() {
    synchronized (readLock) {
      if (doneRemoving) {
        return;
      }
      if (!doneAdding.get()) {
        doneWriting(); //this is and should be scary
      }
      skip(available());
      doneRemoving = true;
    }
  }
  
  public InputStream getInputStream() {
    return in;
  }
  
  public OutputStream getOutputStream() {
    return out;
  }

  public long skip(long bytesToSkip) {
    long bytesSkipped = 0;
    int val;
    while (bytesToSkip > 0) {
      val = innerReadOrSkip(null, 0, (bytesToSkip > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) bytesToSkip, true);
      if (val == -1) {
        return bytesSkipped;
      }
      bytesSkipped += val;
      bytesToSkip -= val;
    }
    return bytesSkipped;
  }

  public int read(byte[] bytes) {
    return read(bytes, 0, bytes.length);
  }

  public int read(byte[] bytes, int start, int length) {
    return innerReadOrSkip(bytes, start, length, false);
  }

  public int available() {
    synchronized (readLock) {
      if (doneRemoving) {
        return 0;
      }
      boolean noLongerWriting = doneAdding.get();
      int chunks = chunkCount.intValue();
      if (noLongerWriting) {
        if (chunks < 2) {
          return lastChunkIndex - firstChunkIndex;
        } else {
          return (chunks - 1) * chunkSize - firstChunkIndex + lastChunkIndex; 
        }
      } else {
        if (chunks < 2) {
          return 0;
        } else {
          return (chunks - 1) * chunkSize - firstChunkIndex;
        }
      }
      
    }
  }

  private int innerReadOrSkip(byte[] bytes, int start, int length, boolean skip) {
    synchronized (readLock) {
      if (doneRemoving) {
        return -1;
      }
      int dataRead = 0;
      int destPos = start;
      int remaining = length;
      while (dataRead < length) {
        if (chunkCount.longValue() >= 2) {
          int bytesRead = coreReadOrSkip(bytes, remaining, destPos, chunkSize - firstChunkIndex, byteQueue.peek(), skip);
          remaining -= bytesRead;
          dataRead += bytesRead;
          destPos += bytesRead;
          if (firstChunkIndex == chunkSize) {
            firstChunkIndex = 0;
            byteQueue.poll();
            chunkCount.decrementAndGet();
          }
        } else if (doneAdding.get()) {
          if (firstChunkIndex == lastChunkIndex) {
            doneRemoving = true;
            return dataRead == 0 ? -1 : dataRead;
          }
          return dataRead
              + coreReadOrSkip(bytes, remaining, destPos, lastChunkIndex - firstChunkIndex, byteQueue.peek(), skip);
        } else {
          try {
            Thread.sleep(0); // wait for more data to be written this can cause deadlock
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      return dataRead;
    }
  }

  private int coreReadOrSkip(byte[] bytes, int length, int destPos, int bufferRemaining, byte[] buffer, boolean skip) {
    int bytesRead = length > bufferRemaining ? bufferRemaining : length;
    if (!skip) {
      System.arraycopy(buffer, firstChunkIndex, bytes, destPos, bytesRead);
    }
    Arrays.fill(buffer, firstChunkIndex, firstChunkIndex + bytesRead, CryptoUtils.ZERO_BYTE);
    firstChunkIndex += bytesRead;
    return bytesRead;
  }

}
