package core;

public class PairityBitCodec {

  public static byte[] encode(byte[] input, boolean isParityBitOdd) {
    int rem = input.length % 7;
    rem = (rem == 0) ? 0 : rem + 1;
    byte[] out = new byte[input.length / 7 * 8 + input.length % 7];

    int pairityBit = isParityBitOdd ? 1 : 0;

    int outIndex = 0;
    int inIndex = 0;

    while (true) {
      if (inIndex >= input.length) {
        break;
      }

      int one = input[inIndex];
      inIndex++;

      //first seven digits of one and the pairityBit
      int a = (one & 0xfe) | pairityBit;
      out[outIndex] = (byte)a;
      outIndex++;

      //last digit of one and the pairityBit
      int b = ((one << 7) & 0x80) | pairityBit;
      
      if (inIndex >= input.length) {
        out[outIndex] = (byte)b;
        break;
      }
      
      int two = input[inIndex];
      inIndex++;
      
      //first 6 digits of two
      b |= (two >>> 1) & 0x7e;
      out[outIndex] = (byte)b;
      outIndex++;
      
      //last 2 digits of two and the pairityBit
      int c = ((two << 6) & 0xc0) | pairityBit;
      
      if (inIndex >= input.length) {
        out[outIndex] = (byte)c;
        break;
      }
      
      int three = input[inIndex];
      inIndex++;
      
      //first 5 digits of three
      c |= (three >>> 2) & 0x3e;
      out[outIndex] = (byte)c;
      outIndex++;
      
      //last 3 digits of three and the pairityBit
      int d = ((three << 5) & 0xe0) | pairityBit;
      
      if (inIndex >= input.length) {
        out[outIndex] = (byte)d;
        break;
      }
      
      int four = input[inIndex];
      inIndex++;
      
      //first 4 digits of four
      d |= (four >>> 3) & 0x1e;
      out[outIndex] = (byte)d;
      outIndex++;
      
      //last 4 digits of four and the pairityBit
      int e = ((four << 4) & 0xf0) | pairityBit;
      
      if (inIndex >= input.length) {
        out[outIndex] = (byte)e;
        break;
      }
      
      int five = input[inIndex];
      inIndex++;
      
      //first 3 digits of five
      e |= (five >>> 4) & 0xe;
      out[outIndex] = (byte)e;
      outIndex++;
      
      //last 5 digits of five and the pairityBit
      int f = ((five << 3) & 0xf8) | pairityBit;
      
      if (inIndex >= input.length) {
        out[outIndex] = (byte)f;
        break;
      }
      
      int six = input[inIndex];
      inIndex++;
      
      //first 2 digits of six
      f |= (six >>> 5) & 0x6;
      out[outIndex] = (byte)f;
      outIndex++;
      
      //last six digits of six and the pairityBit
      int g = ((six << 2) & 0xfc) | pairityBit;
      
      if (inIndex >= input.length) {
        out[outIndex] = (byte)g;
        break;
      }
      
      int seven = input[inIndex];
      inIndex++;
      
      //first digit of seven
      g |= (seven >>> 6) & 0x2;
      out[outIndex] = (byte)g;
      outIndex++;
      
      //last seven digits of seven and the pairityBit
      int h = ((seven << 1) & 0xfe) | pairityBit;
      out[outIndex] = (byte)h;
      outIndex++;
    }

    return out;
  }

  public static byte[] decode(byte[] input, boolean isParityBitOdd) {
    byte[] out = new byte[input.length / 8 * 7 + input.length % 8];

    boolean failedPairity = false;

    int outIndex = 0;
    int inIndex = 0;

    while (true) {
      if (inIndex >= input.length) {
        break;
      }

      int a = input[inIndex];
      inIndex++;
      if (!verifyPairity(a, isParityBitOdd)) {
        failedPairity = true;
        break;
      }

      // first seven digits of a
      int one = a & 0xfe;

      if (inIndex >= input.length) {
        out[outIndex] = (byte) one;
        break;
      }

      int b = input[inIndex];
      inIndex++;
      if (!verifyPairity(b, isParityBitOdd)) {
        failedPairity = true;
        break;
      }

      // the first digit of b
      one |= (b >>> 7) & 0x1;

      out[outIndex] = (byte) one;
      outIndex++;

      // the middle six digits of b
      int two = (b & 0x7e) << 1;

      if (inIndex >= input.length) {
        out[outIndex] = (byte) two;
        break;
      }

      int c = input[inIndex];
      inIndex++;
      if (!verifyPairity(c, isParityBitOdd)) {
        failedPairity = true;
        break;
      }

      // first 2 digits of c
      two |= (c >>> 6) & 0x3;

      out[outIndex] = (byte) two;
      outIndex++;

      // the 3rd through 7th digit of c

      int three = (c << 2) & 0xf8;

      if (inIndex >= input.length) {
        out[outIndex] = (byte) three;
        break;
      }

      int d = input[inIndex];
      inIndex++;
      if (!verifyPairity(d, isParityBitOdd)) {
        failedPairity = true;
        break;
      }

      // the first three digits of d
      three |= (d >>> 5) & 0x7;

      out[outIndex] = (byte) three;
      outIndex++;

      // the 4th through 7th digits of d
      int four = (d << 3) & 0xf0;

      if (inIndex >= input.length) {
        out[outIndex] = (byte) four;
        break;
      }

      int e = input[inIndex];
      inIndex++;
      if (!verifyPairity(e, isParityBitOdd)) {
        failedPairity = true;
        break;
      }

      // the first four bytes of e
      four |= (e >>> 4) & 0xf;
      out[outIndex] = (byte) four;
      outIndex++;

      // the 5th through 7th digits of e
      int five = (e << 4) & 0xe0;

      if (inIndex >= input.length) {
        out[outIndex] = (byte) five;
        break;
      }

      int f = input[inIndex];
      inIndex++;
      if (!verifyPairity(f, isParityBitOdd)) {
        failedPairity = true;
        break;
      }

      // the first five digits of f
      five |= (f >>> 3) & 0x1f;
      out[outIndex] = (byte) five;
      outIndex++;

      // the sixth and seventh digits of f
      int six = (f << 5) & 0xc0;

      if (inIndex >= input.length) {
        out[outIndex] = (byte) six;
        break;
      }

      int g = input[inIndex];
      inIndex++;
      if (!verifyPairity(g, isParityBitOdd)) {
        failedPairity = true;
        break;
      }

      // the first 6 bytes of g
      six |= (g >>> 2) & 0x3f;
      out[outIndex] = (byte) six;
      outIndex++;

      // the seventh digit of g
      int seven = (g << 6) & 0x80;

      if (inIndex >= input.length) {
        out[outIndex] = (byte) seven;
        break;
      }

      int h = input[inIndex];
      inIndex++;
      if (!verifyPairity(h, isParityBitOdd)) {
        failedPairity = true;
        break;
      }

      // the first 7 bytes of h
      seven |= (h >>> 1) & 0x7f;
      out[outIndex] = (byte) seven;
      outIndex++;
    }

    if (failedPairity) {
      throw new IllegalArgumentException("Pairity does not match!");
    }

    return out;
  }

  public static boolean verifyPairity(int b, boolean isParityBitOdd) {
    return verifyPairity((byte) b, isParityBitOdd);
  }

  public static boolean verifyPairity(byte b, boolean isParityBitOdd) {
    // last bit of the byte
    int parityBit = b & 0x1;
    // if the parityBit is 1 and isParityBitOdd is true or
    // parityBit is 1 and isParityBitOdd is false
    return (parityBit == 1) == isParityBitOdd;
  }
}
