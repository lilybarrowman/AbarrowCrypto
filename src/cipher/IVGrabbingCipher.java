package cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import stream.StreamRunnable;
import core.CryptoException;
import core.CryptoUtils;

public class IVGrabbingCipher implements Cipher {
  
  private Cipher cipher;
  
  private int blockSize;
  
  public IVGrabbingCipher(Cipher c) {
    cipher = c;
    blockSize = cipher.getBlockBytes();
  }

  @Override
  public byte[] encrypt(byte[] input) throws CryptoException {
    byte[] iv = cipher.getIV();
    if (iv == null) {
      return cipher.encrypt(input);
    }
    byte[] preOut = cipher.encrypt(input);
    byte[] out = Arrays.copyOf(iv, input.length + blockSize);
    System.arraycopy(preOut, 0, out, blockSize, input.length);
    CryptoUtils.fillWithZeroes(preOut);
    return out;
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    byte[] iv = Arrays.copyOf(input, blockSize);
    if(!cipher.setIV(iv)) {
      return cipher.encrypt(input);
    }
    byte[] truncIn = new byte[input.length - blockSize];
    System.arraycopy(input, blockSize, truncIn, 0, truncIn.length);
    byte[] output = cipher.decrypt(truncIn);
    CryptoUtils.fillWithZeroes(truncIn);
    return output;
  }

  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        byte[] iv = cipher.getIV();
        if (iv != null) {
          out.write(iv);
        }
        cipher.encrypt().start(in, out);
      }
    };
  }

  @Override
  public StreamRunnable decrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        byte[] iv = new byte[blockSize];
        int read = in.read(iv);
        if (read == -1) {
          throw new IOException("Cannot read IV.");
        }
        if (!cipher.setIV(iv)) {
          out.write(iv, 0, read);
        }
        cipher.decrypt().start(in, out);
      }
    };
  }

  @Override
  public void setKey(byte[] key) {
    cipher.setKey(key);
  }

  @Override
  public boolean hasKey() {
    return cipher.hasKey();
  }

  @Override
  public void removeKey() {
    cipher.removeKey();
  }

  @Override
  public boolean setIV(byte[] initVector) {
    return cipher.setIV(initVector);
  }

  @Override
  public boolean hasIV() {
    return cipher.hasIV();
  }

  @Override
  public int getBlockBytes() {
    return cipher.getBlockBytes();
  }

  @Override
  public byte[] getIV() {
    return cipher.getIV();
  }

}
