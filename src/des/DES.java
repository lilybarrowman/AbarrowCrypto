package des;

import java.util.Arrays;

import org.apache.commons.lang.mutable.MutableInt;

import core.AsymmetricBlockCipher;
import core.CryptoUtils;
import core.PairityBitCodec;

public class DES extends AsymmetricBlockCipher {

  public static final int ROUNDS = 16;
  private static final int BLOCK_BITS = 512;
  private static final int BLOCK_BYTES = DES.BLOCK_BITS / 8;

  public static final int[] IP = new int[] { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46,
      38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
      61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 };
  
  public static final int[] IP_PRIME = new int[] {  };
    
  private byte[] premutated;
  
  private byte[][] subKeys;
  
  //private byte[] key;
    
  public DES(byte[] cryptoKey, boolean isParityBitOdd) {
    premutated = new byte[BLOCK_BYTES];
    subKeys = new byte[48][ROUNDS];
    byte[] key = PairityBitCodec.decode(cryptoKey, isParityBitOdd);
    
    byte[] leftKey = CryptoUtils.copyBitsFromByteArray(key, 0, 28, new byte[4], 0);
    byte[] rightKey  = CryptoUtils.copyBitsFromByteArray(key, 28, 28, new byte[4], 0);
    
    
  }

  @Override
  protected int getBlockBytes() {
    return DES.BLOCK_BYTES;
  }

  private void encryptBlock(MutableInt lRef, MutableInt rRef) {

    int L = lRef.intValue();
    int R = rRef.intValue();
    
    

    for (int i = 0; i < DES.ROUNDS; i += 2) {
      R ^= f(L);
      L ^= f(R);
    }

    lRef.setValue(L);
    rRef.setValue(R);
  }

  private int f(int x) {
    return 0;
  }

  @Override
  public void encryptBlock(byte[] input, int srcPos, byte[] output, int destPos) {
    CryptoUtils.permuteByteArray(input, srcPos, premutated, 0, IP, DES.BLOCK_BYTES);
    
    MutableInt L =new MutableInt(CryptoUtils.intFromBytes(premutated, 0));
    MutableInt R =new MutableInt(CryptoUtils.intFromBytes(premutated, 0));

    encryptBlock(L, R);

    CryptoUtils.intToBytes(L.intValue(), premutated, destPos);
    CryptoUtils.intToBytes(R.intValue(), premutated, destPos + 4);
    
    CryptoUtils.permuteByteArray(premutated, 0, output, destPos, IP, DES.BLOCK_BYTES);
    Arrays.fill(premutated, CryptoUtils.ZERO_BYTE);
  }

  @Override
  public void decryptBlock(byte[] input, int srcPos, byte[] output, int destPos) {
    // TODO Auto-generated method stub

  }

}
