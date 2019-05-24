package me.abarrow.cipher.mode;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import me.abarrow.cipher.aes.AES;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.math.Int128;

public class GCMModeTest {

  @Test
  public void testGhashTestCase4() {
    Int128 H = Int128.parseLittleBitEndianHex("b83b533708bf535d0aa6e52980d53b78");
    byte[] A = CryptoUtils.parseHexString("feedfacedeadbeeffeedfacedeadbeefabaddad2");
    byte[] C = CryptoUtils.parseHexString("42831ec2217774244b7221b784d0d49ce3aa212f2c02a4e035c17e2329aca12e21d514b25466931c7d8f6a5aac84aa051ba30b396a0aac973d58e091");
    Int128 expectedHash = Int128.parseLittleBitEndianHex("698e57f70e6ecc7fd9463b7260a9ae5f");
    Int128 hash = new Int128();
    GCMMode.ghash(H, A, C, hash);
    assertArrayEquals(expectedHash.getWordsCopy(), hash.getWordsCopy());
  }
  
  @Test
  public void testGhashTestCase2() {
    Int128 H = Int128.parseLittleBitEndianHex("66e94bd4ef8a2c3b884cfa59ca342b2e");
    byte[] A = new byte[0];
    byte[] C = CryptoUtils.parseHexString("0388dace60b6a392f328c2b971b2fe78");
    Int128 expectedHash = Int128.parseLittleBitEndianHex("f38cbb1ad69223dcc3457ae5b6b0f885");
    Int128 hash = new Int128();
    GCMMode.ghash(H, A, C, hash);
    assertArrayEquals(expectedHash.getWordsCopy(), hash.getWordsCopy());
  }
  
  @Test
  public void testGhashTestCase18IV() {
    Int128 H = Int128.parseLittleBitEndianHex("acbef20579b4b8ebce889bac8732dad7");
    byte[] A = new byte[0];
    byte[] IV = CryptoUtils.parseHexString("9313225df88406e555909c5aff5269aa6a7a9538534f7da1e4c303d2a318a728c3c0c95156809539fcf0e2429a6b525416aedbf5a0de6a57a637b39b");
    Int128 y0Expected = Int128.parseLittleBitEndianHex("0cd953e2140a5976079f8e2406bc8eb4");
    Int128 y0 = new Int128();
    GCMMode.ghash(H, A, IV, y0);
    assertArrayEquals(y0Expected.getWordsCopy(), y0.getWordsCopy());
  }
  
  @Test
  public void testGCMModeTestCase1() throws CryptoException, IOException {
    byte[] key = CryptoUtils.parseHexString("00000000000000000000000000000000");
    byte[] iv = CryptoUtils.parseHexString("000000000000000000000000");
    byte[] tExpected = CryptoUtils.parseHexString("58e2fccefa7e3061367f1d57a4e7455a");

    fullTestCase(key, iv, new byte[0], new byte[0], new byte[0], tExpected);
  }
  
  @Test
  public void testGCMModeTestCase2() throws CryptoException, IOException {
    byte[] key = CryptoUtils.parseHexString("00000000000000000000000000000000");
    byte[] iv = CryptoUtils.parseHexString("000000000000000000000000");
    byte[] plain = CryptoUtils.parseHexString("00000000000000000000000000000000");
    byte[] cipherExpected = CryptoUtils.parseHexString("0388dace60b6a392f328c2b971b2fe78");
    byte[] tExpected = CryptoUtils.parseHexString("ab6e47d42cec13bdf53a67b21257bddf");
    
    fullTestCase(key, iv, plain, null, cipherExpected, tExpected);

  }
  
  @Test
  public void testGCMModeTestCase3() throws CryptoException, IOException {
    byte[] key = CryptoUtils.parseHexString("feffe9928665731c6d6a8f9467308308");
    byte[] iv = CryptoUtils.parseHexString("cafebabefacedbaddecaf888");
    byte[] plain = CryptoUtils.parseHexString(
        "d9313225f88406e5a55909c5aff5269a" + 
        "86a7a9531534f7da2e4c303d8a318a72" + 
        "1c3c0c95956809532fcf0e2449a6b525" + 
        "b16aedf5aa0de657ba637b391aafd255");
    byte[] cipherExpected = CryptoUtils.parseHexString(
        "42831ec2217774244b7221b784d0d49c" + 
        "e3aa212f2c02a4e035c17e2329aca12e" + 
        "21d514b25466931c7d8f6a5aac84aa05" + 
        "1ba30b396a0aac973d58e091473f5985");
    byte[] tExpected = CryptoUtils.parseHexString("4d5c2af327cd64a62cf35abd2ba6fab4");
    
    fullTestCase(key, iv, plain, new byte[0], cipherExpected, tExpected);
  }
  
  @Test
  public void testGCMModeTestCase4() throws CryptoException, IOException {
    byte[] key = CryptoUtils.parseHexString("feffe9928665731c6d6a8f9467308308");
    byte[] iv = CryptoUtils.parseHexString("cafebabefacedbaddecaf888");
    byte[] plain = CryptoUtils.parseHexString(
        "d9313225f88406e5a55909c5aff5269a" + 
        "86a7a9531534f7da2e4c303d8a318a72" + 
        "1c3c0c95956809532fcf0e2449a6b525" + 
        "b16aedf5aa0de657ba637b39");
    byte[] authData = CryptoUtils.parseHexString(
        "feedfacedeadbeeffeedfacedeadbeef" + 
        "abaddad2");
    byte[] cipherExpected = CryptoUtils.parseHexString(
        "42831ec2217774244b7221b784d0d49c" + 
        "e3aa212f2c02a4e035c17e2329aca12e" + 
        "21d514b25466931c7d8f6a5aac84aa05" + 
        "1ba30b396a0aac973d58e091");
    byte[] tExpected = CryptoUtils.parseHexString("5bc94fbc3221a5db94fae95ae7121a47");
    
    fullTestCase(key, iv, plain, authData, cipherExpected, tExpected);
  }
  
  @Test(expected = IOException.class)
  public void testGCMModeTestCase4CorruptTag() throws CryptoException, IOException {
    byte[] key = CryptoUtils.parseHexString("feffe9928665731c6d6a8f9467308308");
    byte[] iv = CryptoUtils.parseHexString("cafebabefacedbaddecaf888");
    byte[] plain = CryptoUtils.parseHexString(
        "d9313225f88406e5a55909c5aff5269a" + 
        "86a7a9531534f7da2e4c303d8a318a72" + 
        "1c3c0c95956809532fcf0e2449a6b525" + 
        "b16aedf5aa0de657ba637b39");
    byte[] authData = CryptoUtils.parseHexString(
        "feedfacedeadbeeffeedfacedeadbeef" + 
        "abaddad2");
    byte[] cipherExpected = CryptoUtils.parseHexString(
        "42831ec2217774244b7221b784d0d49c" + 
        "e3aa212f2c02a4e035c17e2329aca12e" + 
        "21d514b25466931c7d8f6a5aac84aa05" + 
        "1ba30b396a0aac973d58e091");
    byte[] tIncorrect = CryptoUtils.parseHexString("5bc94fbc3223a5db94fae95ae7121a47");
    byte[] outIncorrect = CryptoUtils.concatArrays(cipherExpected, tIncorrect);
    
    new GCMMode(new AES(key)).setAuthData(authData).setIV(iv).decrypt().runSync(outIncorrect); 
  }
  
  @Test(expected = IOException.class)
  public void testGCMModeTestCase4CorruptCipherText() throws CryptoException, IOException {
    byte[] key = CryptoUtils.parseHexString("feffe9928665731c6d6a8f9467308308");
    byte[] iv = CryptoUtils.parseHexString("cafebabefacedbaddecaf888");
    byte[] plain = CryptoUtils.parseHexString(
        "d9313225f88406e5a55909c5aff5269a" + 
        "86a7a9531534f7da2e4c303d8a318a72" + 
        "1c3c0c95956809532fcf0e2449a6b525" + 
        "b16aedf5aa0de657ba637b39");
    byte[] authData = CryptoUtils.parseHexString(
        "feedfacedeadbeeffeedfacedeadbeef" + 
        "abaddad2");
    byte[] cipherIncorrect = CryptoUtils.parseHexString(
        "42831ec2217774244b7221bf84d0d49c" + 
        "e3aa212f2c02a4e035c17e2329aca12e" + 
        "21d514b25466931c7d8f6a5aac84aa05" + 
        "1ba30b396a0aac973d58e091");
    byte[] tExpected = CryptoUtils.parseHexString("5bc94fbc3221a5db94fae95ae7121a47");
    byte[] outIncorrect = CryptoUtils.concatArrays(cipherIncorrect, tExpected);
    
    new GCMMode(new AES(key)).setAuthData(authData).setIV(iv).decrypt().runSync(outIncorrect); 
  }
  
  @Test
  public void testGCMModeTestCase5() throws CryptoException, IOException {
    byte[] key = CryptoUtils.parseHexString("feffe9928665731c6d6a8f9467308308");
    byte[] iv = CryptoUtils.parseHexString("cafebabefacedbad");
    byte[] plain = CryptoUtils.parseHexString(
        "d9313225f88406e5a55909c5aff5269a" + 
        "86a7a9531534f7da2e4c303d8a318a72" + 
        "1c3c0c95956809532fcf0e2449a6b525" + 
        "b16aedf5aa0de657ba637b39");
    byte[] authData = CryptoUtils.parseHexString(
        "feedfacedeadbeeffeedfacedeadbeef" + 
        "abaddad2");
    byte[] cipherExpected = CryptoUtils.parseHexString(
        "61353b4c2806934a777ff51fa22a4755" + 
        "699b2a714fcdc6f83766e5f97b6c7423" + 
        "73806900e49f24b22b097544d4896b42" + 
        "4989b5e1ebac0f07c23f4598");
    byte[] tExpected = CryptoUtils.parseHexString("3612d2e79e3b0785561be14aaca2fccb");
    
    fullTestCase(key, iv, plain, authData, cipherExpected, tExpected);
  }
  
  @Test
  public void testGCMModeTestCase18() throws CryptoException, IOException {
    byte[] key = CryptoUtils.parseHexString(
        "feffe9928665731c6d6a8f9467308308" + 
        "feffe9928665731c6d6a8f9467308308");
    byte[] iv = CryptoUtils.parseHexString("9313225df88406e555909c5aff5269aa" + 
        "6a7a9538534f7da1e4c303d2a318a728" + 
        "c3c0c95156809539fcf0e2429a6b5254" + 
        "16aedbf5a0de6a57a637b39b");
    byte[] plain = CryptoUtils.parseHexString(
        "d9313225f88406e5a55909c5aff5269a" + 
        "86a7a9531534f7da2e4c303d8a318a72" + 
        "1c3c0c95956809532fcf0e2449a6b525" + 
        "b16aedf5aa0de657ba637b39");
    byte[] authData = CryptoUtils.parseHexString(
        "feedfacedeadbeeffeedfacedeadbeef" + 
        "abaddad2");
    byte[] cipherExpected = CryptoUtils.parseHexString(
        "5a8def2f0c9e53f1f75d7853659e2a20" + 
        "eeb2b22aafde6419a058ab4f6f746bf4" + 
        "0fc0c3b780f244452da3ebf1c5d82cde" + 
        "a2418997200ef82e44ae7e3f");
    byte[] tExpected = CryptoUtils.parseHexString("a44a8266ee1c8eb0c8b5d4cf5ae9f19a");
    
    fullTestCase(key, iv, plain, authData, cipherExpected, tExpected);
  }
  
  private void fullTestCase(byte[] key, byte[] iv, byte[] plain, byte[] authData, byte[] cipherExpected,
      byte[] tExpected) throws IOException, CryptoException {
    byte[] outExpected = CryptoUtils.concatArrays(cipherExpected, tExpected);
    byte[] out = new GCMMode(new AES(key)).setAuthData(authData).setIV(iv).encrypt().runSync(plain); 
    assertArrayEquals(outExpected, out);
    
    byte[] in = new GCMMode(new AES(key)).setAuthData(authData).setIV(iv).decrypt().runSync(outExpected); 
    assertArrayEquals(plain, in);
  }

}
