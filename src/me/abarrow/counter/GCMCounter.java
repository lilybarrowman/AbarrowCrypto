package me.abarrow.counter;

import java.util.Arrays;

import me.abarrow.core.CryptoUtils;

public class GCMCounter extends Counter {

  private byte[] value;
  private int intIndex;
  private int originalValue;

  public GCMCounter(byte[] IV) {
    if (IV == null) {
      throw new IllegalArgumentException("The IV of a GCMCounter cannot be null.");
    }
    value = IV;
    intIndex = value.length - 4;
    originalValue = CryptoUtils.intFromBytes(value, intIndex);
  }

  @Override
  public byte[] increment() {
    byte[] clone = Arrays.copyOf(value, value.length);
    int val = CryptoUtils.intFromBytes(value, intIndex);
    val++;
    CryptoUtils.intToBytes(val, value, intIndex);
    return clone;
  }

  @Override
  public byte[][] increment(int count) {
    byte[][] values = new byte[count][];
    int val = CryptoUtils.intFromBytes(value, intIndex);
    for (int i = 0; i < count; i++) {
      values[i] = Arrays.copyOf(value, value.length);
      val++;
      CryptoUtils.intToBytes(val, value, intIndex);
    }
    return values;
  }

  @Override
  public byte[] currentValue() {
    return value;
  }

  @Override
  public void reset() {
    CryptoUtils.intToBytes(originalValue, value, intIndex);
  }

}
