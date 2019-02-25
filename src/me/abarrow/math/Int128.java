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
  
  public Int128(int[] w) {
    words = Arrays.copyOf(w, WORD_COUNT);
  }
	  
  
  static void times(final Int128 left, final Int128 right, Int128 dest) {
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
  
  static void plus(final Int128 left, final Int128 right, Int128 dest) {
    assert(left != dest);
    assert(right != dest);
    
    long carry = 0;
    for (int i = 0; i < WORD_COUNT; i++) {
      long sum = (long)(left.words[i]) + (long)(right.words[i]) + carry;
      carry = sum >>> WORD_SIZE;
      dest.words[i] = (int)sum;
    }
  }
  
  static void xor(final Int128 left, final Int128 right, Int128 dest) {
    assert(left != dest);
    assert(right != dest);
    
    for (int i = 0; i < WORD_COUNT; i++) {
      dest.words[i] = left.words[i] ^ right.words[i];
    }
  }
  
  void xorEquals(final Int128 other) {
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
  
  
  static void finite_times(final Int128 left, final Int128 right, Int128 dest, Int128 spare) {
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
  
  static Int128 parseLittleBitEndianHex(String s) {
    byte[] bytes = DatatypeConverter.parseHexBinary(s);
    
    
    Int128 parsed = new Int128(
      CryptoUtils.safeLittleBitEndianIntFromBytes(bytes, 0),
      CryptoUtils.safeLittleBitEndianIntFromBytes(bytes, 4),
      CryptoUtils.safeLittleBitEndianIntFromBytes(bytes, 8),
      CryptoUtils.safeLittleBitEndianIntFromBytes(bytes, 12)
    );
    CryptoUtils.fillWithZeroes(bytes);
    return parsed;
  }
  
  static Int128 parseBigEndianHex(String s) {
    byte[] bytes = DatatypeConverter.parseHexBinary(s);
    
    
    Int128 parsed = new Int128(
      CryptoUtils.safeBigEndianIntFromBytes(bytes, bytes.length - 1),
      CryptoUtils.safeBigEndianIntFromBytes(bytes, bytes.length - 5),
      CryptoUtils.safeBigEndianIntFromBytes(bytes, bytes.length - 9),
      CryptoUtils.safeBigEndianIntFromBytes(bytes, bytes.length - 13)
    );
    CryptoUtils.fillWithZeroes(bytes);
    return parsed;
  }
  
  String toHexString() {
	StringBuilder out = new StringBuilder();
	for (int w = WORD_COUNT - 1; w >=0 ; w--) {
		out.append(String.format("%08x", words[w]));
	}
	return out.toString();
  }
  
  void toZero() {
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
