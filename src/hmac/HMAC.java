package hmac;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import core.Hasher;
import core.CryptoUtils;

public class HMAC {
  
  private static final byte O_PAD_BYTE = 0x5c;
  private static final byte I_PAD_BYTE = 0x36;

  
  private int blockBytes;
  private int hashByteLength;
  
  private Hasher hasher;
  
  private byte[] padded;
  private byte[] padKey;
  private byte[] padKeyHash;
  
  public HMAC(Hasher hashMaker) {
    hasher = hashMaker;
    blockBytes = hasher.getBlockBytes();
    hashByteLength = hasher.getHashByteLength();
    padded = new byte[blockBytes];
    padKey = new byte[blockBytes];
    padKeyHash = new byte[hashByteLength];
  }
  
  public byte[] computeHash(byte[] key, byte[] message) {
    return computeHash(key, message, new byte[hashByteLength], 0);
  }
  
  public byte[] computeHash(byte[] key, byte[] message, byte[] out, int start) {
    hasher.reset();
    
    if (key.length > blockBytes) {
      key = hasher.addBytes(key).computeHash();
      hasher.reset();
    }
        
    if (key.length < blockBytes) {
      //right pad with zereos
      System.arraycopy(key, 0, padded, 0, key.length);
      key = padded;
    }
    
   
    Arrays.fill(padKey, I_PAD_BYTE);
    CryptoUtils.xorByteArrays(padKey, key, padKey);
    
    hasher.addBytes(padKey).addBytes(message).computeHash(padKeyHash, 0);
    hasher.reset();
    
    Arrays.fill(padKey, O_PAD_BYTE);
    CryptoUtils.xorByteArrays(padKey, key, padKey);
    
    hasher.addBytes(padKey).addBytes(padKeyHash).computeHash(out, start);
    
    CryptoUtils.fillWithZeroes(padded);
    CryptoUtils.fillWithZeroes(padKey);
    hasher.reset();
    
    return out;
  }
  public boolean checkHash(byte[] key, byte[] message, byte[] hmac) {
    return Arrays.equals(computeHash(key, message), hmac);
  }

  
  public String computeHashString(byte[] key, byte[] message) {
    return CryptoUtils.byteArrayToHexString(computeHash(key, message));
  }
  
  public String computeHashString(String key, String message) {
    return CryptoUtils.byteArrayToHexString(computeHash(key.getBytes(), message.getBytes()));
  }
  
  public int getHMACByteLength () {
    return hashByteLength;
  }

}
