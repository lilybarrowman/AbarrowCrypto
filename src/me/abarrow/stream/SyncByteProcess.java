package me.abarrow.stream;

import java.io.IOException;

import me.abarrow.core.CryptoUtils;

public final class SyncByteProcess extends ByteProcess {
  
  private DynamicByteQueue pre;
  private DynamicByteQueue post;
  private StreamRunnable runnable;
  
  public SyncByteProcess(StreamRunnable r) {
    pre = new DynamicByteQueue();
    post = new DynamicByteQueue();
    runnable = r;
  }
  
  public ByteProcess add(byte[] bytes, int start, int len) {
    pre.write(bytes, start, len);
    return this;
  }

  public byte[] finish() throws IOException {
    pre.doneWriting();
    post = new DynamicByteQueue();
    runnable.runSync(pre.getInputStream(), post.getOutputStream(), true);
    byte[] result = new byte[post.available()];
    post.read(result);
    return result;
  }
}
