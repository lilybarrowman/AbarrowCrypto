package me.abarrow.hash;

import java.math.BigInteger;

import me.abarrow.core.CryptoUtils;

public abstract class Hasher {
  
  
  protected BigInteger totalLength;
  protected byte[] toHash;
  protected int toHashPos;
  
  public Hasher() {
    reset();
  }  
  
  public Hasher addBytes(byte[] bytes) {
    return addBytes(bytes, 0, bytes.length);
  }
    
  public Hasher addBytes(byte[] bytes, int start, int length) {
    int blockBytes = getBlockBytes();
    totalLength = totalLength.add(BigInteger.valueOf(length));
    
    if (length >= blockBytes - toHashPos) {
      
      System.arraycopy(bytes, start, toHash, toHashPos, blockBytes - toHashPos);
      hashBlock(toHash, 0);
      CryptoUtils.fillWithZeroes(toHash);
      int startPos = blockBytes - toHashPos + start; 
      int end = start + length;
      while (end - startPos >= blockBytes) {
        hashBlock(bytes, startPos);
        startPos += blockBytes;
      }
      toHashPos = end - startPos;
      System.arraycopy(bytes, startPos, toHash, 0, toHashPos);
    } else {
      System.arraycopy(bytes, start, toHash, toHashPos, length);
      toHashPos += length;
    }
    
    return this;
  }
  
  protected abstract void hashBlock(byte[] data, int srcPos);
  
  public abstract byte[] computeHash(byte[] out, int start);
  
  public final byte[] computeHash() {
    return computeHash(new byte[getHashByteLength()], 0);
  }

  
  protected void reset() {
    if (toHash == null){
       toHash = new byte[256];
    } else {
      //security paranoia
      CryptoUtils.fillWithZeroes(toHash);
    }
    toHashPos = 0;
    totalLength = BigInteger.ZERO;
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
