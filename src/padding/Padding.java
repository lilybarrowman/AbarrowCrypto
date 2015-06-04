package padding;

import java.io.IOException;
import stream.StreamRunnable;

public abstract class Padding {
  protected int blockSize;
  
  public Padding() { 
    blockSize = 0;
  }
  
  public Padding(int bSize) { 
    blockSize = bSize;
  }
  
  public Padding setBlockSize(int bSize) {
    blockSize = bSize;
    return this;
  }
  
  public abstract byte[] pad(byte[] input);
  public abstract byte[] unpad(byte[] input);
  public abstract StreamRunnable pad() throws IOException;
  public abstract StreamRunnable unpad() throws IOException;
}
