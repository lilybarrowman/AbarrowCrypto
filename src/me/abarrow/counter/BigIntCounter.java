package me.abarrow.counter;

import java.math.BigInteger;

public class BigIntCounter extends Counter {
  
  private BigInteger value;
  
  public BigIntCounter() {
    this(BigInteger.ZERO);
  }
  
  public BigIntCounter(BigInteger start) {
    value = start;
  }
  
  public BigIntCounter(byte[] start) {
    value = BigInteger.ZERO; //TODO ADAM FIX
  }

  @Override
  public byte[] increment() {
    byte[] ret = value.toByteArray();
    value = value.add(BigInteger.ONE);
    return ret;
  }
  
  @Override
  public byte[][] increment(int count) {
    byte[][] predictions = new byte[count][];
    for (int i = 0; i < count; i++) {
      predictions[i] = value.toByteArray();
      value = value.add(BigInteger.ONE);
    }
    return predictions;
  }
  
  @Override
  public byte[] currentValue() {
    return value.toByteArray();
  }

  @Override
  public void reset() {
    value = BigInteger.ZERO;
  }

}
