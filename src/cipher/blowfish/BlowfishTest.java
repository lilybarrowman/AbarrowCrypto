package cipher.blowfish;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import cipher.Cipher;
import cipher.PaddedCipher;
import cipher.mode.ECBMode;
import padding.ZeroPadding;
import core.CryptoException;
import core.CryptoUtils;

public class BlowfishTest {
  @Test
  public void testBlowfish() throws CryptoException {
    assertEquals(CryptoUtils.byteArrayToHexString(new BlowfishCipher("key12345".getBytes()).encryptBlock("abcdefgh"
        .getBytes())), "80a89f2a52eba810");

    assertEquals(CryptoUtils.byteArrayToHexString(new BlowfishCipher("key1".getBytes()).encryptBlock(new ZeroPadding(8)
        .pad("abcd".getBytes()))), "7e5cf102a1aefd73");

    assertEquals("c09b52fb70b6baef9725084018dc742f96bc1f0242415178de4759db85b5047f", CryptoUtils
        .byteArrayToHexString(new PaddedCipher(new ECBMode(new BlowfishCipher("mynameisadamfearme22".getBytes())),
            new ZeroPadding()).encrypt("The secret code word is cake.".getBytes())));

    byte[] key = "mynameisadamfearme".getBytes();
    String original = "The secret code word is cake.";
    byte[] originalBytes = original.getBytes();

    Cipher cipher = new PaddedCipher(new ECBMode(new BlowfishCipher(key)),
        new ZeroPadding());

    byte[] encryptBlocked = cipher.encrypt(originalBytes);

    assertEquals(CryptoUtils.byteArrayToHexString(encryptBlocked),
        "011fbdabd42d1cebd1c50da290fbdebc5d5bd3b2b8c02a955870eb8a3be435bd");

    byte[] decryptBlocked = cipher.decrypt(encryptBlocked);

    String decryptBlockedString = new String(decryptBlocked);

    assertEquals(decryptBlockedString.replace("\0", ""), original);
  }
}
