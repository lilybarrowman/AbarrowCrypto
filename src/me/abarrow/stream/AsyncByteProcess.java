package me.abarrow.stream;

import java.io.IOException;

import me.abarrow.core.CryptoUtils;

public final class AsyncByteProcess extends ByteProcess {
  
  private DynamicByteQueue pre;
  private DynamicByteQueue post;
  private StreamProcess runnable;
  
  public AsyncByteProcess(StreamProcess r) {
    pre = new DynamicByteQueue();
    post = new DynamicByteQueue();
    runnable = r;
    runnable.startAsync(pre.getInputStream(), post.getOutputStream(), true);
  }
  
  public ByteProcess add(byte[] bytes, int start, int len) {
    pre.write(bytes, start, len);
    return this;
  }
  
  public byte[] finish() throws IOException {
    pre.doneWriting();
    while (!post.isDoneWriting()) {
      try {
        Thread.sleep(0);
      } catch (InterruptedException e) {
        post.doneReading();
        throw new IOException(e);
      }
    }
    
    byte[] result = new byte[post.available()];
    post.read(result);
    return result;
  }
}
