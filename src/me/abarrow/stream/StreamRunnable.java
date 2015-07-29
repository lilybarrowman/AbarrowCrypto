package me.abarrow.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public abstract class StreamRunnable implements Runnable {
  private InputStream src;
  private OutputStream dest;
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
    PipedInputStream inp = new PipedInputStream() {
      public final void close() throws IOException {
        StreamUtils.quitelyClose(dest);
        StreamUtils.quitelyClose(src);
      }
    };
    try {
      dest = new PipedOutputStream(inp);
      Thread thread = new Thread(this);
      thread.start();
      return inp;
    } catch (IOException e) {
      StreamUtils.quitelyClose(dest);
      StreamUtils.quitelyClose(inp);
      throw e;
    }
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
