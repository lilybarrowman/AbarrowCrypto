package cipher;

import java.util.Arrays;

import core.CryptoException;
import core.CryptoUtils;


public class CompoundBlockCipher extends BlockCipher {
  
  private BlockCipher[] ciphers;
  
  private int blockBytes;
  
  public CompoundBlockCipher(BlockCipher[] blockCiphers) {
    ciphers = blockCiphers;
    
    if (ciphers.length == 0) {
      throw new IllegalArgumentException("At least one block cipher must be included in a compound block cipher.");
    }
    
    
    blockBytes = ciphers[0].getBlockBytes();
    
    for (int n = 1; n < ciphers.length; n++) {
      if(ciphers[n].getBlockBytes() != blockBytes) {
        throw new IllegalArgumentException("All block ciphers in a compound block cipher must have the same key length.");
      }
    }
  }

  @Override
  public int getBlockBytes() {
    return blockBytes;
  }

  @Override
  public byte[] encryptBlock(byte[] input, int srcPos, byte[] output, int destPos) throws CryptoException {
    ciphers[0].encryptBlock(input, srcPos, output, destPos);
    for (int n = 1; n < ciphers.length; n++) {
      ciphers[n].encryptBlock(output, destPos, output, destPos);
    }
    return output;
  }

  @Override
  public byte[] decryptBlock(byte[] input, int srcPos, byte[] output, int destPos) throws CryptoException {
    int last = ciphers.length - 1;
    ciphers[last].decryptBlock(input, srcPos, output, destPos);
    for (; last >= 0; last--) {
      ciphers[last].decryptBlock(output, destPos, output, destPos);
    }
    return output;
  }

  @Override
  public void removeKey() {
    for (int n = 0; n < ciphers.length; n++) {
      ciphers[n].removeKey();
    }
  }

  @Override
  public boolean hasKey() {
    boolean hasKey = true;
    for (int n = 0; n < ciphers.length; n++) {
      hasKey = ciphers[n].hasKey() && hasKey;
    }
    return hasKey;
  }

  @Override
  public void setKey(byte[] key) {
    int chunks = key.length / ciphers.length;
    for (int n = 0; n < ciphers.length; n++) {
      byte[] subKey = Arrays.copyOfRange(key, n * chunks, (n + 1) * chunks);
      ciphers[n].setKey(subKey);
      CryptoUtils.fillWithZeroes(subKey);
    }
  }
  

}
