package me.abarrow.stream;

import java.io.IOException;

import me.abarrow.core.CryptoUtils;

public abstract class ByteProcess {
  
  public abstract ByteProcess add(byte[] bytes, int start, int len);
  
  public ByteProcess add(byte[] bytes) {
    return add(bytes, 0, bytes.length);
  }  
  
  public abstract byte[] finish() throws IOException;
  
  public byte[] finish(byte[] out, int start) throws IOException {
    byte[] res = finish();
    System.arraycopy(res, 0, out, start, res.length);
    CryptoUtils.fillWithZeroes(res);
    return out;
  }
}
