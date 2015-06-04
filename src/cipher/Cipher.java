package cipher;

import core.CryptoException;
import stream.StreamRunnable;

public interface Cipher {
  
  public byte[] encrypt(byte[] input) throws CryptoException;
  public byte[] decrypt(byte[] input) throws CryptoException;
  
  public StreamRunnable encrypt();
  public StreamRunnable decrypt();
  
  public void setKey(byte[] key);
  public boolean hasKey();
  public void removeKey();
  
  public void setIV(byte[] initVector);
  public boolean hasIV();
  
  public int getBlockBytes();
}
