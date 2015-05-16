package padding;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import cipher.CBCModeCipher;
import cipher.aes.AES;

public class PKCS7Test {
  @Test
  public void testPadding() {
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54, -89, 1 }, PKCS7.pad(new byte[] { 67, 46, 12, 13, 54, -89 }, 7));
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54, 3, 3, 3 }, PKCS7.pad(new byte[] { 67, 46, 12, 13, 54 }, 8));
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54, 5, 5, 5, 5, 5 }, PKCS7.pad(new byte[] { 67, 46, 12, 13, 54 }, 5));
  }

  @Test
  public void testUnpadding() {
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54, -89 }, PKCS7.unpad(new byte[] { 67, 46, 12, 13, 54, -89, 1 }));
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54 }, PKCS7.unpad(new byte[] { 67, 46, 12, 13, 54, 3, 3, 3 }));
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54 }, PKCS7.unpad(new byte[] { 67, 46, 12, 13, 54, 5, 5, 5, 5, 5 }));
  }
  
  @Test
  public void cbcTest() {
    assertArrayEquals(
        parseHexBinary("6232d0a50c4e00a30cfb161bcc3a4dd84079a729f94dde6429887d8ba50752c906cb7d66533a3344438d921755681a12"),
        new CBCModeCipher(new AES(parseHexBinary("123afc45778932543cdabb432645123afc457789b2543cdabb432645b4326408")),
            parseHexBinary("8051f5bb13d68fcb5f8e25dd890228ac"))
            .encrypt(PKCS7.pad(parseHexBinary("48656c6c6f20686f772061726520796f752c2061726520796f752061207261626269743f"), 16)));
    assertArrayEquals(
        parseHexBinary("48656c6c6f20686f772061726520796f752c2061726520796f752061207261626269743f"),
        PKCS7.unpad(new CBCModeCipher(new AES(parseHexBinary("123afc45778932543cdabb432645123afc457789b2543cdabb432645b4326408")),
            parseHexBinary("8051f5bb13d68fcb5f8e25dd890228ac"))
            .decrypt(parseHexBinary("6232d0a50c4e00a30cfb161bcc3a4dd84079a729f94dde6429887d8ba50752c906cb7d66533a3344438d921755681a12"))));
  }

}
