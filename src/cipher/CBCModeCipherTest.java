package cipher;

import static org.junit.Assert.assertArrayEquals;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import org.junit.Test;
import cipher.aes.AES;

public class CBCModeCipherTest {
  @Test
  public void testEncoding() {
    assertArrayEquals(
        parseHexBinary("6232d0a50c4e00a30cfb161bcc3a4dd84079a729f94dde6429887d8ba50752c909f12f7266ba4eaab8991375e0b938ec"),
        new CBCModeCipher(new AES(parseHexBinary("123afc45778932543cdabb432645123afc457789b2543cdabb432645b4326408")),
            parseHexBinary("8051f5bb13d68fcb5f8e25dd890228ac"))
            .encrypt(parseHexBinary("48656c6c6f20686f772061726520796f752c2061726520796f752061207261626269743f")));
    assertArrayEquals(
        parseHexBinary("48656c6c6f20686f772061726520796f752c2061726520796f752061207261626269743f000000000000000000000000"),
        new CBCModeCipher(new AES(parseHexBinary("123afc45778932543cdabb432645123afc457789b2543cdabb432645b4326408")),
            parseHexBinary("8051f5bb13d68fcb5f8e25dd890228ac"))
            .decrypt(parseHexBinary("6232d0a50c4e00a30cfb161bcc3a4dd84079a729f94dde6429887d8ba50752c909f12f7266ba4eaab8991375e0b938ec")));
  }
}
