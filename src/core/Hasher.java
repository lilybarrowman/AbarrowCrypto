package core;

import java.math.BigInteger;

public abstract class Hasher {
  
  
  protected BigInteger totalLength;
  protected byte[] toHash;
  protected int toHashPos;
  
  public Hasher() {
    reset();
  }  
    
  public Hasher addBytes(byte[] bytes) {
    int blockBytes = getBlockBytes();
    if (bytes == null) {
      throw new IllegalArgumentException("Cannot hash a null array.");
    }
    
    if (bytes.length > 0) {
      
      totalLength = totalLength.add(BigInteger.valueOf(bytes.length));
      
      if (bytes.length >= blockBytes - toHashPos) {
        
        System.arraycopy(bytes, 0, toHash, toHashPos, blockBytes - toHashPos);
        hashBlock(toHash, 0);
        CryptoUtils.fillWithZeroes(toHash);
        int startPos = getBlockBytes() - toHashPos;
        
        while (bytes.length - startPos >= blockBytes) {
          hashBlock(bytes, startPos);
          startPos += blockBytes;
        }
        toHashPos = bytes.length - startPos;
        System.arraycopy(bytes, startPos, toHash, 0, toHashPos);
      } else {
        System.arraycopy(bytes, 0, toHash, toHashPos, bytes.length);
        toHashPos += bytes.length;
      }
    }
    
    return this;
  }
  
  protected abstract void hashBlock(byte[] data, int srcPos);
  
  public abstract byte[] computeHash(byte[] out, int start);
  
  public final byte[] computeHash() {
    return computeHash(new byte[getHashByteLength()], 0);
  }

  
  public Hasher reset() {
    if (toHash == null){
       toHash = new byte[256];
    } else {
      //security paranoia
      CryptoUtils.fillWithZeroes(toHash);
    }
    toHashPos = 0;
    totalLength = BigInteger.ZERO;
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
