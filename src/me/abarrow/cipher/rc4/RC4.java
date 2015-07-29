package me.abarrow.cipher.rc4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import me.abarrow.cipher.Cipher;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.mac.MAC;
import me.abarrow.stream.StreamRunnable;

public class RC4 implements Cipher {

  private static final int MAX_KEY_LENGTH = 256;
  
  private static final int IV_LENGTH = 16;

  
  private byte[] S;
  
  private boolean prependingIV;
  
  private int drop;
  
  private MAC keyMac;
  
  private byte[] iv;
  
  public RC4(MAC keyMakingMAC) {
    drop = 0;
    prependingIV = keyMakingMAC != null;
    keyMac = keyMakingMAC;
  }
  
  public RC4(MAC keyMakingMAC, byte[] key) {  
    this(keyMakingMAC, key, 0);
  }
  
  public RC4(MAC keyMakingMAC, byte[] key, int bytesToDrop) {
    keyMac = keyMakingMAC;
    setKey(key);
    prependingIV = keyMakingMAC != null;
    drop = bytesToDrop;
  }
  
  private byte[] nextBytes(byte[] bytes) {
    return nextBytes(bytes, 0, bytes.length);
  }
  
  private byte[] nextBytes(byte[] bytes, int start, int length) {
    int i = 0;
    int j = 0;
    int len = start + length;
    for(int n = start; n < len; n ++) {
        i = (i + 1) % 256;
        j = (j + (S[i] & 0xff)) % 256;
        byte temp = S[i];
        S[i] = S[j];
        S[j] = temp;
        bytes[n] = S[((S[i] + S[j]) & 0xff) % 256];
    }
    return bytes;
  }

  @Override
  public byte[] encrypt(byte[] input) throws CryptoException {
    return encryptCore(input, true);
  }

  private byte[] encryptCore(byte[] input, boolean encrypting) throws CryptoException {
    byte[] output = null;
    int outputOffset = 0;
    if (prependingIV) {
      if (encrypting) {
        if (iv == null) {
          throw new CryptoException(CryptoException.NO_IV);
        }
        output = new byte[input.length + IV_LENGTH];
        System.arraycopy(iv, 0, output, 0, IV_LENGTH);
        outputOffset = IV_LENGTH;
      } else {
        //TODO
      }
    } else {
      output = new byte[input.length];
    }
    
    
    createAbsoluteKeyAsNeeded();
    CryptoUtils.fillWithZeroes(nextBytes(new byte[drop]));
    nextBytes(output, outputOffset, output.length - outputOffset);
    CryptoUtils.xorByteArrays(input, 0, output, outputOffset, output, outputOffset, input.length);
    removeAbsoluteKey();
    return output;
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    return encryptCore(input, false);
  }

  private void createAbsoluteKeyAsNeeded() throws CryptoException {
    if (keyMac == null) {
      if (!hasKey()) {
        throw new CryptoException(CryptoException.NO_KEY);
      }
    } else {
      if (keyMac.hasMACKey()) {
        if (hasIV()) {
          setAbsoluteKey(keyMac.tag(iv, true));
        } else {
          throw new CryptoException(CryptoException.NO_IV);
        }
      } else {
        throw new CryptoException(CryptoException.NO_KEY);
      }
    }
  }
  
  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        try {
          createAbsoluteKeyAsNeeded();
        } catch (CryptoException e) {
          throw new IOException(e);
        }
        CryptoUtils.fillWithZeroes(nextBytes(new byte[drop]));
        int blockSize = 16;
        byte[] block = new byte[blockSize];
        byte[] output = new byte[blockSize];
        while (true) {
          int read = in.read(block);
          if (read == -1) {
            break;
          }
          if (read == blockSize) {
            nextBytes(output);
          } else {
            CryptoUtils.fillWithZeroes(output);
            output = new byte[read];
            nextBytes(output);
          }
          CryptoUtils.xorByteArrays(output, 0, block, 0, output, 0, read);
          out.write(output, 0, read);
        }
        CryptoUtils.fillWithZeroes(output);
        CryptoUtils.fillWithZeroes(block);
        removeAbsoluteKey();
      }
    };
  }

  @Override
  public StreamRunnable decrypt() {
    return encrypt();
  }
  
  private void setAbsoluteKey(byte[] key) {
    removeAbsoluteKey();
    S = new byte[RC4.MAX_KEY_LENGTH];
    int i;
    
    for (i = 0; i < S.length; i++) {
      S[i] = (byte)i;
    }
    
    int j =0;
    byte temp;
    
    for (i = 0; i < S.length; i++) {
      j = (j + (S[i] & 0xff) + (key[i % key.length] & 0xff)) % 256;
      temp = S[i];
      S[i] = S[j];
      S[j] = temp;
    }
  }

  @Override
  public Cipher setKey(byte[] key) {
    if (keyMac == null) {
      setAbsoluteKey(key);
    } else {
      removeAbsoluteKey();
      keyMac.setMACKey(key);
    }
    return this;
  }

  @Override
  public Cipher removeKey() {
    if (keyMac != null) {
      keyMac.removeMACKey();
    }
    removeAbsoluteKey();
    return this;
  }
  
  private void removeAbsoluteKey() {
    if (S == null) {
      return;
    }
    CryptoUtils.fillWithZeroes(S);
    S = null;
  }

  @Override
  public boolean hasKey() {
    if (keyMac == null) {
      return S != null;
    } else {
      return keyMac.hasMACKey();
    }
  }

  @Override
  public Cipher setIV(byte[] initVector) {
    if (keyMac == null) {
      //maybe throw an exception
    } else {
      iv = Arrays.copyOf(initVector, RC4.IV_LENGTH);
    }
    return this;
  }
  
  @Override
  public boolean hasIV() {
    return iv != null;
  }

  @Override
  public byte[] getIV() {
    return iv;
  }

  @Override
  public boolean isIVPrepending() {
    return prependingIV;
  }

  @Override
  public Cipher setIVPrepending(boolean prepend) {
    prependingIV = prepend;
    return this;
  }
  
}
