package aes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import core.CryptoUtils;

public class AESTest {

  @Test
  public void testMultiplyBytes() {
    assertEquals(0xc1, AES.multiplyBytes(0x57, 0x83));
  }

  @Test
  public void testMultiplyInts() {
    assertEquals(0x57, AES.multiplyInts(0x0b0d090e, AES.multiplyInts(0x03010102, 0x57)));
  }

  @Test
  public void testKeyExpansion() {

    assertArrayEquals(CryptoUtils.swapEndianness(new int[] { 0x2b7e1516, 0x28aed2a6, 0xabf71588, 0x09cf4f3c,
        0xa0fafe17, 0x88542cb1, 0x23a33939, 0x2a6c7605, 0xf2c295f2, 0x7a96b943, 0x5935807a, 0x7359f67f, 0x3d80477d,
        0x4716fe3e, 0x1e237e44, 0x6d7a883b, 0xef44a541, 0xa8525b7f, 0xb671253b, 0xdb0bad00, 0xd4d1c6f8, 0x7c839d87,
        0xcaf2b8bc, 0x11f915bc, 0x6d88a37a, 0x110b3efd, 0xdbf98641, 0xca0093fd, 0x4e54f70e, 0x5f5fc9f3, 0x84a64fb2,
        0x4ea6dc4f, 0xead27321, 0xb58dbad2, 0x312bf560, 0x7f8d292f, 0xac7766f3, 0x19fadc21, 0x28d12941, 0x575c006e,
        0xd014f9a8, 0xc9ee2589, 0xe13f0cc8, 0xb6630ca6 }), AES.expandRoundKeys(DatatypeConverter
            .parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")));

    assertArrayEquals(CryptoUtils.swapEndianness(new int[] { 0x603deb10, 0x15ca71be, 0x2b73aef0, 0x857d7781,
        0x1f352c07, 0x3b6108d7, 0x2d9810a3, 0x0914dff4, 0x9ba35411, 0x8e6925af, 0xa51a8b5f, 0x2067fcde, 0xa8b09c1a,
        0x93d194cd, 0xbe49846e, 0xb75d5b9a, 0xd59aecb8, 0x5bf3c917, 0xfee94248, 0xde8ebe96, 0xb5a9328a, 0x2678a647,
        0x98312229, 0x2f6c79b3, 0x812c81ad, 0xdadf48ba, 0x24360af2, 0xfab8b464, 0x98c5bfc9, 0xbebd198e, 0x268c3ba7,
        0x09e04214, 0x68007bac, 0xb2df3316, 0x96e939e4, 0x6c518d80, 0xc814e204, 0x76a9fb8a, 0x5025c02d, 0x59c58239,
        0xde136967, 0x6ccc5a71, 0xfa256395, 0x9674ee15, 0x5886ca5d, 0x2e2f31d7, 0x7e0af1fa, 0x27cf73c3, 0x749c47ab,
        0x18501dda, 0xe2757e4f, 0x7401905a, 0xcafaaae3, 0xe4d59b34, 0x9adf6ace, 0xbd10190d, 0xfe4890d1, 0xe6188d0b,
        0x046df344, 0x706c631e }), AES.expandRoundKeys(DatatypeConverter
            .parseHexBinary("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4")));
  }

  @Test
  public void testEncoding() {

    assertArrayEquals(DatatypeConverter.parseHexBinary("3925841d02dc09fbdc118597196a0b32"), new AES(DatatypeConverter
        .parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")).encrypt(DatatypeConverter
        .parseHexBinary("3243f6a8885a308d313198a2e0370734")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("69c4e0d86a7b0430d8cdb78070b4c55a"), new AES(DatatypeConverter
        .parseHexBinary("000102030405060708090a0b0c0d0e0f")).encrypt(DatatypeConverter
        .parseHexBinary("00112233445566778899aabbccddeeff")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("00112233445566778899aabbccddeeff"), new AES(DatatypeConverter
        .parseHexBinary("000102030405060708090a0b0c0d0e0f")).decrypt(DatatypeConverter
        .parseHexBinary("69c4e0d86a7b0430d8cdb78070b4c55a")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("dda97ca4864cdfe06eaf70a0ec0d7191"), new AES(DatatypeConverter
        .parseHexBinary("000102030405060708090a0b0c0d0e0f1011121314151617")).encrypt(DatatypeConverter
        .parseHexBinary("00112233445566778899aabbccddeeff")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("00112233445566778899aabbccddeeff"), new AES(DatatypeConverter
        .parseHexBinary("000102030405060708090a0b0c0d0e0f1011121314151617")).decrypt(DatatypeConverter
        .parseHexBinary("dda97ca4864cdfe06eaf70a0ec0d7191")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("8ea2b7ca516745bfeafc49904b496089"), new AES(DatatypeConverter
        .parseHexBinary("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f")).encrypt(DatatypeConverter
        .parseHexBinary("00112233445566778899aabbccddeeff")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("00112233445566778899aabbccddeeff"), new AES(DatatypeConverter
        .parseHexBinary("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f")).decrypt(DatatypeConverter
        .parseHexBinary("8ea2b7ca516745bfeafc49904b496089")));
  }

}
