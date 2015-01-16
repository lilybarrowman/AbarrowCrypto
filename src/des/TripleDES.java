package des;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import core.AsymmetricBlockCipher;
import core.CryptoUtils;
import core.PairityBitType;

public class TripleDES extends AsymmetricBlockCipher {
  
  private DES des1;
  private DES des2;
  private DES des3;
  
  private byte[] temp1;
  private byte[] temp2;
  
  public TripleDES(byte[] key, PairityBitType pairityType) {
    des1 = new DES(ArrayUtils.subarray(key, 0, 7), pairityType);
    des2 = new DES(ArrayUtils.subarray(key, 1, 15), pairityType);
    des3 = new DES(ArrayUtils.subarray(key, 16, 23), pairityType);
  }

  @Override
  protected int getBlockBytes() {
    return 8;
  }

  @Override
  public void encryptBlock(byte[] input, int srcPos, byte[] output, int destPos) {
    
    des1.encryptBlock(input, srcPos, temp1, 0);
    des2.decryptBlock(temp1, 0, temp2, 0);
    Arrays.fill(temp1, CryptoUtils.ZERO_BYTE);
    des3.encryptBlock(temp2, 0, output, destPos);
    Arrays.fill(temp2, CryptoUtils.ZERO_BYTE);
  }

  @Override
  public void decryptBlock(byte[] input, int srcPos, byte[] output, int destPos) {
    des3.decryptBlock(input, srcPos, temp1, 0);
    des2.encryptBlock(temp1, 0, temp2, 0);
    Arrays.fill(temp1, CryptoUtils.ZERO_BYTE);
    des1.decryptBlock(temp2, 0, output, destPos);
    Arrays.fill(temp2, CryptoUtils.ZERO_BYTE);
  }

}
