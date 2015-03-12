package core;

import java.math.BigInteger;

public class CTRModeCipher extends SymmetricStreamCipher {
  
  private AsymmetricBlockCipher core;
  private byte[] iv;
  
  private byte[] temp;  
  private int blockBytes;
  

  public CTRModeCipher(AsymmetricBlockCipher cipherCore, byte[] intializationVector) {
    
    core = cipherCore;
    iv = intializationVector;
    
    blockBytes = core.getBlockBytes();
    
    if (blockBytes != iv.length) {
      throw new IllegalArgumentException("The initialization vector must be of the same length as the block size.");
    }
    
    temp = new byte[blockBytes];
  }
  
  
  @Override
  public byte[] codec(byte[] input) {
    byte[] output = new byte[cipherTextLength(input.length)];

    int i;
    
    BigInteger blockNumber = BigInteger.ZERO;

    for (i = 0; (i + blockBytes - 1) < input.length; i+= blockBytes) {
      
      CryptoUtils.fillLastBytes(blockNumber.toByteArray(), temp, blockBytes);
      CryptoUtils.xorByteArrays(iv, 0, temp, 0, temp, 0, blockBytes);   
      core.encryptBlock(temp, 0, temp, 0);
      
      CryptoUtils.xorByteArrays(input, i, temp, 0, output, i, blockBytes);
      
      CryptoUtils.fillWithZeroes(temp);
      blockNumber = blockNumber.add(BigInteger.ONE);
    }    
    
    if (i < input.length) {
      CryptoUtils.fillLastBytes(blockNumber.toByteArray(), temp, blockBytes);
      CryptoUtils.xorByteArrays(iv, 0, temp, 0, temp, 0, blockBytes);   
      core.encryptBlock(temp, 0, temp, 0);
      
      //since we don't have a full block xor what we have left
      int amountLeft = input.length - i;
      CryptoUtils.xorByteArrays(input, i, temp, 0, output, i, amountLeft);
      
      CryptoUtils.fillWithZeroes(temp);
    }
    return output;
  }

}
