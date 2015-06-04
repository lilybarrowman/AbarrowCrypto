package random;

import cipher.BlockCipher;
import core.CryptoException;
import core.CryptoUtils;
import counter.BigIntCounter;
import counter.Counter;

public class CTRModeRandom extends BufferedRandom {
  private static final long serialVersionUID = 9012294883724451247L;
  private BlockCipher core;
  private byte[] iv;
  private int blockBytes;
  private Counter counter;

  public CTRModeRandom(BlockCipher cipherCore, byte[] intializationVector) {
    this(cipherCore, intializationVector, new BigIntCounter());
  }
  
  public CTRModeRandom(BlockCipher cipherCore, byte[] initVector, Counter start) {
    super(cipherCore.getBlockBytes());
    core = cipherCore;
    blockBytes = core.getBlockBytes();
    iv = initVector;
    counter = start;
  }

  @Override
  protected void generateMoreBytes(byte[] data) {
    CryptoUtils.fillWithZeroes(data);
    CryptoUtils.fillLastBytes(counter.increment(), data, blockBytes);
    CryptoUtils.xorByteArrays(iv, data, data);
    try {
      core.encryptBlock(data, data);
    } catch (CryptoException e) {
      e.printStackTrace();
      //TODO ADAM find a better fix
      //nothing much we can do here
    }
  }

}
