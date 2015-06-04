package padding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import core.CryptoUtils;
import stream.StreamRunnable;
import stream.StreamUtils;

public class ZeroPadding extends Padding {
  
  public ZeroPadding() { 
    super();
  }
  
  public ZeroPadding(int bSize) { 
    super(bSize);
  }
  
  @Override
  public byte[] pad(byte[] input) {
    if (blockSize == 0) {
      return input;
    }
    int paddedLength = ((input.length + blockSize - 1) / blockSize) * blockSize;
    byte[] padded = new byte[paddedLength];
    System.arraycopy(input, 0, padded, 0, input.length);
    return padded;
  }

  @Override
  public byte[] unpad(byte[] input) {
    return input;
  }

  @Override
  public StreamRunnable pad() throws IOException {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (blockSize == 0) {
          StreamUtils.copyStream(in, out);
          return;
        }
        byte[] block = new byte[blockSize];
        while (true) {
          int read = in.read(block);
          if (read == blockSize) {
            out.write(block);
            continue;
          } else if (read == -1) {
            break;
          } else {
            Arrays.fill(block, read, blockSize, CryptoUtils.ZERO_BYTE);
            out.write(block);
            break;
          }
        }
      }
    };
  }

  @Override
  public StreamRunnable unpad() throws IOException {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        StreamUtils.copyStream(in, out);
      }
    };
  }

}
