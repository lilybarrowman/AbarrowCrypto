package cipher.des;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import padding.ZeroPadding;
import cipher.Cipher;
import cipher.PaddedCipher;
import cipher.mode.ECBMode;
import core.CryptoException;
import core.CryptoUtils;

public class TripleDESTest {
  
  
  @Test
  public void testTrippleDES() throws CryptoException {
    Cipher cipher = new PaddedCipher(new ECBMode(new TripleDES("AllYourPasswordsAreWayTooShort".getBytes())), new ZeroPadding());
    String original = "This is a super secret and secure message!";
    byte[] originalBytes = original.getBytes();
    byte[] encrypted = cipher.encrypt(originalBytes);
    byte[] decrypted = cipher.decrypt(encrypted);
    assertEquals(CryptoUtils.byteArrayToHexString(encrypted), "c12b611f56a32794956fb5604785819382787616258174a33dbf605749bc193e567b04de02403c3545ca7452fc534d8f");
    assertEquals(original, new String(decrypted).replaceAll("\0", ""));
  }
}
