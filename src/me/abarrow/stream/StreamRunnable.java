package me.abarrow.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamRunnable implements Runnable {
  
  private InputStream src;
  private OutputStream dest;
  private StreamProcess processor;
  private boolean closeOnEnd;

  
  private IOException failure = null;  
  
  public StreamRunnable(InputStream in, OutputStream out, StreamProcess proc, boolean closeWhenDone) {
    src = in;
    dest = out;
    processor = proc;
    closeOnEnd = closeWhenDone;
  }
  
  @Override
  public final void run() {
    try {
      processor.process(src, dest);
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
  
  public StreamRunnable startOnNewThread() {
    Thread thread = new Thread(this); //this should be replaced to use thread pools of some sort
    thread.start();
    return this;
  }
  
  
  public static class InPair {
    private StreamRunnable runnable;
    private InputStream inputStream;
    
    public InPair(StreamRunnable runner, InputStream src) {
      runnable = runner;
      inputStream = src;
    }

    public StreamRunnable getRunnable() {
      return runnable;
    }
    
    public InputStream getInputStream() {
      return inputStream;
    }
    
    
  }
  
}
