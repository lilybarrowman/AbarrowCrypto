package me.abarrow.stream;

import java.io.ByteArrayOutputStream;

public class DirectByteOutputStream extends ByteArrayOutputStream {
  public byte[] getBuffer() {
    return buf;
  }
  
  public int getCount() {
    return count;
  }
}