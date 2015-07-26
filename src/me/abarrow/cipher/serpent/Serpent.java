package me.abarrow.cipher.serpent;

import java.util.Arrays;

import me.abarrow.cipher.BlockCipher;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;

public class Serpent extends BlockCipher {

  private static final int BLOCK_BYTES = 16;

  private static final int ROUNDS = 32;

  private static final int[] KEY_LENGTHS = new int[] { 16, 24, 32 };

  private static final int PHI = 0x9e3779b9;

  private int[][] roundKeys;
  
  public Serpent() {
  }

  public Serpent(byte[] key) {
    setKey(key);
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

    int t1 = a ^ d;
    int t2 = a & d;
    int t3 = c ^ t1;
    int t6 = b & t1;
    int t4 = b ^ t3;
    int t10 = ~t3;
    int h = t2 ^ t4;
    int t7 = a ^ t6;
    int t14 = ~t7;
    int t8 = c | t7;
    int t11 = t3 ^ t7;
    int g = t4 ^ t8;
    int t12 = h & t11;
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

    int t1 = ~a;
    int t2 = a ^ b;
    int t3 = t1 | t2;
    int t4 = d ^ t3;
    int t7 = d & t2;
    int t5 = c ^ t4;
    int t8 = t1 ^ t7;
    int g = t2 ^ t5;
    int t11 = a & t4;
    int t9 = g & t8;
    int t14 = t5 ^ t8;
    int f = t4 ^ t9;
    int t12 = t5 | f;
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

    int t1 = ~a;
    int t2 = b ^ t1;
    int t3 = a | t2;
    int t4 = d | t2;
    int t5 = c ^ t3;
    int g = d ^ t5;
    int t7 = b ^ t4;
    int t8 = t2 ^ g;
    int t9 = t5 & t7;
    int h = t8 ^ t9;
    int t11 = t5 ^ t7;
    int f = h ^ t11;
    int t13 = t8 & t11;
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

    int t1 = a ^ d;
    int t2 = a & b;
    int t3 = b ^ c;
    int t4 = a ^ t3;
    int t5 = b | d;
    int t7 = c | t1;
    int h = t4 ^ t5;
    int t8 = b ^ t7;
    int t11 = ~t2;
    int t9 = t4 & t8;
    int f = t1 ^ t9;
    int t13 = t9 ^ t11;
    int t12 = h & f;
    int g = t12 ^ t13;
    int t15 = a & d;
    int t16 = c ^ t13;
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

    int t1 = ~a;
    int t2 = b ^ d;
    int t3 = c & t1;
    int t13 = d | t1;
    int e = t2 ^ t3;
    int t5 = c ^ t1;
    int t6 = c ^ e;
    int t7 = b & t6;
    int t10 = e | t5;
    int h = t5 ^ t7;
    int t9 = d | t7;
    int t11 = t9 & t10;
    int t14 = t2 ^ h;
    int g = a ^ t11;
    int t15 = g ^ t13;
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

    int t1 = b ^ d;
    int t2 = ~t1;
    int t3 = a ^ c;
    int t4 = c ^ t1;
    int t7 = a | t2;
    int t5 = b & t4;
    int t8 = d ^ t7;
    int t11 = ~t4;
    int e = t3 ^ t5;
    int t9 = t3 | t8;
    int t14 = d & t11;
    int h = t1 ^ t9;
    int t12 = e | h;
    int f = t11 ^ t12;
    int t15 = t3 ^ t12;
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

    int t1 = a ^ c;
    int t2 = d ^ t1;
    int t3 = a & t2;
    int t4 = d ^ t3;
    int t5 = b & t4;
    int g = t2 ^ t5;
    int t7 = a | g;
    int t8 = b | d;
    int t11 = a | d;
    int t9 = t4 & t7;
    int f = t8 ^ t9;
    int t12 = b ^ t11;
    int t13 = g ^ t9;
    int t15 = t3 ^ t8;
    int h = t12 ^ t13;
    int t16 = c & t15;
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

    int t1 = b ^ c;
    int t2 = b | c;
    int t3 = a ^ c;
    int t7 = a ^ d;
    int t4 = t2 ^ t3;
    int t5 = d | t4;
    int t9 = t2 ^ t7;
    int e = t1 ^ t5;
    int t8 = t1 | t5;
    int t11 = a & t4;
    int g = t8 ^ t9;
    int t12 = e | t9;
    int f = t11 ^ t12;
    int t14 = a & g;
    int t15 = t2 ^ t14;
    int t16 = e & t15;
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

    int t1 = a ^ d;
    int t2 = d & t1;
    int t3 = c ^ t2;
    int t4 = b | t3;
    int h = t1 ^ t4;
    int t6 = ~b;
    int t7 = t1 | t6;
    int e = t3 ^ t7;
    int t9 = a & e;
    int t10 = t1 ^ t6;
    int t11 = t4 & t10;
    int g = t9 ^ t11;
    int t13 = a ^ t3;
    int t14 = t10 & g;
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

    int t1 = c ^ d;
    int t2 = c | d;
    int t3 = b ^ t2;
    int t4 = a & t3;
    int f = t1 ^ t4;
    int t6 = a ^ d;
    int t7 = b | d;
    int t8 = t6 & t7;
    int h = t3 ^ t8;
    int t10 = ~a;
    int t11 = c ^ h;
    int t12 = t10 | t11;
    int e = t3 ^ t12;
    int t14 = c | t4;
    int t15 = t7 ^ t14;
    int t16 = h | t10;
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

    int t1 = ~a;
    int t2 = a ^ b;
    int t3 = a ^ d;
    int t4 = c ^ t1;
    int t5 = t2 | t3;
    int e = t4 ^ t5;
    int t7 = d & e;
    int t8 = t2 ^ e;
    int t10 = t1 | e;
    int f = t7 ^ t8;
    int t11 = t2 | t7;
    int t12 = t3 ^ t10;
    int t14 = b ^ t7;
    int g = t11 ^ t12;
    int t15 = f & t12;
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

    int t1 = ~c;
    int t2 = b & t1;
    int t3 = d ^ t2;
    int t4 = a & t3;
    int t5 = b ^ t1;
    int h = t4 ^ t5;
    int t7 = b | h;
    int t8 = a & t7;
    int f = t3 ^ t8;
    int t10 = a | d;
    int t11 = t1 ^ t7;
    int e = t10 ^ t11;
    int t13 = a ^ c;
    int t14 = b & t10;
    int t15 = t4 | t13;
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

    int t1 = ~a;
    int t2 = a ^ d;
    int t3 = b ^ t2;
    int t4 = t1 | t2;
    int t5 = c ^ t4;
    int f = b ^ t5;
    int t13 = ~t5;
    int t7 = t2 | f;
    int t8 = d ^ t7;
    int t9 = t5 & t8;
    int g = t3 ^ t9;
    int t11 = t5 ^ t8;
    int e = g ^ t11;
    int t14 = t3 & t11;
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

    int t1 = ~a;
    int t2 = a ^ b;
    int t3 = c ^ t2;
    int t4 = c | t1;
    int t5 = d ^ t4;
    int t13 = d & t1;
    int f = t3 ^ t5;
    int t7 = t3 & t5;
    int t8 = t2 ^ t7;
    int t9 = b | t8;
    int h = t5 ^ t9;
    int t11 = b | h;
    int e = t8 ^ t11;
    int t14 = t3 ^ t11;
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

    int t1 = ~c;
    int t2 = b ^ c;
    int t3 = b | t1;
    int t4 = d ^ t3;
    int t5 = a & t4;
    int t7 = a ^ d;
    int h = t2 ^ t5;
    int t8 = b ^ t5;
    int t9 = t2 | t8;
    int t11 = d & t3;
    int f = t7 ^ t9;
    int t12 = t5 ^ f;
    int t15 = t1 | t4;
    int t13 = h & t12;
    int g = t11 ^ t13;
    int t16 = t12 ^ g;
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

    int t1 = a & b;
    int t2 = a | b;
    int t3 = c | t1;
    int t4 = d & t2;
    int h = t3 ^ t4;
    int t6 = ~d;
    int t7 = b ^ t4;
    int t8 = h ^ t6;
    int t11 = c ^ t7;
    int t9 = t7 | t8;
    int f = a ^ t9;
    int t12 = d | f;
    int e = t11 ^ t12;
    int t14 = a & h;
    int t15 = t3 ^ f;
    int t16 = e ^ t14;
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
  public byte[] encryptBlock(byte[] input, int srcPos, byte[] output, int destPos) throws CryptoException {
    if (!hasKey()) {
      throw new CryptoException(CryptoException.NO_KEY);
    }
    int[] b = CryptoUtils.intArrayFromBytes(new int[4], 0, input, srcPos, 16, true);

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
    return output;
  }

  @Override
  public byte[] decryptBlock(byte[] input, int srcPos, byte[] output, int destPos) throws CryptoException {
    if (!hasKey()) {
      throw new CryptoException(CryptoException.NO_KEY);
    }
    int[] b = CryptoUtils.intArrayFromBytes(new int[4], 0, input, srcPos, 16, true);

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
    return output;
  }

  @Override
  public void removeKey() {
    if (!hasKey()) {
      return;
    }
    for (int i = 0; i < 33; i++) {
      CryptoUtils.fillWithZeroes(roundKeys[i]);
    }
    roundKeys = null;
  }

  @Override
  public void setKey(byte[] key) {
    removeKey();
    byte[] fullKey;

    if (key.length == 32) {
      fullKey = key;
    } else {
      fullKey = Arrays.copyOf(key, 32);
      if (key.length < 32) {
        fullKey[key.length] = 1;
      }
    }

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
  }

  @Override
  public boolean hasKey() {
    return roundKeys != null;
  }

}
