package sha;

import java.math.BigInteger;
import java.util.Arrays;
import core.Hasher;
import core.CryptoUtils;

public class SHA2_512 extends Hasher {
  
  private static final int BLOCK_BITS = 1024;
  private static final int BLOCK_BYTES = SHA2_512.BLOCK_BITS / 8;
  private static final int MIN_PADDING_BYTES = 17;

  private static final long[] INITIAL_HASHES = new long[] { 0x6a09e667f3bcc908L, 0xbb67ae8584caa73bL,
      0x3c6ef372fe94f82bL, 0xa54ff53a5f1d36f1L, 0x510e527fade682d1L, 0x9b05688c2b3e6c1fL, 0x1f83d9abfb41bd6bL,
      0x5be0cd19137e2179L };

  private static final long[] CONSTANTS = new long[] { 0x428a2f98d728ae22L, 0x7137449123ef65cdL,
      0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL, 0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL,
      0xab1c5ed5da6d8118L, 0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
      0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L, 0xe49b69c19ef14ad2L,
      0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L, 0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L,
      0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L, 0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL,
      0xbf597fc7beef0ee4L, 0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
      0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL, 0x650a73548baf63deL,
      0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL, 0xa2bfe8a14cf10364L, 0xa81a664bbc423001L,
      0xc24b8b70d0f89791L, 0xc76c51a30654be30L, 0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL,
      0x106aa07032bbd1b8L, 0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
      0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L, 0x748f82ee5defb2fcL,
      0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL, 0x90befffa23631e28L, 0xa4506cebde82bde9L,
      0xbef9a3f7b2c67915L, 0xc67178f2e372532bL, 0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL,
      0xf57d4f7fee6ed178L, 0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
      0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL, 0x4cc5d4becb3e42b6L,
      0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L };

  
  private long[] hash;
  
  private long[] W;
  
  public SHA2_512() {
    reset();
  }

  @Override
  public byte[] computeHash() {
    byte[] padded = new byte[SHA2_512.BLOCK_BYTES];
    int copiedLength = toHash.length;
    
    if (copiedLength == 0) {
      fillPadding(padded, 0);
      hashBlock(padded, 0);
      
    } else if ((SHA2_512.BLOCK_BYTES - copiedLength) < SHA2_512.MIN_PADDING_BYTES) {
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
    
    return CryptoUtils.longArrayToByteArray(hash);
  }
  
  private void fillPadding(byte[] padded, int startIndex) {
    padded[startIndex] = CryptoUtils.ONE_AND_SEVEN_ZEROES_BYTE;
    appendWithLength(padded);
  }
  
  private void appendWithLength(byte[] padded) {
    CryptoUtils.fillLastBytes(totalLength.multiply(BigInteger.valueOf(8)).toByteArray(), padded, 16);
  }
  
  protected void hashBlock(byte[] bytes, int start) {
    long a = hash[0];
    long b = hash[1];
    long c = hash[2];
    long d = hash[3];
    long e = hash[4];
    long f = hash[5];
    long g = hash[6];
    long h = hash[7];
    
    long T1;
    long T2;
    for (int t = 0; t < 80; t++) {
      if (t < 16) {
        W[t] = CryptoUtils.longFromBytes(bytes, start + t * 8);
      } else {
        W[t] = CryptoUtils.lowerSigmaOneFiveTwelve(W[t-2]) + W[t-7] + CryptoUtils.lowerSigmaZeroFiveTwelve(W[t-15]) + W[t-16];
      }
      T1 = h + CryptoUtils.upperSigmaOneFiveTwelve(e) + CryptoUtils.longCh(e, f, g) + CONSTANTS[t] + W[t];
      T2 = CryptoUtils.upperSigmaZeroFiveTwelve(a) + CryptoUtils.longMaj(a, b, c);
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
  public Hasher reset() {
    super.reset();
    if (hash == null) {
      hash = Arrays.copyOf(SHA2_512.INITIAL_HASHES, 8);
    } else {
      System.arraycopy(SHA2_512.INITIAL_HASHES, 0, hash, 0, 8);
    }
    
    if (W == null) {
      W = new long[80];
    } else {
      CryptoUtils.fillWithZeroes(W);
    }
    
    return this;
  }

  @Override
  public int getBlockBytes() {
    return SHA2_512.BLOCK_BYTES;
  }

  @Override
  public int getHashByteLength() {
    return 64;
  }
  
  

}
