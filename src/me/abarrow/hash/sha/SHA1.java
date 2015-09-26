package me.abarrow.hash.sha;

import me.abarrow.core.CryptoUtils;

public class SHA1 extends SHA32Hash {
  
  private static final int BLOCK_BITS = 512;
  private static final int BLOCK_BYTES = SHA1.BLOCK_BITS / 8;
  private static final int[] INITIAL_HASHES = new int[] { 0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476, 0xc3d2e1f0 };
  
  private static final int[] CONSTANTS = new int[] { 0x5a827999, 0x6ed9eba1, 0x8f1bbcdc, 0xca62c1d6 };
  
  protected void hashBlock(byte[] bytes, int start) {
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
  public int getBlockBytes() {
    return SHA1.BLOCK_BYTES;
  }

  @Override
  public int getHashByteLength() {
    return 20;
  }

  @Override
  protected int[] getInitialHashes() {
    return SHA1.INITIAL_HASHES;
  }

  @Override
  protected int getWLength() {
    return 80;
  }

}
