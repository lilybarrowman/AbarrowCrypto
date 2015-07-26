package me.abarrow.cipher.des;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import me.abarrow.cipher.BlockCipher;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.padding.PKCS7;

import org.junit.Test;

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
    BlockCipher cipher = new DES("12345678".getBytes());

    String original = "abc";
    byte[] originalBytes = new PKCS7(cipher.getBlockBytes()).pad(original.getBytes());
    byte[] encrypted = cipher.encryptBlock(originalBytes);
    byte[] decrypted = cipher.decryptBlock(encrypted);
    assertEquals("c24b98fad5c0580e", CryptoUtils.byteArrayToHexString(encrypted));
    assertArrayEquals(originalBytes, decrypted);
    
    
    encrypted = new DES(new byte[]{(byte) 0x80, 0, 0, 0, 0, 0, 0, 0}).encryptBlock(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});
    decrypted = new DES(new byte[]{(byte) 0x80, 0, 0, 0, 0, 0, 0, 0}).decryptBlock(encrypted);
    assertEquals(CryptoUtils.byteArrayToHexString(encrypted), "95a8d72813daa94d");
    assertEquals(CryptoUtils.byteArrayToHexString(decrypted), "0000000000000000");
    
  }
}
