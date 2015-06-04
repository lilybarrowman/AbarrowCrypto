package padding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import core.CryptoException;
import stream.StreamRunnable;
import stream.StreamUtils;

public class PKCS7 extends Padding {
  
  public PKCS7() { 
    super();
  }
  
  public PKCS7(int bSize) { 
    super(bSize);
  }

  @Override
  public byte[] pad(byte[] input) {
    if (blockSize == 0) {
      return input;
    }
    byte[] output = new byte[input.length + blockSize - (input.length % blockSize)];
    System.arraycopy(input, 0, output, 0, input.length);
    byte diff = (byte) (output.length - input.length);
    for (int n = input.length, end = output.length; n < end; n++) {
      output[n] = diff;
    }
    return output;
  }

  @Override
  public byte[] unpad(byte[] input) {
    int diff = input[input.length - 1] & 0xff;
    byte[] output = new byte[input.length - diff];
    System.arraycopy(input, 0, output, 0, output.length);
    return output;
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
            Arrays.fill(block, (byte) blockSize);
            out.write(block);
            break;
          } else {
            Arrays.fill(block, read, blockSize, (byte) (blockSize - read));
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
        if (blockSize == 0) {
          StreamUtils.copyStream(in, out);
          return;
        }
        byte[] block = new byte[blockSize];
        byte[] oldBlock = new byte[blockSize];
        byte[] swap;
        boolean hasReadBlock = false;
        while (true) {
          int read = in.read(block);
          if (read == blockSize) {
            if (hasReadBlock) {
              out.write(oldBlock);
            }
            hasReadBlock = true;
            swap = block;
            block = oldBlock;
            oldBlock = swap;
            continue;
          }
          if (read != -1) {
            throw new IOException(new CryptoException(CryptoException.INVALID_LENGTH));
          }
          // padded block
          int drop = oldBlock[blockSize - 1] & 0xff;
          drop = (drop > blockSize) ? 0 : drop;
          out.write(oldBlock, 0, (byte) (blockSize - drop));
          break;
        }
      }
    };
  }
}
