package mac.gcmac;

import core.CryptoUtils;

public class GCMMAC {
  
  public static final int BLOCK_SIZE = 16;
  private static final byte[] TRUNC_POLY = new byte[BLOCK_SIZE];
  static {
    GCMMAC.TRUNC_POLY[0] = (byte) 0xe1;
  }
  
  private byte[] key;
  private byte[] spare;
  
  public GCMMAC(byte[] hashKey) {
    key = hashKey;
    spare = new byte[GCMMAC.BLOCK_SIZE];
  }

  public byte[] plainAuthHash(byte[] plainAuth) {
    byte[] hash = new byte[GCMMAC.BLOCK_SIZE];
    return hashData(hash, plainAuth);
  }
  
  public byte[] hashData(byte[] hash, byte[] data) {
    int m = (data.length + GCMMAC.BLOCK_SIZE - 1) / GCMMAC.BLOCK_SIZE; 
    int i;
    for (i = 1; i < m; i++) {
      hash = hashBlock(hash, data, (i - 1) * GCMMAC.BLOCK_SIZE);
    }
    return hashLastBlock(hash, data, (i - 1) * GCMMAC.BLOCK_SIZE);
  }
  
  public byte[] hashLastBlock(byte[] hash, byte[] data, int dataStart) {
    CryptoUtils.fillWithZeroes(spare);
    System.arraycopy(data, dataStart, spare, 0, data.length - dataStart);
    CryptoUtils.xorByteArrays(hash, 0, spare, 0, spare, 0, GCMMAC.BLOCK_SIZE);
    CryptoUtils.multiplyFiniteFieldsModifyingInputs(spare, key, hash, GCMMAC.TRUNC_POLY);
    return hash;
  }
  
  public byte[] hashBlock(byte[] hash, byte[] data, int dataStart) {
    CryptoUtils.xorByteArrays(hash, 0, data, dataStart, spare, 0, GCMMAC.BLOCK_SIZE);
    CryptoUtils.multiplyFiniteFieldsModifyingInputs(spare, key, hash, GCMMAC.TRUNC_POLY);
    return hash;
  }
  
  public byte[] finalHash(byte[] hash, int plainBytes, int cipherBytes) {
    long plainLength = 8L * plainBytes;
    long cipherLength = 8L * cipherBytes;
    CryptoUtils.longToBytes(plainLength, spare, 0, false);
    CryptoUtils.longToBytes(cipherLength, spare, 0, false);
    CryptoUtils.xorByteArrays(hash, 0, spare, 0, spare, 0, GCMMAC.BLOCK_SIZE);
    CryptoUtils.multiplyFiniteFieldsModifyingInputs(spare, key, hash, GCMMAC.TRUNC_POLY);
    return hash;
  }
  
}
