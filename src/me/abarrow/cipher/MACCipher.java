package me.abarrow.cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.abarrow.core.CryptoException;
import me.abarrow.mac.MAC;
import me.abarrow.stream.StreamRunnable;

public class MACCipher implements AuthenticatedCipher {
  
  private Cipher cipher;
  private MAC mac;
  
  public MACCipher(Cipher c, MAC m) {
    cipher = c;
    mac = m;
  }

  @Override
  public final StreamRunnable encrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        StreamRunnable encrypt = cipher.encrypt();
        mac.tag(false).runSync(encrypt.startAsync(in), out);
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
        cipher.decrypt().runSync(checkTag.startAsync(in), out);
        checkTag.throwIfFailed();
      }
    };
  }
  
  public Cipher getCipher() {
    return cipher;
  }
  
  public MAC getMac() {
    return mac;
  }
  
  @Override
  public Cipher setKey(byte[] key) throws CryptoException {
    throw new UnsupportedOperationException("AuthenticatedCiphers do not support Cipher::setKey");
  }

  @Override
  public boolean hasKey() {
    return cipher.hasKey() && mac.hasKey();
  }

  @Override
  public Cipher removeKey() {
    cipher.removeKey();
    mac.removeKey();
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
