package cipher.des;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import padding.PKCS7;
import cipher.PaddedCipher;
import cipher.mode.ECBMode;
import core.CryptoException;
import core.CryptoUtils;

public class DESTest {
  @Test
  public void testDESPremutation() {
    
    String sample = "abcdefgh";
    byte[] input = sample.getBytes();
    
    byte[] premutated = new byte[8];
    CryptoUtils.permuteByteArrayByBit(input, -1, premutated, DES.IP);
    
    byte[] depremutated = new byte[8];
    CryptoUtils.permuteByteArrayByBit(premutated, -1, depremutated, DES.IP_PRIME);
    
    assertArrayEquals(input, depremutated);
    
  }
  
  @Test
  public void testDES() throws CryptoException {
    PaddedCipher cipher = new PaddedCipher(new ECBMode(new DES("12345678".getBytes())), new PKCS7());

    String original = "abc";
    byte[] originalBytes = original.getBytes();
    byte[] encrypted = cipher.encrypt(originalBytes);
    byte[] decrypted = cipher.decrypt(encrypted);
    assertEquals("c24b98fad5c0580e", CryptoUtils.byteArrayToHexString(encrypted));
    assertArrayEquals(originalBytes, decrypted);
    
    
    encrypted = new ECBMode(new DES(new byte[]{(byte) 0x80, 0, 0, 0, 0, 0, 0, 0})).encrypt(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});
    decrypted = new ECBMode(new DES(new byte[]{(byte) 0x80, 0, 0, 0, 0, 0, 0, 0})).decrypt(encrypted);
    assertEquals(CryptoUtils.byteArrayToHexString(encrypted), "95a8d72813daa94d");
    assertEquals(CryptoUtils.byteArrayToHexString(decrypted), "0000000000000000");
    
  }
}
