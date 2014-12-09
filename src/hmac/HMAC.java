package hmac;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import core.Hasher;
import core.CryptoUtils;

public class HMAC {
  
  private static final byte O_PAD_BYTE = 0x5c;
  private static final byte I_PAD_BYTE = 0x36;

  
  private int blockBytes;
  
  private Hasher hasher;
  
  public HMAC(Hasher hashMaker) {
    hasher = hashMaker;
    blockBytes = hasher.getBlockBytes();
  }
  
  public byte[] computeHash(byte[] key, byte[] message) {
    hasher.reset();
    
    if (key.length > blockBytes) {
      key = hasher.addBytes(key).computeHash();
      hasher.reset();
    }
    
    byte[] padded = new byte[blockBytes];
    
    if (key.length < blockBytes) {
      //right pad with zereos
      System.arraycopy(key, 0, padded, 0, key.length);
      key = padded;
    }
    
    byte[] padKey = new byte[blockBytes];
    Arrays.fill(padKey, I_PAD_BYTE);
    padKey = CryptoUtils.xorByteArrays(padKey, key);
    
    padded = hasher.addBytes(ArrayUtils.addAll(padKey, message)).computeHash();
    hasher.reset();
    
    Arrays.fill(padKey, O_PAD_BYTE);
    padKey = CryptoUtils.xorByteArrays(padKey, key);
    
    return hasher.addBytes(ArrayUtils.addAll(padKey, padded)).computeHash();
  }
  
  public String computeHashString(byte[] key, byte[] message) {
    return CryptoUtils.byteArrayToHexString(computeHash(key, message));
  }
  
  public String computeHashString(String key, String message) {
    return CryptoUtils.byteArrayToHexString(computeHash(key.getBytes(), message.getBytes()));
  }

}
