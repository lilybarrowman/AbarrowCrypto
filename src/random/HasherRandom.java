package random;

import java.util.Arrays;
import java.util.Random;

import sha.SHA512;
import core.CryptoUtils;
import core.Hasher;

public class HasherRandom extends Random {

  private static final long serialVersionUID = 2016568662253776827L;
    
  final private Hasher hasher;
  
  final private int hashLength;
  
  //state data
  
  private int randomDataIndex;
  
  private long counter; 
  
  //An array of the key and a block of bytes to fill the counter with
  private byte[] toHash;
  
  private byte[] randomData;
  
  private byte[] intBytes;
  
  public HasherRandom() {
    this(new SHA512(), null);
  }
  
  public HasherRandom(byte[] cryptoKey) {
    this(new SHA512(), cryptoKey);
  }
  
  public HasherRandom(long seed) {
    this(new SHA512(), CryptoUtils.longToBytes(seed, new byte[8], 0));
  }
  
  public HasherRandom(Hasher hashMaker, byte[] cryptoKey) {    
    hasher = hashMaker;
    hasher.reset();
    
    byte[] key;
    if (cryptoKey == null) {
      key = RandomKeyMaker.makeKey(hashMaker.getHashByteLength());
    } else {      
      key = cryptoKey;
    }
    
    int blockBytes = hasher.getBlockBytes();
    
    hashLength = hasher.getHashByteLength();
    
    toHash = new byte[blockBytes + key.length];
    System.arraycopy(key, 0, toHash, 0, key.length);
    
    intBytes = new byte[4];
    randomData = new byte[blockBytes];
    randomDataIndex = -1;
  }
  
 
  
  @Override
  protected int next(int bits) {
    if (randomDataIndex == -1 || (randomData.length - randomDataIndex) < intBytes.length) {
      generateNextKey();
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
  public void nextBytes(byte[] bytes) {
    
    int byteIndex = 0;
    
    int count = 0;
    
    while (byteIndex < bytes.length) {
      
      int bytesLeft = randomData.length - randomDataIndex;
      
      if(count % 100 == 0) {
        //System.out.println(byteIndex + " / " + bytes.length);
      }
      count++;
      
      if (randomDataIndex == -1 || bytesLeft < 1) {
        generateNextKey();
        bytesLeft = hashLength;
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
  
  private void generateNextKey() {
    
    int startOfCounter = toHash.length - 8;
    
    randomDataIndex = 0;
    randomData = hasher.addBytes(CryptoUtils.longToBytes(counter, toHash, startOfCounter)).computeHash();
    hasher.reset();
    
    //clear the count from toHash
    Arrays.fill(toHash, startOfCounter, toHash.length, CryptoUtils.ZERO_BYTE);
    
    counter++;
  }

}
