package me.abarrow.padding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import me.abarrow.core.CryptoException;
import me.abarrow.stream.StreamRunnable;
import me.abarrow.stream.StreamUtils;

public class PKCS7 extends Padding {
  
  public PKCS7() { 
    super();
  }
  
  public PKCS7(int bSize) { 
    super(bSize);
  }

  @Override
  public byte[] pad(byte[] input, int start, int len) {
    if (blockSize == 0) {
      return input;
    }
    byte[] output = new byte[len + blockSize - (len % blockSize)];
    System.arraycopy(input, start, output, 0, len);
    byte diff = (byte) (output.length - len);
    for (int n = input.length, end = output.length; n < end; n++) {
      output[n] = diff;
    }
    return output;
  }

  @Override
  public byte[] unpad(byte[] input, int start, int len) {
    int diff = input[start + len - 1] & 0xff;
    byte[] output = new byte[len - diff];
    System.arraycopy(input, start, output, 0, output.length);
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
