package cipher.des;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import core.CryptoUtils;

public class TripleDESTest {
  
  
  @Test
  public void testTrippleDES() {
    TripleDES des = new TripleDES("AllYourPasswordsAreWayTooShort".getBytes(), false);
    String original = "This is a super secret and secure message!";
    byte[] originalBytes = original.getBytes();
    byte[] encrypted = des.encrypt(originalBytes);
    byte[] decrypted = des.decrypt(encrypted);
    assertEquals(CryptoUtils.byteArrayToHexString(encrypted), "c12b611f56a32794956fb5604785819382787616258174a33dbf605749bc193e567b04de02403c3545ca7452fc534d8f");
    assertEquals(original, new String(decrypted).replace("\0", ""));
  }
}
