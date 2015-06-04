package cipher.mode;

import static org.junit.Assert.assertArrayEquals;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import core.CryptoException;
import padding.Padding;
import padding.ZeroPadding;
import cipher.Cipher;
import cipher.PaddedCipher;
import cipher.aes.AES;

public class CBCModeTest {
  @Test
  public void testEncoding() throws CryptoException, InterruptedException, IOException {
    byte[] key = parseHexBinary("123afc45778932543cdabb432645123afc457789b2543cdabb432645b4326408");
    byte[] iv = parseHexBinary("8051f5bb13d68fcb5f8e25dd890228ac");
    byte[] plainText = parseHexBinary("48656c6c6f20686f772061726520796f752c2061726520796f752061207261626269743f");
    byte[] expectedCipherText = parseHexBinary("6232d0a50c4e00a30cfb161bcc3a4dd84079a729f94dde6429887d8ba50752c909f12f7266ba4eaab8991375e0b938ec");
    byte[] expectedPlainText = parseHexBinary("48656c6c6f20686f772061726520796f752c2061726520796f752061207261626269743f000000000000000000000000");
    testCase(key, iv,  plainText, expectedCipherText, expectedPlainText, new ZeroPadding());
  }
  
  private void testCase(byte[] key, byte[] iv, byte[] plainText,
      byte[] expectedCipherText, byte[] expectedPlainText, Padding padding)
      throws InterruptedException, IOException, CryptoException {
    Cipher cbcMode = new PaddedCipher(new CBCMode(new AES(key), iv), padding);
    
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    cbcMode.encrypt().start(new ByteArrayInputStream(plainText), o);
    assertArrayEquals(expectedCipherText, cbcMode.encrypt(plainText));
    assertArrayEquals(expectedCipherText,  o.toByteArray());

    o = new ByteArrayOutputStream();
    cbcMode.decrypt().start(new ByteArrayInputStream(expectedCipherText), o);
    assertArrayEquals(expectedPlainText, cbcMode.decrypt(expectedCipherText));
    assertArrayEquals(expectedPlainText, o.toByteArray());
    
  }
  
}
