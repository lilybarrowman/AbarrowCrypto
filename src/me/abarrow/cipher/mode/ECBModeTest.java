package me.abarrow.cipher.mode;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import me.abarrow.cipher.Cipher;
import me.abarrow.cipher.aes.AES;
import me.abarrow.core.CryptoException;
import me.abarrow.padding.PKCS7;
import me.abarrow.padding.Padding;
import me.abarrow.padding.ZeroPadding;

import org.junit.Test;

public class ECBModeTest {

  @Test
  public void test() throws IOException, InterruptedException, CryptoException {
    testCase(parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c"), parseHexBinary("3243f6a8885a308d313198a2e0370734"),
        parseHexBinary("3925841d02dc09fbdc118597196a0b32"), new ZeroPadding());
    
    testCase(parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c"), parseHexBinary("3243f6a8885a308d313198a2e0370734"),
        parseHexBinary("3925841d02dc09fbdc118597196a0b32a254be88e037ddd9d79fb6411c3f9df8"), new PKCS7());
  }

  private void testCase(byte[] key, byte[] plainText, byte[] expectedCipherText, Padding padding)
      throws InterruptedException, IOException, CryptoException {
    Cipher ecbMode = new ECBMode(new AES(key), padding);
    
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    ecbMode.encrypt().startSync(new ByteArrayInputStream(plainText), o);
    assertArrayEquals(expectedCipherText,  o.toByteArray());
    assertArrayEquals(expectedCipherText, ecbMode.encrypt().startSync(plainText));

    o = new ByteArrayOutputStream();
    ecbMode.decrypt().startSync(new ByteArrayInputStream(expectedCipherText), o);
    assertArrayEquals(plainText, o.toByteArray());
    assertArrayEquals(plainText, ecbMode.decrypt().startSync(expectedCipherText));
  }

}
