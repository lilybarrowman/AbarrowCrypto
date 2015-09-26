package me.abarrow.random;

import java.io.IOException;

import me.abarrow.counter.BigIntCounter;
import me.abarrow.counter.Counter;
import me.abarrow.hash.Hasher;

public class HasherRandom extends BufferedRandom {

  private static final long serialVersionUID = 2016568662253776827L;
    
  final private Hasher hasher;
  private byte[] key;
  private Counter counter;
  
  public HasherRandom(Hasher hashMaker, byte[] cryptoKey) {
    this(hashMaker, cryptoKey, new BigIntCounter());
  }
  
  public HasherRandom(Hasher hashMaker, byte[] cryptoKey, Counter c) {
    super(hashMaker.getHashByteLength());
    hasher = hashMaker;
    key = cryptoKey;    
    counter = c;
  }
  
  @Override
  protected void generateMoreBytes(byte[] data) {    
    try {
      hasher.hash().asByteProcess(false).add(key).add(counter.increment()).finishSync(data, 0);
    } catch (IOException e) {
      //there's little we can do about this
    }
  }
  
  public void resetCounter() {
    clearBuffer();
    counter.reset();
  }
}
