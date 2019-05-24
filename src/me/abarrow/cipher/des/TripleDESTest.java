  package me.abarrow.cipher.des;

import static org.junit.Assert.assertArrayEquals;

import me.abarrow.cipher.BlockCipher;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;

import org.junit.Test;

public class TripleDESTest {
  
  
  @Test
  public void testTrippleDES() throws CryptoException {
    BlockCipher cipher = new TripleDES(CryptoUtils.parseHexString("0123456789abcdef01234567000000000000000000000000"));
    byte[] originalBytes = CryptoUtils.parseHexString("0123456700000000");
    byte[] encrypted = cipher.encryptBlock(originalBytes);
    byte[] decrypted = cipher.decryptBlock(encrypted);
    assertArrayEquals(encrypted, CryptoUtils.parseHexString("a7106c9badfb1c0c"));
    assertArrayEquals(originalBytes, decrypted);
  }
}
