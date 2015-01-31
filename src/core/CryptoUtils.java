package core;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.mutable.MutableInt;

public class CryptoUtils {

  public static final byte ONE_AND_SEVEN_ZEROES_BYTE = (byte) 0x80;
  public static final byte ZERO_BYTE = (byte) 0;

  private static int[] LAST_N_BITS = new int[] {
      // [0] 0000 0000
      0x0,
      // [1] 0000 0001
      0x1,
      // [2] 0000 0011
      0x3,
      // [3] 0000 0111
      0x7,
      // [4] 0000 1111
      0xf,
      // [5] 0001 1111
      0x1f,
      // [6] 0011 1111
      0x3f,
      // [7] 0111 1111
      0x7f,
      // [8] 1111 1111
      0xff };

  private static int[] FIRST_N_BITS = new int[] {
      // [0] 0000 0000
      0x0,
      // [1] 1000 0000
      0x80,
      // [2] 1100 0000
      0xc0,
      // [3] 1110 0000
      0xe0,
      // [4] 1111 0000
      0xf0,
      // [5] 1111 1000
      0xf8,
      // [6] 1111 1100
      0xfc,
      // [7] 1111 1110
      0xfe,
      // [8] 1111 1111
      0xff };

  public static int swapEndianness(int x) {
    return CryptoUtils.intFromBytes(CryptoUtils.intToBytes(x, new byte[4], 0, false), 0, true);
  }
  
  public static int[] swapEndianness(int[] ints) {
    return CryptoUtils.intArrayFromBytes(CryptoUtils.intArrayToByteArray(ints, false), 0, 4 * ints.length, true);
  }
  
  public static int rotateIntLeft(int x, int digits) {
    return (x << digits) | (x >>> (32 - digits));
  }

  public static int rotateIntRight(int x, int digits) {
    return (x >>> digits) | (x << (32 - digits));
  }

  public static long rotateLongLeft(long x, long digits) {
    return (x << digits) | (x >>> (64 - digits));
  }

  public static long rotateLongRight(long x, long digits) {
    return (x >>> digits) | (x << (64 - digits));
  }

  public static byte[] longToBytes(long val, byte[] bytes, int start) {
    longToBytes(val, bytes, start, false);
    return bytes;
  }

  public static void longToBytes(long val, byte[] bytes, int start, boolean isLittleEndian) {
    if (isLittleEndian) {
      bytes[start + 7] = (byte) ((val >>> 56) & 0xff);
      bytes[start + 6] = (byte) ((val >>> 48) & 0xff);
      bytes[start + 5] = (byte) ((val >>> 40) & 0xff);
      bytes[start + 4] = (byte) ((val >>> 32) & 0xff);
      bytes[start + 3] = (byte) ((val >>> 24) & 0xff);
      bytes[start + 2] = (byte) ((val >>> 16) & 0xff);
      bytes[start + 1] = (byte) ((val >>> 8) & 0xff);
      bytes[start] = (byte) (val & 0xff);
    } else {
      bytes[start] = (byte) ((val >>> 56) & 0xff);
      bytes[start + 1] = (byte) ((val >>> 48) & 0xff);
      bytes[start + 2] = (byte) ((val >>> 40) & 0xff);
      bytes[start + 3] = (byte) ((val >>> 32) & 0xff);
      bytes[start + 4] = (byte) ((val >>> 24) & 0xff);
      bytes[start + 5] = (byte) ((val >>> 16) & 0xff);
      bytes[start + 6] = (byte) ((val >>> 8) & 0xff);
      bytes[start + 7] = (byte) (val & 0xff);
    }
  }

  public static byte[] utf16CharArrayToByteAray(char[] input, byte[] output) {
    for (int i = 0; i < input.length; i++) {
      int letter = input[i];
      output[i * 2] = (byte)((letter >>> 8) & 0xff);
      output[i * 2 + 1] = (byte)(letter & 0xff);
    }
    return output;
  }
  
  public static byte[] utf8CharArrayToByteAray(char[] input, byte[] output) {
    for (int i = 0; i < input.length; i++) {
      output[i] = (byte)(input[i] & 0xff);
    }
    return output;
  }
  
  public static byte[] intToBytes(int val, byte[] bytes, int start) {
    return intToBytes(val, bytes, start, false);
  }

  public static void xorIntArray(int[] ints, int val) {
    for (int i = 0; i < ints.length; i++) {
      ints[i] ^= val;
    }
  }

  public static byte[] intToBytes(int val, byte[] bytes, int start, boolean isLittleEndian) {
    if (isLittleEndian) {
      bytes[start + 3] = (byte) ((val >>> 24) & 0xff);
      bytes[start + 2] = (byte) ((val >>> 16) & 0xff);
      bytes[start + 1] = (byte) ((val >>> 8) & 0xff);
      bytes[start] = (byte) (val & 0xff);
    } else {
      bytes[start] = (byte) ((val >>> 24) & 0xff);
      bytes[start + 1] = (byte) ((val >>> 16) & 0xff);
      bytes[start + 2] = (byte) ((val >>> 8) & 0xff);
      bytes[start + 3] = (byte) (val & 0xff);
    }
    return bytes;
  }

  public static int[] intArrayFromBytes(byte[] bytes, int start, int length) {
    return intArrayFromBytes(bytes, start, length, false);
  }

  public static int[] intArrayFromBytes(byte[] bytes, int start, int length, boolean isLittleEndian) {
    int[] result = new int[length / 4];

    for (int i = 0; i < result.length; i++) {
      result[i] = CryptoUtils.intFromBytes(bytes, start + i * 4, isLittleEndian);
    }

    return result;
  }

  public static int intFromBytes(byte[] bytes, int start) {
    return CryptoUtils.intFromBytes(bytes, start, false);
  }

  /*
   */
  public static int circularlyExtractIntFromBytes(byte[] bytes, MutableInt start) {
    // big endian
    int startVal = start.intValue();
    int data = 0;
    for (int q = 0; q < 4; q++) {
      data = (data << 8) | (bytes[startVal] & 0xff);
      startVal = (startVal + 1) % bytes.length;
    }
    start.setValue(startVal);
    return data;
  }

  public static int intFromBytes(byte[] bytes, int start, boolean isLittleEndian) {
    if (isLittleEndian) {
      return ((bytes[start + 3] & 0xff) << 24) + ((bytes[start + 2] & 0xff) << 16) + ((bytes[start + 1] & 0xff) << 8)
          + (bytes[start] & 0xff);
    } else {
      return ((bytes[start] & 0xff) << 24) + ((bytes[start + 1] & 0xff) << 16) + ((bytes[start + 2] & 0xff) << 8)
          + (bytes[start + 3] & 0xff);
    }

  }

  public static long longFromBytes(byte[] bytes, int start) {
    return ((bytes[start] & 0xffL) << 56) + ((bytes[start + 1] & 0xffL) << 48) + ((bytes[start + 2] & 0xffL) << 40)
        + ((bytes[start + 3] & 0xffL) << 32) + ((bytes[start + 4] & 0xffL) << 24) + ((bytes[start + 5] & 0xffL) << 16)
        + ((bytes[start + 6] & 0xffL) << 8) + (bytes[start + 7] & 0xffL);
  }
  
  
  public static long safeLongFromBytes(byte[] bytes, int start) {
    long output = 0;
    
    int index = start;
    
    int shift = 56;
    
    while((bytes.length > index) && (shift >= 0)) {
      output +=(bytes[index] & 0xffL) << shift;
      index++;
      shift -= 8;
    }
    
    return output;
  }

  public static byte[] intArrayToByteArray(int[] ints) {
    return intArrayToByteArray(ints, false);
  }

  public static byte[] intArrayToByteArray(int[] ints, boolean isLittleEndian) {
    byte[] bytes = new byte[ints.length * 4];
    for (int i = 0; i < ints.length; i++) {
      intToBytes(ints[i], bytes, i * 4, isLittleEndian);
    }
    return bytes;
  }

  public static byte[] longArrayToByteArray(long[] longs) {
    byte[] bytes = new byte[longs.length * 8];
    for (int i = 0; i < longs.length; i++) {
      longToBytes(longs[i], bytes, i * 8);
    }
    return bytes;
  }

  public static String intArrayToHexString(int[] ints) {
    StringBuilder s = new StringBuilder();

    for (int i = 0; i < ints.length; i++) {
      s.append(String.format("%08x", ints[i]));
    }

    return s.toString();
  }

  public static String longArrayToHexString(long[] longs) {
    StringBuilder s = new StringBuilder();

    for (int i = 0; i < longs.length; i++) {

      s.append(String.format("%016x", longs[i]));
    }

    return s.toString();
  }

  public static String byteArrayToHexString(byte[] bytes) {
    return DatatypeConverter.printHexBinary(bytes).toLowerCase();
  }

  public static String hexStringToBinaryString(String hex) {
    String binaryBlock = hex.replace("0", "0000").replace("1", "0001").replace("2", "0010").replace("3", "0011")
        .replace("4", "0100").replace("5", "0101").replace("6", "0110").replace("7", "0111").replace("8", "1000")
        .replace("9", "1001").replace("a", "1010").replace("b", "1011").replace("c", "1100").replace("d", "1101")
        .replace("e", "1110").replace("f", "1111");

    StringBuilder binaryString = new StringBuilder();

    for (int i = 0; (i + 7) < binaryBlock.length(); i += 8) {
      if (i != 0) {
        binaryString.append(' ');
      }
      binaryString.append(binaryBlock.substring(i, i + 8));
    }

    return binaryString.toString();
  }

  public static String byteArrayToBinaryString(byte[] bytes) {
    return hexStringToBinaryString(byteArrayToHexString(bytes));
  }

  public static int intCh(int x, int y, int z) {
    return (x & y) ^ ((~x) & z);
  }

  public static int intParity(int x, int y, int z) {
    return x ^ y ^ z;
  }

  public static int intMaj(int x, int y, int z) {
    return (x & y) ^ (x & z) ^ (y & z);
  }

  public static long longCh(long x, long y, long z) {
    return (x & y) ^ ((~x) & z);
  }

  public static long longMaj(long x, long y, long z) {
    return (x & y) ^ (x & z) ^ (y & z);
  }

  public static long upperSigmaZeroFiveTwelve(long x) {
    return CryptoUtils.rotateLongRight(x, 28) ^ CryptoUtils.rotateLongRight(x, 34) ^ CryptoUtils.rotateLongRight(x, 39);
  }

  public static long upperSigmaOneFiveTwelve(long x) {
    return CryptoUtils.rotateLongRight(x, 14) ^ CryptoUtils.rotateLongRight(x, 18) ^ CryptoUtils.rotateLongRight(x, 41);
  }

  public static long lowerSigmaZeroFiveTwelve(long x) {
    return CryptoUtils.rotateLongRight(x, 1) ^ CryptoUtils.rotateLongRight(x, 8) ^ (x >>> 7);
  }

  public static long lowerSigmaOneFiveTwelve(long x) {
    return CryptoUtils.rotateLongRight(x, 19) ^ CryptoUtils.rotateLongRight(x, 61) ^ (x >>> 6);
  }

  public static byte[] xorByteArrays(byte[] a, byte[] b) {

    if (a.length != b.length) {
      throw new IllegalArgumentException("Cannot xor byte arrays of unequal lenghts.");
    }

    byte[] c = new byte[a.length];

    for (int i = 0; i < a.length; i++) {
      c[i] = (byte) ((a[i] ^ b[i]) & 0xff);
    }

    return c;
  }

  public static byte[] permuteByteArray(byte[] input, byte[] output, int[] permutation) {
    return permuteByteArray(input, 0, output, 0, permutation, input.length);
  }

  public static byte[] permuteByteArray(byte[] input, int srcPos, byte[] output, int destPos, int[] permutation,
      int length) {

    for (int i = 0; i < length; i++) {
      output[destPos + i] = input[srcPos + permutation[i]];
    }
    return output;
  }
  
  public static int getBitFromByteArray(byte[] bytes, int bitIndex) {
    int bitPos = bitIndex % 8;
    int firstN = bytes[bitIndex / 8] & FIRST_N_BITS[bitPos + 1];
    return ((bytes[bitIndex / 8] & FIRST_N_BITS[bitPos + 1]) >>> (7 - bitPos)) & 0x1;
  }
  
  public static byte[] permuteByteArrayByBit(byte[] input, int srcPos, byte[] output, int[] permutation) {
    int c = 0;
    int n = 0;    
    boolean going = true;
    while (going) {
      int val = 0;
      for (int i = 0; i < 8; i ++) {
        //we've exhausted all the bits
        if (c >= permutation.length) {
          going = false;
          break;
        }
        
        //add the new bit
        val |= getBitFromByteArray(input, srcPos + permutation[c]) << (7 - i);
        c++;
      }
      if (output.length > n) {
        output[n] = (byte)val;
        n++;
      }
    }
    
    return output;
  }
  
  public static int permuteIntByBit(int input, int srcPos, int[] permutation) {
    int output = 0;
    for(int n = 0; n < permutation.length; n ++) {
      output += ((input >>> (31-permutation[n]-srcPos)) & 0x1) << (31 - n);
    }
    return output;
  }
  
  
  public static long permuteIntByBitToLong(int input, int srcPos, int[] permutation) {
    long inputAsLong = input;
    long output = 0;
    for(int n = 0; n < permutation.length; n ++) {
      int leftShift = 31-permutation[n]-srcPos;
      long leftShifted = inputAsLong >> leftShift;
      long leftShiftedAndOne = leftShifted & 0x1;
      output += ((inputAsLong >>> (31-permutation[n]-srcPos)) & 0x1) << (63 - n);
    }
    return output;
  }

  public static byte[] copyBitsFromByteArray(byte[] input, int startingSrcBit, int bitLength, byte[] output,
      int startingDestBit) {

    int currentDestBit = startingDestBit;

    int currentSrcBit = startingSrcBit;

    int lastSrcBit = startingSrcBit + bitLength;

    int srcBitsLeft = lastSrcBit - currentSrcBit;

    while (srcBitsLeft > 0) {
      int currentDestByte = currentDestBit / 8;
      int currestDestByteBitPos = currentDestBit - currentDestByte * 8;
      int bitsLeftOnDestByte = 8 - currestDestByteBitPos;

      int currentSrcByte = currentSrcBit / 8;
      int currestSrcByteBitPos = currentSrcBit - currentSrcByte * 8;
      int bitsLeftOnSrcByte = 8 - currestSrcByteBitPos;

      int bitsFromCurrentBytes = getLastBitsFromBytes(input[currentSrcByte] & 0xff, currestSrcByteBitPos,
          bitsLeftOnSrcByte, currestDestByteBitPos);
      
      int bitsToOrWithDest = Math.min(bitsLeftOnDestByte, Math.min(bitsLeftOnSrcByte, srcBitsLeft));

      output[currentDestByte] |= (bitsFromCurrentBytes & (FIRST_N_BITS[bitsToOrWithDest] >> currestDestByteBitPos));

      currentDestBit += bitsToOrWithDest;
      currentSrcBit += bitsToOrWithDest;

      srcBitsLeft -= bitsToOrWithDest;
    }

    return output;
  }
  
  public static byte[] rotateByteArrayRight(byte[] input, int bitLength, int bitsRight, byte[] output) {
    return copyBitsFromByteArray(input, bitLength - bitsRight, bitsRight, copyBitsFromByteArray(input, 0, bitLength - bitsRight, output, bitsRight), 0);
  }
  
  public static byte[] rotateByteArrayLeft(byte[] input, int bitLength, int bitsLeft, byte[] output) {
    return copyBitsFromByteArray(input, bitsLeft, bitLength - bitsLeft, copyBitsFromByteArray(input, 0, bitsLeft, output, bitLength - bitsLeft), 0);
  }

  public static int getLastBitsFromBytes(int byteInt, int start, int len) {
    return getLastBitsFromBytes(byteInt, start, len, 0);
  }
  
  public static int getLastBitsFromBytes(int byteInt, int start, int len, int pos) {
    int res = byteInt & LAST_N_BITS[len];
    return leftOrRightBitShift(res, pos - start);
  }

  public static int leftOrRightBitShift(int src, int digitsToShiftRightIsPositive) {
    if (digitsToShiftRightIsPositive > 0) {
      return src >>> digitsToShiftRightIsPositive;
    } else {
      return src << (-digitsToShiftRightIsPositive);
    }
  }

}
