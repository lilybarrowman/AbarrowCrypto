package random;

import java.util.Arrays;
import java.util.Random;
import core.CryptoUtils;

public abstract class BufferedRandom extends Random {

  private static final long serialVersionUID = -815491814864684933L;
  private int randomDataIndex;
  private byte[] randomData;
  private byte[] intBytes;
  private int bufferSize;
  
  public BufferedRandom(int buffSize) {    
    bufferSize = buffSize;
    intBytes = new byte[4];
    randomData = new byte[bufferSize];
    randomDataIndex = -1;
  }
  
  @Override
  protected final int next(int bits) {
    if (randomDataIndex == -1 || (randomData.length - randomDataIndex) < intBytes.length) {
      generateMoreBytes(randomData);
      randomDataIndex = 0;
    }
    
    System.arraycopy(randomData, randomDataIndex, intBytes, 0, 4);
    
    //now we have that much less random data to work with
    Arrays.fill(randomData, randomDataIndex, randomDataIndex + 4, CryptoUtils.ZERO_BYTE);
    randomDataIndex += 4;
    
    int result = CryptoUtils.intFromBytes(intBytes, 0) >>> (32 - bits);
    
    //make sure to limit state data
    Arrays.fill(intBytes, CryptoUtils.ZERO_BYTE);
    return result;
  }
  
  @Override
  public final void nextBytes(byte[] bytes) {
    int byteIndex = 0;
    
    while (byteIndex < bytes.length) {
      int bytesLeft = randomData.length - randomDataIndex;
      
      if (randomDataIndex == -1 || bytesLeft < 1) {
        generateMoreBytes(randomData);
        bytesLeft = bufferSize;
        randomDataIndex = 0;
      }
      
      //copy bytes from the random data to the byte array
      int bytesCopied = Math.min(bytesLeft, bytes.length - byteIndex);
      System.arraycopy(randomData, 0, bytes, byteIndex, bytesCopied); 
      byteIndex += bytesCopied;
      
      //now we have that much less random data to work with
      Arrays.fill(randomData, randomDataIndex, randomDataIndex + bytesCopied, CryptoUtils.ZERO_BYTE);
      randomDataIndex += bytesCopied;
    }
  }
  
  public final void clearBuffer() {
    CryptoUtils.fillWithZeroes(randomData);
    randomDataIndex = -1;
  }
  
  protected abstract void generateMoreBytes(byte[] data);
}
