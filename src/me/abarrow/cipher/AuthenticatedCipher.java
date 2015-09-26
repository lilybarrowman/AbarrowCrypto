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
        StreamRunnable encrypt = cipher.encrypt();
        mac.tag(false).start(encrypt.start(in), out);
        encrypt.throwIfFailed();
      }
    };
  }

  @Override
  public StreamRunnable decrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        StreamRunnable checkTag = mac.checkTag(false);
        cipher.decrypt().start(checkTag.start(in), out);
        checkTag.throwIfFailed();
      }
    };
  }

  @Override
  public Cipher setKey(byte[] key) throws CryptoException {
    cipher.setKey(key);
    return this;
  }

  @Override
  public boolean hasKey() {
    return cipher.hasKey();
  }

  @Override
  public Cipher removeKey() {
    cipher.removeKey();
    return this;
  }
  
  public AuthenticatedCipher setKeys(byte[] cipherKey, byte[] macKey) throws CryptoException {
    cipher.setKey(cipherKey);
    mac.setMACKey(macKey);
    return this;
  }
  
  public AuthenticatedCipher removeKeys() {
    removeKey();
    removeMACKey();
    return this;
  }
  
  public AuthenticatedCipher setMACKey(byte[] key) throws CryptoException {
    mac.setMACKey(key);
    return this;
  }
  
  public boolean hasMACKey() {
    return mac.hasMACKey();
  }

  public AuthenticatedCipher removeMACKey() {
    mac.removeMACKey();
    return this;
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
