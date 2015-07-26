package me.abarrow.stream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class CryptoStream extends InputStream {
  
  private InputStream src;
  private byte[] outputBuffer;
  private byte[] inputBuffer;
  private int outputPos;
  private int inputPos;
  private int outputBufferSize;
  private int inputBufferSize;

  private boolean closed;

  protected CryptoStream(InputStream in) {
    src = in;
    closed = false;
    outputBufferSize = 1024;
    outputBuffer = new byte[outputBufferSize];
    outputPos = -1;
    
    inputBufferSize = 1024;
    inputBuffer = new byte[inputBufferSize];
    inputPos = -1;
  }
    
  public int addToOutputBuffer() throws IOException {
    
    return -1;
  }
  
  @Override
  public final int read(byte[] bytes, int offset, int length) throws IOException {
    if (closed) {
      return -1;
    }
    int byteIndex = offset;
    int last = offset + length;
    int filled = 0;
    while (offset < last) {
      int bytesLeft = outputBuffer.length - outputPos;
      if (outputPos == -1 || bytesLeft < 1) {
        bytesLeft = addToOutputBuffer();
        if (bytesLeft == -1) {
          closed = true;
          return -1;
        }
        outputPos = 0;
      }
      int maxCopy = last - byteIndex;
      int bytesCopied = (bytesLeft < maxCopy) ? bytesLeft : maxCopy;
      System.arraycopy(outputBuffer, outputPos, bytes, byteIndex, bytesCopied); 
      byteIndex += bytesCopied;
      filled += bytesCopied;
    }
    return filled;
  }

  @Override
  public final int read(byte[] bytes) throws IOException {
    return this.read(bytes, 0, bytes.length);
  }

  @Override
  public final int read() throws IOException {
    byte[] single = new byte[1];
    return (read(single) == -1) ? -1 : single[0] & 0xff;
  }

  @Override
  public final int available() throws IOException {
    return 0;
  }

  @Override
  public final boolean markSupported() {
    return false;
  }

  @Override
  public final long skip(long n) throws IOException {
    throw new IOException("Cannot seak a CryptoStream.");
  }

  @Override
  public final void reset() throws IOException {
    throw new IOException("Cannot reset a CryptoStream.");
  }

  @Override
  public final void mark(int readlimit) {
  }

  @Override
  public final void close() throws IOException {
    closed = true;
    src.close();
  }

}
