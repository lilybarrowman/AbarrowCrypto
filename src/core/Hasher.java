package core;

import org.apache.commons.lang.ArrayUtils;

public abstract class Hasher {
  
  
  protected int totalLength;
  protected byte[] toHash;
  
  public Hasher() {
    reset();
  }  
    
  public Hasher addBytes(byte[] bytes) {
    if (bytes == null) {
      throw new IllegalArgumentException("Cannot hash a null array.");
    }
    
    if (bytes.length > 0) {
      
      totalLength += bytes.length;
      
      toHash = ArrayUtils.addAll(toHash, bytes);
      
      int i;
      
      for (i = 0; i + getBlockBytes() <= toHash.length; i += getBlockBytes()) {
        hashBlock(toHash, i);
      }
          
      if (i > 0 ) {
        toHash = ArrayUtils.subarray(toHash, i, toHash.length);
      }
    }
    
    return this;
  }
  
  protected abstract void hashBlock(byte[] data, int index);
  
  public abstract byte[] computeHash();
  
  public Hasher reset() {
    if (toHash != null){
      //security paranoia
      CryptoUtils.fillWithZeroes(toHash);
    }
    toHash = new byte[0];
    totalLength = 0;
    return this;
  }
  
  public abstract int getBlockBytes();
  
  public abstract int getHashByteLength();
  
  public final Hasher addString(String string) {
    addBytes(string.getBytes());
    return this;
  }
  
  public String computeHashString() {
    return CryptoUtils.byteArrayToHexString(computeHash());
  }

}
