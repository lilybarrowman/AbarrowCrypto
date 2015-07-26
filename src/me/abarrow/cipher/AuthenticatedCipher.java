package me.abarrow.cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.abarrow.core.CryptoException;
import me.abarrow.mac.MAC;
import me.abarrow.stream.StreamRunnable;

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
    return cipher.decrypt(mac.checkTag(input, false));
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
        cipher.decrypt().start(mac.checkTag(false).start(in), out);
      }
    };
  }

  @Override
  public Cipher setKey(byte[] key) {
    cipher.setKey(key);
    return this;
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
  public Cipher setIV(byte[] initVector) {
    cipher.setIV(initVector);
    return this;
  }

  @Override
  public boolean hasIV() {
    return cipher.hasIV();
  }

  @Override
  public byte[] getIV() {
    return cipher.getIV();
  }

  @Override
  public boolean isIVPrepending() {
    return cipher.isIVPrepending();
  }

  @Override
  public Cipher setIVPrepending(boolean ivPrepending) {
    cipher.setIVPrepending(ivPrepending);
    return this;
  }
}
