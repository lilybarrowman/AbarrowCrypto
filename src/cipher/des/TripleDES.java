package cipher.des;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import cipher.BlockCipher;
import core.CryptoException;
import core.CryptoUtils;

public class TripleDES extends BlockCipher {
  
  private static final int[] KEY_LENGTHS = new int[] {24};
  
  private DES des1;
  private DES des2;
  private DES des3;
  
  private byte[] temp1;
  private byte[] temp2;
  
  public TripleDES() {
  }

  
  public TripleDES(byte[] key) {
    setKey(key);
  }

  @Override
  public int getBlockBytes() {
    return 8;
  }

  @Override
  public byte[] encryptBlock(byte[] input, int srcPos, byte[] output, int destPos) throws CryptoException {
    des1.encryptBlock(input, srcPos, temp1, 0);
    des2.decryptBlock(temp1, 0, temp2, 0);
    Arrays.fill(temp1, CryptoUtils.ZERO_BYTE);
    des3.encryptBlock(temp2, 0, output, destPos);
    Arrays.fill(temp2, CryptoUtils.ZERO_BYTE);
    return output;
  }

  @Override
  public byte[] decryptBlock(byte[] input, int srcPos, byte[] output, int destPos) throws CryptoException {
    des3.decryptBlock(input, srcPos, temp1, 0);
    des2.encryptBlock(temp1, 0, temp2, 0);
    Arrays.fill(temp1, CryptoUtils.ZERO_BYTE);
    des1.decryptBlock(temp2, 0, output, destPos);
    Arrays.fill(temp2, CryptoUtils.ZERO_BYTE);
    return output;
  }

  public int[] getValidKeyLengths() {
    return TripleDES.KEY_LENGTHS;
  }


  @Override
  public void removeKey() {
    des1.removeKey();
    des2.removeKey();
    des3.removeKey();
  }


  @Override
  public boolean hasKey() {
    return des1.hasKey();
  }


  @Override
  public void setKey(byte[] key) {
    des1 = new DES(ArrayUtils.subarray(key, 0, 8));
    des2 = new DES(ArrayUtils.subarray(key, 8, 16));
    des3 = new DES(ArrayUtils.subarray(key, 16, 24));
    
    temp1=new byte[getBlockBytes()];
    temp2=new byte[getBlockBytes()];    
  }

}
