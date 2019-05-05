package me.abarrow.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.abarrow.core.CryptoUtils;

public abstract class StreamProcess {
  
  public abstract void process(InputStream in, OutputStream out) throws IOException;
  
  public final SyncByteProcess createSyncByteProcess() {
    return new SyncByteProcess(this);
  }
  
  public final AsyncByteProcess createAsyncByteProcess() {
    return new AsyncByteProcess(this);
  }
  
  /**
   * This can potentially use up a crazy high amount of RAM, since it buffers the entire output before returning,
   * so be careful what input streams you use it with.
   * @param in
   * @return 
   * @throws IOException
   */
  public final InputStream runSync(InputStream in) throws IOException {
    DynamicByteQueue q = new DynamicByteQueue();
    runSync(in, q.getOutputStream(), true);
    return q.getInputStream();
  }
  
  public final byte[] runSync(byte[] input) throws IOException {
    DirectByteOutputStream out = new DirectByteOutputStream();
    process(new ByteArrayInputStream(input), out);
    out.close();
    byte[] output = out.toByteArray();
    CryptoUtils.fillWithZeroes(out.getBuffer());
    return output;
  }

  public final OutputStream runSync(InputStream in, OutputStream out) throws IOException {
    return runSync(in, out, true);
  }
  
  public final OutputStream runSync(InputStream in, OutputStream out, boolean closeWhenDone) throws IOException {
    try {
    process(in, out);
    } finally {
      StreamUtils.quitelyClose(in);
      if (closeWhenDone) {
        StreamUtils.quitelyClose(out);
      }
    }
    return out;
  }
  
  public final StreamRunnable startAsync(InputStream in, OutputStream out) {
    return startAsync(in, out, true);
  }
  
  public final StreamRunnable startAsync(InputStream in, OutputStream out, boolean closeWhenDone) {
    return new StreamRunnable(in, out, this, closeWhenDone).startOnNewThread();
  }
  
  public final StreamRunnable.InPair startAsync(InputStream in) {
    DynamicByteQueue q = new DynamicByteQueue();
    OutputStream qOut = q.getOutputStream();
    InputStream qIn = q.getInputStream();
    
    StreamRunnable run = new StreamRunnable(in, qOut, this, true);
    run.startOnNewThread();
    
    return new StreamRunnable.InPair(run, qIn);
  }
}
