package me.abarrow.math;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import me.abarrow.core.CryptoUtils;

public class Int128 extends Number {
  private static final int WORD_COUNT = 4;
  private static final int WORD_SIZE = 32;
  private static final int BIT_COUNT = 128;

  
  // The 4 words in little endian order
  private int[] words;
  
  public Int128() {
    words = new int[WORD_COUNT];
  }
  
  public Int128(int lowest, int low, int high, int highest) {
    words = new int[] {lowest, low, high, highest};
  }
  
  public Int128(int from) {
    words = new int[] {from, 0, 0, 0};
  }
  
  public Int128(long from) {
    words = new int[] {(int)(from & 0xffffffff), (int)((from >>> 32) & 0xffffffff), 0, 0};
  }
  
  
  public Int128(int[] w) {
    words = Arrays.copyOf(w, WORD_COUNT);
  }
	
  public void setWords(int lowest, int low, int high, int highest) {
    words[0] = lowest;
    words[1] = low;
    words[2] = high;
    words[3] = highest;
  }
  
  public void setWord(int idx, int value) {
    words[idx] = value;
  }
  
  public int getWord(int idx) {
    return words[idx];
  }
  
  public static void times(final Int128 left, final Int128 right, Int128 dest) {
    assert(left != dest);
    assert(right != dest);
    
    dest.toZero();
    for (int ri = 0; ri < WORD_COUNT; ri++) {
      long carry = 0;
      for (int li = 0; (ri + li) < WORD_COUNT; li++) {
        long product = (long)(dest.words[ri + li]) + (long)(carry) + (long)(left.words[li]) * (long)(right.words[ri]);
        carry = product >> WORD_SIZE;
        dest.words[ri + li] = (int)product;
      }
      // carry here would always be stored words[WORD_COUNT+] 
    }
  }
  
  public static void plus(final Int128 left, final Int128 right, Int128 dest) {
    assert(left != dest);
    assert(right != dest);
    
    long carry = 0;
    for (int i = 0; i < WORD_COUNT; i++) {
      long sum = (long)(left.words[i]) + (long)(right.words[i]) + carry;
      carry = sum >>> WORD_SIZE;
      dest.words[i] = (int)sum;
    }
  }
  
  public static void xor(final Int128 left, final Int128 right, Int128 dest) {
    assert(left != dest);
    assert(right != dest);
    
    for (int i = 0; i < WORD_COUNT; i++) {
      dest.words[i] = left.words[i] ^ right.words[i];
    }
  }
  
  public void plusEquals(final Int128 other) {
    long carry = 0;
    for (int i = 0; i < WORD_COUNT; i++) {
      long sum = (long)(words[i]) + (long)(other.words[i]) + carry;
      carry = sum >>> WORD_SIZE;
      words[i] = (int)sum;
    }
  }
  
  public void plusEquals(int other) {
    long carry = 0;
    long sum = (long)(words[0]) + (long)(other) + carry;
    carry = sum >>> WORD_SIZE;
    words[0] = (int)sum;
    for (int i = 1; i < WORD_COUNT; i++) {
      sum = (long)(words[i]) + carry;
      carry = sum >>> WORD_SIZE;
      words[i] = (int)sum;
    }
  }
  
  public void plusEquals(long other) {
    long otherLowest = other & 0xffffffff;
    long otherLow = (other >>> 32) & 0xffffffff;
    
    long carry = 0;
    long sum = (long)(words[0]) + otherLowest + carry;
    carry = sum >>> WORD_SIZE;
    words[0] = (int)sum;
    
    sum = (long)(words[1]) + otherLow + carry;
    carry = sum >>> WORD_SIZE;
    words[1] = (int)sum;
    
    for (int i = 2; i < WORD_COUNT; i++) {
      sum = (long)(words[i]) + carry;
      carry = sum >>> WORD_SIZE;
      words[i] = (int)sum;
    }
  }
  
  public void xorEquals(final Int128 other) {
    for (int i = 0; i < WORD_COUNT; i++) {
      words[i] ^= other.words[i];
    }
  }
  
  void set(final Int128 other) {
    for (int i = 0; i < WORD_COUNT; i++) {
      words[i] = other.words[i];
    }
  }
  
  int shiftLeftOne() {
    int carry = 0;
    for (int i = 0; i < WORD_COUNT; i++) {
      int new_carry = words[i] >>> (WORD_SIZE - 1);
      words[i] = (words[i] << 1) | carry;
      carry = new_carry;
    }
    return carry;
  }
  
  
  public static void finiteTimes(final Int128 left, final Int128 right, Int128 dest, Int128 spare) {
    assert(left != dest);
    assert(right != dest);
    
    dest.toZero();
    spare.set(left);
    
      for (int n = 0; n < BIT_COUNT; n++) {
        if (right.getBit(n) == 1) {
          dest.xorEquals(spare);
        }
        
        if (spare.shiftLeftOne() == 1) {
          // This is specific to the use in GCM/GHASH
          spare.words[0] ^= 0x87;
        }
      }
  }
  
  public void finiteTimesEquals(final Int128 other, Int128 spare) {
    assert(other != spare);
    spare.set(this);
    toZero();
    
      for (int n = 0; n < BIT_COUNT; n++) {
        if (other.getBit(n) == 1) {
          xorEquals(spare);
        }
        
        if (spare.shiftLeftOne() == 1) {
          // This is specific to the use in GCM/GHASH
          spare.words[0] ^= 0x87;
        }
      }
  }
  
  public Int128 copyFromLittleEndian(byte[] bytes) {
    return copyFromLittleEndian(bytes, 0);
  }
  
  public Int128 copyFromLittleEndian(byte[] bytes, int start) {
    words[0] = CryptoUtils.safeLittleEndianIntFromBytes(bytes, start);
    words[1] = CryptoUtils.safeLittleEndianIntFromBytes(bytes, start + 4);
    words[2] = CryptoUtils.safeLittleEndianIntFromBytes(bytes, start + 8);
    words[3] = CryptoUtils.safeLittleEndianIntFromBytes(bytes, start + 12);
    return this;
  }
  
  public static Int128 fromLittleEndian(byte[] bytes, int start) {
    return new Int128().copyFromLittleEndian(bytes, start);
  }
  
  public static Int128 fromLittleEndian(byte[] bytes) {
    return fromLittleEndian(bytes, 0);
  }
  
  public Int128 copyFromLittleBitEndian(byte[] bytes, int start) {
    words[0] = CryptoUtils.safeLittleBitEndianIntFromBytes(bytes, start);
    words[1] = CryptoUtils.safeLittleBitEndianIntFromBytes(bytes, start + 4);
    words[2] = CryptoUtils.safeLittleBitEndianIntFromBytes(bytes, start + 8);
    words[3] = CryptoUtils.safeLittleBitEndianIntFromBytes(bytes, start + 12);
    return this;
  }

  public Int128 copyFromLittleBitEndian(byte[] bytes) {
    return copyFromLittleBitEndian(bytes, 0);
  }
  
  public static Int128 fromLittleBitEndian(byte[] bytes, int start) {
    return new Int128().copyFromLittleBitEndian(bytes, start);
  }
  
  public static Int128 fromLittleBitEndian(byte[] bytes) {
    return fromLittleBitEndian(bytes, 0);
  }
  
  public static Int128 parseLittleBitEndianHex(String s) {
    byte[] bytes = DatatypeConverter.parseHexBinary(s);
    Int128 parsed = fromLittleBitEndian(bytes);
    CryptoUtils.fillWithZeroes(bytes);
    return parsed;
  }
  
  public static Int128 parseBigEndianHex(String s) {
    byte[] bytes = DatatypeConverter.parseHexBinary(s);
    Int128 parsed = new Int128(
      CryptoUtils.safeBigEndianIntFromBytes(bytes, bytes.length - 1),
      CryptoUtils.safeBigEndianIntFromBytes(bytes, bytes.length - 5),
      CryptoUtils.safeBigEndianIntFromBytes(bytes, bytes.length - 9),
      CryptoUtils.safeBigEndianIntFromBytes(bytes, bytes.length - 13));
    CryptoUtils.fillWithZeroes(bytes);
    return parsed;
  }
  
  public String toHexString() {
  	StringBuilder out = new StringBuilder();
  	for (int w = WORD_COUNT - 1; w >=0 ; w--) {
  		out.append(String.format("%08x", words[w]));
  	}
  	return out.toString();
  }
  
  public String tolittleBitEndianHexString() {
    StringBuilder out = new StringBuilder();
    byte[] littleBitEndians = toLittleBitEndianBytes();
    for (int n = 0; n < littleBitEndians.length; n++) {
      out.append(String.format("%02x", littleBitEndians[n] & 0xff));
    }
    return out.toString();
  }
  
  public byte[] toLittleEndianBytes() {
    return CryptoUtils.intArrayToByteArray(words, true);
  }
  
  public byte[] toLittleBitEndianBytes() {
    return CryptoUtils.littleBitEndianToLittleEndian(toLittleEndianBytes());
  }
  
  public byte[] toLittleEndianBytes(byte[] dest) {
    return CryptoUtils.intArrayToByteArray(dest, 0, words, true);
  }
  
  public byte[] toLittleEndianBytes(byte[] dest, int start) {
    return CryptoUtils.intArrayToByteArray(dest, start, words, true);
  }
  
  public byte[] toBigEndianBytes(byte[] dest, int start) {
    CryptoUtils.intToBytes(words[0], dest, start + 12, false);
    CryptoUtils.intToBytes(words[1], dest, start + 8, false);
    CryptoUtils.intToBytes(words[2], dest, start + 4, false);
    CryptoUtils.intToBytes(words[3], dest, start, false);
    return dest;
  }
  
  public byte[] toLittleBitEndianBytes(byte[] dest) {
    return CryptoUtils.littleBitEndianToLittleEndian(toLittleEndianBytes(dest));
  }
  
  public void toZero() {
    for (int i = 0; i < WORD_COUNT; i++) {
      words[i] = 0;
    }
  }
  
  int getBit(int idx) {
    int word_idx = idx / WORD_SIZE;
    int bit_idx = idx % WORD_SIZE;
    return (words[word_idx] >>> bit_idx) & 0x1;
  }
  
  public int[] getWordsCopy() {
	  return Arrays.copyOf(words, WORD_COUNT);
  }
  
  public boolean constantTimeEquals(Int128 other) {
    return CryptoUtils.constantTimeArrayEquals(words, other.words);
  }

  public boolean fastEquals(Int128 other) {
    return (words[0] == other.words[0]) &&
        (words[1] == other.words[1]) &&
        (words[2] == other.words[2]) &&
        (words[3] == other.words[3]);
  }

  @Override
  public int intValue() {
    return words[0];
  }

  @Override
  public long longValue() {
    long low = words[0];
    long high = words[1] << WORD_SIZE;
    return high | low;
  }

  @Override
  public float floatValue() {
    return (float) doubleValue();
  }

  @Override
  public double doubleValue() {
    return words[0] +
        words[1] * Math.pow(2, WORD_SIZE) +
        words[2] * Math.pow(2, 2 * WORD_SIZE) +
        words[3] * Math.pow(2, 3* WORD_SIZE);
  }

}
