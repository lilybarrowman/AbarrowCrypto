package me.abarrow.stream;

import java.io.ByteArrayOutputStream;

class CopyFreeByteArrayOutputStream extends ByteArrayOutputStream {
  public byte[] getBuffer() {
    return buf;
  }
  
  public int getCount() {
    return count;
  }
}