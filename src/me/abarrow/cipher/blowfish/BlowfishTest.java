package me.abarrow.cipher.blowfish;

import static org.junit.Assert.assertArrayEquals;

import javax.xml.bind.DatatypeConverter;

import me.abarrow.cipher.BlockCipher;
import me.abarrow.core.CryptoException;
import me.abarrow.padding.ZeroPadding;

import org.junit.Test;

public class BlowfishTest {
  @Test
  public void testBlowfish() throws CryptoException {
    assertArrayEquals(DatatypeConverter.parseHexBinary("80a89f2a52eba810"), new BlowfishCipher("key12345".getBytes())
        .encryptBlock("abcdefgh".getBytes()));

    assertArrayEquals(DatatypeConverter.parseHexBinary("7e5cf102a1aefd73"),
        new BlowfishCipher("key1".getBytes()).encryptBlock(new ZeroPadding(8).pad("abcd".getBytes())));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("c09b52fb70b6baef"), new BlowfishCipher("mynameisadamfearme22"
        .getBytes()).encryptBlock("The secr".getBytes()));

    byte[] key = "mynameisadamfearme".getBytes();
    String original = "The secr";
    byte[] originalBytes = original.getBytes();

    BlockCipher cipher = new BlowfishCipher(key);

    byte[] encryptBlocked = cipher.encryptBlock(originalBytes);

    assertArrayEquals(encryptBlocked, DatatypeConverter
        .parseHexBinary("011fbdabd42d1ceb"));

    byte[] decryptBlocked = cipher.decryptBlock(encryptBlocked);
    assertArrayEquals(decryptBlocked, originalBytes);
  }
}
