package me.abarrow.stream;

import java.io.IOException;

import me.abarrow.core.CryptoUtils;

public final class ByteProcess {
  
  private DynamicByteQueue pre;
  private StreamRunnable runnable;
  
  public ByteProcess(StreamRunnable r) {
    pre = new DynamicByteQueue();
    runnable = r;
  }
  
  
  public ByteProcess add(byte[] bytes, int start, int len) {
    pre.write(bytes, start, len);
    return this;
  }
  public ByteProcess add(byte[] bytes) {
    return add(bytes, 0, bytes.length);
  }
  public byte[] finish () throws IOException {
    pre.doneWriting();
    DynamicByteQueue outputBuffer = new DynamicByteQueue();
    runnable.start(pre.getInputStream(), outputBuffer.getOutputStream(), true);
    byte[] result = new byte[outputBuffer.available()];
    outputBuffer.read(result);
    return result;
  }
  
  public byte[] finish (byte[] out, int start) throws IOException {
    byte[] res = finish();
    System.arraycopy(res, 0, out, start, res.length);
    CryptoUtils.fillWithZeroes(res);
    return out;
  }
}
