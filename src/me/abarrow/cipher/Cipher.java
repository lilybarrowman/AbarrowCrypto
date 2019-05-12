package me.abarrow.cipher;

import me.abarrow.core.CryptoException;
import me.abarrow.stream.StreamProcess;

public interface Cipher {
  
  public StreamProcess encrypt();
  public StreamProcess decrypt();
  
  public Cipher setKey(byte[] key) throws CryptoException;
  public boolean hasKey();
  public Cipher removeKey();
  
  public Cipher setIV(byte[] initVector);
  public byte[] getIV();
  public boolean hasIV();
  
  public boolean isIVPrepending();
  public Cipher setIVPrepending(boolean ivPrepending);
}
