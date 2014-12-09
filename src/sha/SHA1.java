package sha;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import core.Hasher;
import core.CryptoUtils;

public class SHA1 extends Hasher {
  
  private static final int BLOCK_BITS = 512;
  private static final int BLOCK_BYTES = SHA1.BLOCK_BITS / 8;
  private static final int MIN_PADDING_BYTES = 9;
  private static final int[] INITIAL_HASHES = new int[] { 0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476, 0xc3d2e1f0 };
  
  private static final int[] CONSTANTS = new int[] { 0x5a827999, 0x6ed9eba1, 0x8f1bbcdc, 0xca62c1d6 };
  
  private int[] hash;
  private int[] W;
  
  private byte[] toHash;
  
  private long totalLength;
  
  
  public SHA1(){
    reset();
  }

  @Override
  public Hasher addBytes(byte[] bytes) {
    
    if (bytes == null) {
      throw new IllegalArgumentException("SHA1 cannot hash a null array.");
    }
    
    totalLength += bytes.length;
    
    toHash = ArrayUtils.addAll(toHash, bytes);
    
    int i;
    
    for (i = 0; i + SHA1.BLOCK_BYTES <= toHash.length; i += SHA1.BLOCK_BYTES) {
      hashBlock(toHash, i);
    }
        
    if (i > 0 ) {
      toHash = ArrayUtils.subarray(toHash, i, toHash.length);
    }
    
    return this;
  }

  @Override
  public byte[] computeHash() {
    byte[] padded = new byte[SHA1.BLOCK_BYTES];
    
    int copiedLength = toHash.length;
    
    if (copiedLength == 0) {
      fillPadding(padded, 0);
      hashBlock(padded, 0);
    } else if ((SHA1.BLOCK_BYTES - copiedLength) < SHA1.MIN_PADDING_BYTES) {
      System.arraycopy(toHash, 0, padded, 0, copiedLength);
      padded[copiedLength] = CryptoUtils.ONE_AND_SEVEN_ZEROES_BYTE;
      hashBlock(padded, 0);
                
      Arrays.fill(padded, 0, padded.length, (byte)0);
      appendWithLength(padded);
      hashBlock(padded, 0);
    } else {
      System.arraycopy(toHash, 0, padded, 0, copiedLength);
      fillPadding(padded, copiedLength);
      hashBlock(padded, 0);
    }
    
    return CryptoUtils.intArrayToByteArray(hash);
  }
  
  private void fillPadding(byte[] padded, int startIndex) {
    padded[startIndex] = CryptoUtils.ONE_AND_SEVEN_ZEROES_BYTE;
    appendWithLength(padded);
  }
  
  private void appendWithLength(byte[] padded) {
    CryptoUtils.longToBytes(totalLength * 8L, padded, padded.length - 8);
  }
  
  private void hashBlock(byte[] bytes, int start) {
    int a = hash[0];
    int b = hash[1];
    int c = hash[2];
    int d = hash[3];
    int e = hash[4];
    int T;
    for (int t = 0; t < 80; t++) {
      if (t < 16) {
        W[t] = CryptoUtils.intFromBytes(bytes, start + t * 4);
      } else {
        W[t] = CryptoUtils.rotateIntLeft(W[t - 3] ^ W[t - 8] ^ W[t - 14] ^ W[t - 16], 1);
      }
      T = CryptoUtils.rotateIntLeft(a, 5) + SHA1Func(b, c, d, t) + e + CONSTANTS[t / 20] + W[t];
      e = d;
      d = c;
      c = CryptoUtils.rotateIntLeft(b, 30);
      b = a;
      a = T;
    }

    hash[0] = a + hash[0];
    hash[1] = b + hash[1];
    hash[2] = c + hash[2];
    hash[3] = d + hash[3];
    hash[4] = e + hash[4];
  }
  
  private int SHA1Func(int x, int y, int z, int index) {
    if (index < 20) {
      return CryptoUtils.intCh(x, y, z);
    } else if (index < 40) {
      return CryptoUtils.intParity(x, y, z);
    } else if (index < 60) {
      return CryptoUtils.intMaj(x, y, z);
    } else if (index < 80) {
      return CryptoUtils.intParity(x, y, z);
    } else {
      throw new IllegalArgumentException("SHA-1's sequence of logical functions only has 80 members.");
    }
  }

  @Override
  public Hasher reset() {
    hash = Arrays.copyOf(SHA1.INITIAL_HASHES, 5);
    W = new int[80];
    toHash = new byte[0];
    totalLength = 0;
    return this;
  }
  
  @Override
  public int getBlockBytes() {
    return SHA1.BLOCK_BYTES;
  }

  @Override
  public int getHashByteLength() {
    return 20;
  }

}
