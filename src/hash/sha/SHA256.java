package hash.sha;

import java.math.BigInteger;
import java.util.Arrays;

import core.CryptoUtils;
import hash.Hasher;

public class SHA256 extends Hasher {
  
  private static final int[] CONSTANTS = new int[]{
    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
    0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
    0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
    0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
    0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
  };
  
  private static final int[] INITIAL_HASHES = new int[]{
    0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
  };
  
  private static final int MIN_PADDING_BYTES = 9;
  
  private static final int BLOCK_BYTES = 64;

  byte[] padded;
  private int[] hash;
  private int[] W;
  
  @Override
  protected void hashBlock(byte[] data, int srcPos) {
    int a = hash[0];
    int b = hash[1];
    int c = hash[2];
    int d = hash[3];
    int e = hash[4];
    int f = hash[5];
    int g = hash[6];
    int h = hash[7];
    for (int t = 0; t < 64; t++) {
      if (t < 16) {
        W[t] = CryptoUtils.intFromBytes(data, srcPos + t * 4);
      } else {
        W[t] = CryptoUtils.lowerSigmaOneTwoFiftySix(W[t -2]) + W[t- 7] +
            CryptoUtils.lowerSigmaZeroTwoFiftySix(W[t - 15]) + W[t -16];
      }
      int T1 = h + CryptoUtils.upperSigmaOneTwoFiftySix(e) + CryptoUtils.intCh(e, f, g) + SHA256.CONSTANTS[t] + W[t];
      int T2 = CryptoUtils.upperSigmaZeroTwoFiftySix(a) + CryptoUtils.intMaj(a, b, c);
      h = g;
      g = f;
      f = e;
      e = d + T1;
      d = c;
      c = b;
      b = a;
      a = T1 + T2;
    }
    hash[0] = a + hash[0];
    hash[1] = b + hash[1];
    hash[2] = c + hash[2];
    hash[3] = d + hash[3];
    hash[4] = e + hash[4];
    hash[5] = f + hash[5];
    hash[6] = g + hash[6];
    hash[7] = h + hash[7];
  }

  @Override
  public byte[] computeHash(byte[] out, int start) {
    
    int copiedLength = toHashPos;
    if (copiedLength == 0) {
      fillPadding(padded, 0);
      hashBlock(padded, 0);
    } else if ((SHA256.BLOCK_BYTES - copiedLength) < SHA256.MIN_PADDING_BYTES) {
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
    CryptoUtils.fillWithZeroes(padded);
    byte[] result = CryptoUtils.intArrayToByteArray(out, start, hash, false);
    reset();
    return result;
  }

  private void fillPadding(byte[] padded, int startIndex) {
    padded[startIndex] = CryptoUtils.ONE_AND_SEVEN_ZEROES_BYTE;
    appendWithLength(padded);
  }
  
  private void appendWithLength(byte[] padded) {
    CryptoUtils.longToBytes(totalLength.multiply(BigInteger.valueOf(8)).longValue(), padded, padded.length - 8);
  }
  
  @Override
  public int getBlockBytes() {
    return SHA256.BLOCK_BYTES;
  }

  @Override
  public int getHashByteLength() {
    return 32;
  }
  
  @Override
  protected void reset() {
    super.reset();
    if (hash == null) {
      hash = Arrays.copyOf(SHA256.INITIAL_HASHES, 8);
      W = new int[64];
      padded = new byte[SHA256.BLOCK_BYTES];
    } else {
      System.arraycopy(SHA256.INITIAL_HASHES, 0, hash, 0, 8);
      CryptoUtils.fillWithZeroes(W);
      CryptoUtils.fillWithZeroes(padded);
    }
  }

}
