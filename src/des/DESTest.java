package des;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;




import org.junit.Test;

import core.CryptoUtils;
import core.PairityBitType;

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
  public void testDES() {
    DES des = new DES("12345678".getBytes(), PairityBitType.NONE);
    String original = "abc";
    byte[] originalBytes = original.getBytes();
    byte[] encrypted = des.encrypt(originalBytes);
    byte[] decrypted = des.decrypt(encrypted);
    assertEquals(CryptoUtils.byteArrayToHexString(encrypted), "2c8369311a2e38fa");
    assertEquals(original, new String(decrypted).replace("\0", ""));
    
    
    byte[] orig = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    encrypted = new DES(new byte[]{(byte) 0x80, 0, 0, 0, 0, 0, 0, 0}, PairityBitType.NONE).encrypt(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});
    decrypted = new DES(new byte[]{(byte) 0x80, 0, 0, 0, 0, 0, 0, 0}, PairityBitType.NONE).decrypt(encrypted);
    assertEquals(CryptoUtils.byteArrayToHexString(encrypted), "95a8d72813daa94d");
    assertEquals(CryptoUtils.byteArrayToHexString(decrypted), "0000000000000000");
    
  }
}
