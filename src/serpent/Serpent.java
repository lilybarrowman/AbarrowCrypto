package serpent;

import core.BlockCipher;
import core.CryptoUtils;

public class Serpent extends BlockCipher {

  private static final int BLOCK_BYTES = 16;

  private static final int ROUNDS = 32;

  private static final int[] KEY_LENGTHS = new int[] { 16, 24, 32 };

  private static final int PHI = 0x9e3779b9;

  private int[] b;

  private int[][] roundKeys;

  private int t1;
  private int t2;
  private int t3;
  private int t4;
  private int t5;
  private int t6;
  private int t7;
  private int t8;
  private int t9;
  private int t10;
  private int t11;
  private int t12;
  private int t13;
  private int t14;
  private int t15;
  private int t16;

  public Serpent(byte[] key) {

    byte[] fullKey;

    if (key.length == 32) {
      fullKey = key;
    } else if ((key.length == 16) || (key.length == 24)) {
      fullKey = new byte[32];
      System.arraycopy(key, 0, fullKey, 0, key.length);
      fullKey[key.length] = 1;// CryptoUtils.ONE_AND_SEVEN_ZEROES_BYTE;
    } else {
      throw new IllegalArgumentException("The supplied key is not a valid length;");
    }

    b = new int[4];

    int[] roundKeyWords = CryptoUtils.intArrayFromBytes(new int[140], 0, fullKey, 0, 32, true);

    for (int i = 8; i < 140; i++) {
      roundKeyWords[i] = CryptoUtils.rotateIntLeft(roundKeyWords[i - 8] ^ roundKeyWords[i - 5] ^ roundKeyWords[i - 3]
          ^ roundKeyWords[i - 1] ^ Serpent.PHI ^ (i - 8), 11);
    }

    roundKeys = new int[33][];

    for (int i = 0; i < 33; i++) {
      roundKeys[i] = S(new int[] { roundKeyWords[i * 4 + 8], roundKeyWords[i * 4 + 9], roundKeyWords[i * 4 + 10],
          roundKeyWords[i * 4 + 11] }, 35 - i);
    }
    clearTs();
  }

  private void clearTs() {
    t1 = 0;
    t2 = 0;
    t3 = 0;
    t4 = 0;
    t5 = 0;
    t6 = 0;
    t7 = 0;
    t8 = 0;
    t9 = 0;
    t10 = 0;
    t11 = 0;
    t12 = 0;
    t13 = 0;
    t14 = 0;
    t15 = 0;
    t16 = 0;
  }

  @Override
  public int getBlockBytes() {
    return Serpent.BLOCK_BYTES;
  }

  public int[] getValidKeyLengths() {
    return Serpent.KEY_LENGTHS;
  }

  private int[] S(int[] a, int round) {

    int r = round % 8;

    if (r == 0) {
      return sb0(a);
    } else if (r == 1) {
      return sb1(a);
    } else if (r == 2) {
      return sb2(a);
    } else if (r == 3) {
      return sb3(a);
    } else if (r == 4) {
      return sb4(a);
    } else if (r == 5) {
      return sb5(a);
    } else if (r == 6) {
      return sb6(a);
    } else {
      return sb7(a);
    }
  }

  private int[] inverseS(int[] a, int round) {

    int r = round % 8;

    if (r == 0) {
      return ib0(a);
    } else if (r == 1) {
      return ib1(a);
    } else if (r == 2) {
      return ib2(a);
    } else if (r == 3) {
      return ib3(a);
    } else if (r == 4) {
      return ib4(a);
    } else if (r == 5) {
      return ib5(a);
    } else if (r == 6) {
      return ib6(a);
    } else {
      return ib7(a);
    }
  }

  private int[] sb0(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = a ^ d;
    t2 = a & d;
    t3 = c ^ t1;
    t6 = b & t1;
    t4 = b ^ t3;
    t10 = ~t3;
    int h = t2 ^ t4;
    t7 = a ^ t6;
    t14 = ~t7;
    t8 = c | t7;
    t11 = t3 ^ t7;
    int g = t4 ^ t8;
    t12 = h & t11;
    int f = t10 ^ t12;
    int e = t12 ^ t14;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] ib0(int[] x) {
    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = ~a;
    t2 = a ^ b;
    t3 = t1 | t2;
    t4 = d ^ t3;
    t7 = d & t2;
    t5 = c ^ t4;
    t8 = t1 ^ t7;
    int g = t2 ^ t5;
    t11 = a & t4;
    t9 = g & t8;
    t14 = t5 ^ t8;
    int f = t4 ^ t9;
    t12 = t5 | f;
    int h = t11 ^ t12;
    int e = h ^ t14;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] sb1(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = ~a;
    t2 = b ^ t1;
    t3 = a | t2;
    t4 = d | t2;
    t5 = c ^ t3;
    int g = d ^ t5;
    t7 = b ^ t4;
    t8 = t2 ^ g;
    t9 = t5 & t7;
    int h = t8 ^ t9;
    t11 = t5 ^ t7;
    int f = h ^ t11;
    t13 = t8 & t11;
    int e = t5 ^ t13;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] ib1(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = a ^ d;
    t2 = a & b;
    t3 = b ^ c;
    t4 = a ^ t3;
    t5 = b | d;
    t7 = c | t1;
    int h = t4 ^ t5;
    t8 = b ^ t7;
    t11 = ~t2;
    t9 = t4 & t8;
    int f = t1 ^ t9;
    t13 = t9 ^ t11;
    t12 = h & f;
    int g = t12 ^ t13;
    t15 = a & d;
    t16 = c ^ t13;
    int e = t15 ^ t16;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] sb2(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = ~a;
    t2 = b ^ d;
    t3 = c & t1;
    t13 = d | t1;
    int e = t2 ^ t3;
    t5 = c ^ t1;
    t6 = c ^ e;
    t7 = b & t6;
    t10 = e | t5;
    int h = t5 ^ t7;
    t9 = d | t7;
    t11 = t9 & t10;
    t14 = t2 ^ h;
    int g = a ^ t11;
    t15 = g ^ t13;
    int f = t14 ^ t15;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] ib2(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = b ^ d;
    t2 = ~t1;
    t3 = a ^ c;
    t4 = c ^ t1;
    t7 = a | t2;
    t5 = b & t4;
    t8 = d ^ t7;
    t11 = ~t4;
    int e = t3 ^ t5;
    t9 = t3 | t8;
    t14 = d & t11;
    int h = t1 ^ t9;
    t12 = e | h;
    int f = t11 ^ t12;
    t15 = t3 ^ t12;
    int g = t14 ^ t15;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] sb3(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = a ^ c;
    t2 = d ^ t1;
    t3 = a & t2;
    t4 = d ^ t3;
    t5 = b & t4;
    int g = t2 ^ t5;
    t7 = a | g;
    t8 = b | d;
    t11 = a | d;
    t9 = t4 & t7;
    int f = t8 ^ t9;
    t12 = b ^ t11;
    t13 = g ^ t9;
    t15 = t3 ^ t8;
    int h = t12 ^ t13;
    t16 = c & t15;
    int e = t12 ^ t16;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] ib3(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = b ^ c;
    t2 = b | c;
    t3 = a ^ c;
    t7 = a ^ d;
    t4 = t2 ^ t3;
    t5 = d | t4;
    t9 = t2 ^ t7;
    int e = t1 ^ t5;
    t8 = t1 | t5;
    t11 = a & t4;
    int g = t8 ^ t9;
    t12 = e | t9;
    int f = t11 ^ t12;
    t14 = a & g;
    t15 = t2 ^ t14;
    t16 = e & t15;
    int h = t4 ^ t16;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] sb4(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = a ^ d;
    t2 = d & t1;
    t3 = c ^ t2;
    t4 = b | t3;
    int h = t1 ^ t4;
    t6 = ~b;
    t7 = t1 | t6;
    int e = t3 ^ t7;
    t9 = a & e;
    t10 = t1 ^ t6;
    t11 = t4 & t10;
    int g = t9 ^ t11;
    t13 = a ^ t3;
    t14 = t10 & g;
    int f = t13 ^ t14;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] ib4(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = c ^ d;
    t2 = c | d;
    t3 = b ^ t2;
    t4 = a & t3;
    int f = t1 ^ t4;
    t6 = a ^ d;
    t7 = b | d;
    t8 = t6 & t7;
    int h = t3 ^ t8;
    t10 = ~a;
    t11 = c ^ h;
    t12 = t10 | t11;
    int e = t3 ^ t12;
    t14 = c | t4;
    t15 = t7 ^ t14;
    t16 = h | t10;
    int g = t15 ^ t16;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] sb5(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = ~a;
    t2 = a ^ b;
    t3 = a ^ d;
    t4 = c ^ t1;
    t5 = t2 | t3;
    int e = t4 ^ t5;
    t7 = d & e;
    t8 = t2 ^ e;
    t10 = t1 | e;
    int f = t7 ^ t8;
    t11 = t2 | t7;
    t12 = t3 ^ t10;
    t14 = b ^ t7;
    int g = t11 ^ t12;
    t15 = f & t12;
    int h = t14 ^ t15;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] ib5(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = ~c;
    t2 = b & t1;
    t3 = d ^ t2;
    t4 = a & t3;
    t5 = b ^ t1;
    int h = t4 ^ t5;
    t7 = b | h;
    t8 = a & t7;
    int f = t3 ^ t8;
    t10 = a | d;
    t11 = t1 ^ t7;
    int e = t10 ^ t11;
    t13 = a ^ c;
    t14 = b & t10;
    t15 = t4 | t13;
    int g = t14 ^ t15;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] sb6(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = ~a;
    t2 = a ^ d;
    t3 = b ^ t2;
    t4 = t1 | t2;
    t5 = c ^ t4;
    int f = b ^ t5;
    t13 = ~t5;
    t7 = t2 | f;
    t8 = d ^ t7;
    t9 = t5 & t8;
    int g = t3 ^ t9;
    t11 = t5 ^ t8;
    int e = g ^ t11;
    t14 = t3 & t11;
    int h = t13 ^ t14;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] ib6(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = ~a;
    t2 = a ^ b;
    t3 = c ^ t2;
    t4 = c | t1;
    t5 = d ^ t4;
    t13 = d & t1;
    int f = t3 ^ t5;
    t7 = t3 & t5;
    t8 = t2 ^ t7;
    t9 = b | t8;
    int h = t5 ^ t9;
    t11 = b | h;
    int e = t8 ^ t11;
    t14 = t3 ^ t11;
    int g = t13 ^ t14;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] sb7(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = ~c;
    t2 = b ^ c;
    t3 = b | t1;
    t4 = d ^ t3;
    t5 = a & t4;
    t7 = a ^ d;
    int h = t2 ^ t5;
    t8 = b ^ t5;
    t9 = t2 | t8;
    t11 = d & t3;
    int f = t7 ^ t9;
    t12 = t5 ^ f;
    t15 = t1 | t4;
    t13 = h & t12;
    int g = t11 ^ t13;
    t16 = t12 ^ g;
    int e = t15 ^ t16;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] ib7(int[] x) {

    int a = x[0];
    int b = x[1];
    int c = x[2];
    int d = x[3];

    t1 = a & b;
    t2 = a | b;
    t3 = c | t1;
    t4 = d & t2;
    int h = t3 ^ t4;
    t6 = ~d;
    t7 = b ^ t4;
    t8 = h ^ t6;
    t11 = c ^ t7;
    t9 = t7 | t8;
    int f = a ^ t9;
    t12 = d | f;
    int e = t11 ^ t12;
    t14 = a & h;
    t15 = t3 ^ f;
    t16 = e ^ t14;
    int g = t15 ^ t16;

    x[0] = e;
    x[1] = f;
    x[2] = g;
    x[3] = h;
    return x;
  }

  private int[] L(int[] a) {
    int x0 = a[0];
    int x1 = a[1];
    int x2 = a[2];
    int x3 = a[3];

    x0 = CryptoUtils.rotateIntLeft(x0, 13);
    x2 = CryptoUtils.rotateIntLeft(x2, 3);
    x1 ^= x0 ^ x2;
    x3 ^= x2 ^ (x0 << 3);
    x1 = CryptoUtils.rotateIntLeft(x1, 1);
    x3 = CryptoUtils.rotateIntLeft(x3, 7);
    x0 ^= x1 ^ x3;
    x2 ^= x3 ^ (x1 << 7);
    x0 = CryptoUtils.rotateIntLeft(x0, 5);
    x2 = CryptoUtils.rotateIntLeft(x2, 22);

    a[0] = x0;
    a[1] = x1;
    a[2] = x2;
    a[3] = x3;
    return a;
  }

  private int[] inverseL(int[] a) {
    int x0 = a[0];
    int x1 = a[1];
    int x2 = a[2];
    int x3 = a[3];

    x2 = CryptoUtils.rotateIntRight(x2, 22);
    x0 = CryptoUtils.rotateIntRight(x0, 5);
    x2 ^= x3 ^ (x1 << 7);
    x0 ^= x1 ^ x3;
    x3 = CryptoUtils.rotateIntRight(x3, 7);
    x1 = CryptoUtils.rotateIntRight(x1, 1);
    x3 ^= x2 ^ (x0 << 3);
    x1 ^= x0 ^ x2;
    x2 = CryptoUtils.rotateIntRight(x2, 3);
    x0 = CryptoUtils.rotateIntRight(x0, 13);

    a[0] = x0;
    a[1] = x1;
    a[2] = x2;
    a[3] = x3;
    return a;
  }

  @Override
  public void encryptBlock(byte[] input, int srcPos, byte[] output, int destPos) {
    CryptoUtils.intArrayFromBytes(b, 0, input, srcPos, 16, true);

    for (int r = 0; r < (Serpent.ROUNDS - 1); r++) {
      CryptoUtils.xorIntArray(b, roundKeys[r], b);
      S(b, r);
      L(b);
    }

    CryptoUtils.xorIntArray(b, roundKeys[31], b);
    S(b, 31);
    CryptoUtils.xorIntArray(b, roundKeys[32], b);

    CryptoUtils.intArrayToByteArray(output, destPos, b, true);

    CryptoUtils.fillWithZeroes(b);
    clearTs();
  }

  @Override
  public void decryptBlock(byte[] input, int srcPos, byte[] output, int destPos) {
    CryptoUtils.intArrayFromBytes(b, 0, input, srcPos, 16, true);

    CryptoUtils.xorIntArray(b, roundKeys[32], b);
    inverseS(b, 31);
    CryptoUtils.xorIntArray(b, roundKeys[31], b);

    for (int r = (Serpent.ROUNDS - 2); r >= 0; r--) {
      inverseL(b);
      inverseS(b, r);
      CryptoUtils.xorIntArray(b, roundKeys[r], b);
    }

    CryptoUtils.intArrayToByteArray(output, destPos, b, true);

    CryptoUtils.fillWithZeroes(b);
    clearTs();
  }

}
