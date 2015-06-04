package cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import stream.StreamRunnable;
import core.CryptoException;
import mac.MAC;

public class AuthenticatedCipher implements Cipher {
  
  private Cipher cipher;
  private MAC mac;
  
  public AuthenticatedCipher(Cipher c, MAC m) {
    cipher = c;
    mac = m;
  }

  @Override
  public byte[] encrypt(byte[] input) throws CryptoException {
    return mac.tag(cipher.encrypt(input), false);
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    return cipher.decrypt(mac.unTag(input, false));
  }

  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        mac.tag(false).start(cipher.encrypt().start(in), out);
      }
    };
  }

  @Override
  public StreamRunnable decrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        cipher.decrypt().start(mac.unTag(false).start(in), out);
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
  public void setIV(byte[] initVector) {
    cipher.setIV(initVector);
  }

  @Override
  public int getBlockBytes() {
    return cipher.getBlockBytes();
  }

  @Override
  public boolean hasIV() {
    return cipher.hasIV();
  }
}
