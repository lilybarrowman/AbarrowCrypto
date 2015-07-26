package me.abarrow.cipher.mode;

import me.abarrow.cipher.BlockCipher;
import me.abarrow.core.CryptoUtils;
import me.abarrow.mac.gcmac.GCMMAC;

public class GCMMode {
/*  
  public BlockCipher core;
  private byte[] hash;
  private GCMMAC mac;
  private int plainBytes;
  private int cipherBytes;
  
  public GCMMode(BlockCipher cipher, byte[] plain, byte[] key, byte[] IV) {
    core = cipher;
    if (core.getBlockBytes() != GCMMAC.BLOCK_SIZE) {
      throw new IllegalArgumentException("GCM is only defined for ciphers with a 128 bit block size.");
    }
    
    mac = new GCMMAC(core.encryptBlock(IV));

    byte[] preIV = new byte[(IV.length + 2 * GCMMAC.BLOCK_SIZE - 1) / GCMMAC.BLOCK_SIZE];
    System.arraycopy(IV, 0, preIV, 0, IV.length);
    CryptoUtils.longToBytes(IV.length * 8L, preIV, preIV.length - 8);
    
    byte[] j0 = mac.hashData(IV, preIV);
    
    
    hash = mac.plainAuthHash(plain);
    plainBytes = plain.length;
    cipherBytes = 0;
  }

  public byte[] codec(byte[] input) {
    cipherBytes = input.length;
    int cipherBlocks = (input.length + GCMMAC.BLOCK_SIZE - 1) / GCMMAC.BLOCK_SIZE;
    byte[] output = new byte[cipherBlocks * GCMMAC.BLOCK_SIZE];
    
    
    
    return output;
  }
  
  public byte[] getFinalMAC() {
    hash = mac.finalHash(hash, plainBytes, cipherBytes);
    //after this function is called this class is dead
    return hash;
  }
*/
}
