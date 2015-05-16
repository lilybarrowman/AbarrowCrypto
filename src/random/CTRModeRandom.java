package random;

import java.math.BigInteger;
import core.BlockCipher;
import core.CryptoUtils;

public class CTRModeRandom extends BufferedRandom {
  private static final long serialVersionUID = 9012294883724451247L;
  private BlockCipher core;
  private byte[] iv;
  private int blockBytes;
  private BigInteger counter;

  public void setIV(byte[] initVector) {
    if (blockBytes != initVector.length) {
      throw new IllegalArgumentException("The initialization vector must be of the same length as the block size.");
    }
    iv = initVector;
    resetCounter();
  }

  public CTRModeRandom(BlockCipher cipherCore, byte[] intializationVector) {
    super(cipherCore.getBlockBytes());
    core = cipherCore;
    blockBytes = core.getBlockBytes();
    setIV(intializationVector);
    counter = BigInteger.ZERO;
  }

  @Override
  protected void generateMoreBytes(byte[] data) {
    CryptoUtils.fillWithZeroes(data);
    CryptoUtils.fillLastBytes(counter.toByteArray(), data, blockBytes);
    CryptoUtils.xorByteArrays(iv, 0, data, 0, data, 0, blockBytes);
    core.encryptBlock(data, 0, data, 0);
    counter = counter.add(BigInteger.ONE);
  }
  
  public void resetCounter() {
    clearBuffer();
    counter = BigInteger.ZERO;
  }

}
