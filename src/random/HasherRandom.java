package random;

import hash.Hasher;
import hash.sha.SHA2_512;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import core.CryptoUtils;

public class HasherRandom extends BufferedRandom {

  private static final long serialVersionUID = 2016568662253776827L;
    
  final private Hasher hasher;
  private int hashLength;
  private byte[] key;
  BigInteger counter;
  
  public HasherRandom(Hasher hashMaker, byte[] cryptoKey) {
    super(hashMaker.getHashByteLength());
    hasher = hashMaker;
    hasher.reset();
    key = cryptoKey;    
    hashLength = hasher.getHashByteLength();
    counter = BigInteger.ZERO;
  }
  
  @Override
  protected void generateMoreBytes(byte[] data) {    
    System.arraycopy(hasher.addBytes(key).addBytes(counter.toByteArray()).computeHash(), 0, data, 0, hashLength);
    hasher.reset();
    counter = counter.add(BigInteger.ONE);
  }
  
  public void resetCounter() {
    clearBuffer();
    counter = BigInteger.ZERO;
  }
}
