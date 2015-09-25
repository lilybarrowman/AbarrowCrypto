package me.abarrow.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class StreamRunnable implements Runnable {
  private InputStream src;
  private OutputStream dest;
  private InputStream related;
  private boolean closeOnEnd;
  private IOException failure;  
  
  public abstract void process(InputStream in, OutputStream out) throws IOException;
  
  @Override
  public final void run() {
    try {
      process(src, dest);
    } catch (IOException e) {
      e.printStackTrace();
      StreamUtils.quitelyClose(dest);
      StreamUtils.quitelyClose(related);
      flagFailure(e);
    } finally {
      StreamUtils.quitelyClose(src);
      if (closeOnEnd) {
        StreamUtils.quitelyClose(dest);
      }
    }
    
  }
  
  private synchronized final void flagFailure(IOException failureReason) {
    failure = failureReason;
  }
  
  public synchronized final void throwIfFailed() throws IOException {
    if (failure != null) {
      throw failure;
    }
  }
  
  
  public synchronized final boolean didFail() {
    return (failure != null);
  }
  
  public synchronized final IOException getFailureReason() {
    return failure;
  }

  public final InputStream start(InputStream in) throws IOException {
    src = in;
    closeOnEnd = true;
    DynamicByteQueue q = new DynamicByteQueue();
    dest = q.getOutputStream();
    related = q.getInputStream();
    Thread thread = new Thread(this);
    thread.start();
    return related;
  }
  
  public final ByteProcess start() {
    return new ByteProcess(this);
  }
  
  public final byte[] start(byte[] input) throws IOException {
    return start().add(input).finish();
  }

  public final OutputStream start(InputStream in, OutputStream out) throws IOException {
    return start(in, out, true);
  }
  
  public final OutputStream start(InputStream in, OutputStream out, boolean closeWhenDone) throws IOException {
    src = in;
    dest = out;
    closeOnEnd = closeWhenDone;
    run();
    if (didFail()) {
      throw getFailureReason();
    }
    return out;
  }

}
