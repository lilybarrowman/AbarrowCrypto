package cipher;

import java.math.BigInteger;

import core.CryptoUtils;

public class CBCModeCipher implements Cipher {

  private byte[] iv;
  private BlockCipher core;
  private int blockBytes;

  private byte[] temp;
  private byte[] temp2;

  public CBCModeCipher(BlockCipher cipherCore, byte[] intializationVector) {
    core = cipherCore;
    blockBytes = core.getBlockBytes();
    setIV(intializationVector);
    temp = new byte[blockBytes];
    temp2 = new byte[blockBytes];
  }

  public void setIV(byte[] initVector) {
    if (blockBytes != initVector.length) {
      throw new IllegalArgumentException("The initialization vector must be of the same length as the block size.");
    }
    iv = initVector;
  }

  @Override
  public byte[] encrypt(byte[] input) {
    byte[] output = new byte[cipherTextLength(input.length)];

    int i;
    System.arraycopy(iv, 0, temp, 0, blockBytes);
    for (i = 0; (i + blockBytes - 1) < input.length; i += blockBytes) {
      CryptoUtils.xorByteArrays(input, i, temp, 0, temp, 0, blockBytes);
      temp = core.encrypt(temp);
      System.arraycopy(temp, 0, output, i, blockBytes);
    }
    if (i < input.length) {
      CryptoUtils.xorByteArrays(input, i, temp, 0, temp, 0, input.length - i);
      System.arraycopy(core.encrypt(temp), 0, output, i, blockBytes);
    }

    CryptoUtils.fillWithZeroes(temp);
    return output;
  }

  @Override
  public byte[] decrypt(byte[] input) {
    byte[] output = new byte[cipherTextLength(input.length)];
    byte[] swap;
    int i;
    System.arraycopy(iv, 0, temp2, 0, blockBytes);
    for (i = 0; (i + blockBytes - 1) < input.length; i += blockBytes) {
      System.arraycopy(input, i, temp, 0, blockBytes);
      CryptoUtils.xorByteArrays(core.decrypt(temp), 0, temp2, 0, output, i, blockBytes);
      swap = temp2;
      temp2 = temp;
      temp = swap;
    }
    if (i < input.length) {
      System.arraycopy(input, i, temp, 0, input.length - i);
      CryptoUtils.xorByteArrays(core.decrypt(temp), 0, temp2, 0, output, i, blockBytes);
    }

    CryptoUtils.fillWithZeroes(temp);
    CryptoUtils.fillWithZeroes(temp2);
    return output;
  }

  @Override
  public int cipherTextLength(int plainTextLength) {
    return core.cipherTextLength(plainTextLength);
  }

}
