package core;

public class CompoundBlockCipher extends AsymmetricBlockCipher {
  
  private AsymmetricBlockCipher[] ciphers;
  
  private int blockBytes;
  
  public CompoundBlockCipher(AsymmetricBlockCipher[] blockCiphers) {
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
  public void encryptBlock(byte[] input, int srcPos, byte[] output, int destPos) {
    ciphers[0].encryptBlock(input, srcPos, output, destPos);
    for (int n = 1; n < ciphers.length; n++) {
      ciphers[n].encryptBlock(output, destPos, output, destPos);
    }
  }

  @Override
  public void decryptBlock(byte[] input, int srcPos, byte[] output, int destPos) {
    ciphers[0].decryptBlock(input, srcPos, output, destPos);
    for (int n = 1; n < ciphers.length; n++) {
      ciphers[n].decryptBlock(output, destPos, output, destPos);
    }
  }

}
