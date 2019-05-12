package me.abarrow.hash;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;

import me.abarrow.core.CryptoUtils;
import me.abarrow.stream.StreamProcess;

public abstract class Hasher {
  
  
  public Hasher() {
    reset();
  }
  
  protected abstract void hashBlock(byte[] data, int srcPos);
  
  protected abstract byte[] computeHash(BigInteger dataLength, byte[] remainder, int remainderLength);
  
  public final StreamProcess hash() {
    return new StreamProcess(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        reset();
        BigInteger totalLength = BigInteger.ZERO; // TODO can I replace with long?
        int blockBytes = getBlockBytes();
        
        byte[] temp = new byte[blockBytes];
        int read = 0;
        while(true) {
          read = in.read(temp);
          read = read == -1 ? 0 : read;
          totalLength = totalLength.add(BigInteger.valueOf(read));
          if (read == blockBytes) {
            hashBlock(temp, 0);
          } else {
            Arrays.fill(temp, read, blockBytes, CryptoUtils.ZERO_BYTE);
            break;
          }
        }
        byte[] hashed = computeHash(totalLength, temp, read);
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
  protected void reset() {
  }
  
  public abstract int getBlockBytes();
  
  public abstract int getHashByteLength();
  

}
