  package cipher.des;

import static org.junit.Assert.assertArrayEquals;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import cipher.BlockCipher;
import core.CryptoException;

public class TripleDESTest {
  
  
  @Test
  public void testTrippleDES() throws CryptoException {
    BlockCipher cipher = new TripleDES(DatatypeConverter
        .parseHexBinary("0123456789abcdef01234567000000000000000000000000"));
    byte[] originalBytes = DatatypeConverter.parseHexBinary("0123456700000000");
    byte[] encrypted = cipher.encryptBlock(originalBytes);
    byte[] decrypted = cipher.decryptBlock(encrypted);
    assertArrayEquals(encrypted, DatatypeConverter.parseHexBinary("a7106c9badfb1c0c"));
    assertArrayEquals(originalBytes, decrypted);
  }
}
