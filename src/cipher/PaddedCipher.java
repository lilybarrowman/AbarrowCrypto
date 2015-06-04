package cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import core.CryptoException;
import padding.Padding;
import stream.StreamRunnable;

public class PaddedCipher implements Cipher {

  private Cipher cipher;
  private Padding padding;

  public PaddedCipher(Cipher c, Padding p) {
    cipher = c;
    padding = p;
    padding.setBlockSize(cipher.getBlockBytes());
  }

  @Override
  public byte[] encrypt(byte[] input) throws CryptoException {
    return cipher.encrypt(padding.pad(input));
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    return padding.unpad(cipher.decrypt(input));
  }

  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        cipher.encrypt().start(padding.pad().start(in), out);
      }
    };
  }

  @Override
  public StreamRunnable decrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
          padding.unpad().start(cipher.decrypt().start(in), out);
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
  public int getBlockBytes() {
    return cipher.getBlockBytes();
  }

  @Override
  public boolean hasIV() {
    return cipher.hasIV();
  }

  @Override
  public byte[] getIV() {
    return cipher.getIV();
  }

}
