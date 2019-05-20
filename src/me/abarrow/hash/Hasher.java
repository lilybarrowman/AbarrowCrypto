package me.abarrow.hash;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import me.abarrow.core.CryptoUtils;
import me.abarrow.math.Int128;
import me.abarrow.stream.StreamProcess;

public abstract class Hasher {
  
  protected Int128 hashByteCount = new Int128();
  
  public Hasher() {
    reset();
  }
  
  public abstract void hashBlock(byte[] data, int srcPos);
  
  public abstract byte[] computeHash(byte[] remainder, int remainderLength);
  
  public final StreamProcess hash() {
    return new StreamProcess(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        reset();
        int blockBytes = getBlockBytes();
        
        byte[] temp = new byte[blockBytes];
        int read = 0;
        while(true) {
          read = in.read(temp);
          read = read == -1 ? 0 : read;
          if (read == blockBytes) {
            hashBlock(temp, 0);
          } else {
            Arrays.fill(temp, read, blockBytes, CryptoUtils.ZERO_BYTE);
            break;
          }
        }
        byte[] hashed = computeHash(temp, read);
        out.write(hashed);
        CryptoUtils.fillWithZeroes(hashed);
        CryptoUtils.fillWithZeroes(temp);
        reset();
      }
    };
  }
  
  /**
   * Resets any internal state of a hasher.
   */
  public void reset() {
    hashByteCount.toZero();
  }
  
  public abstract int getBlockBytes();
  
  public abstract int getHashByteLength();
  

}
