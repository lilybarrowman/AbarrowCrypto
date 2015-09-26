package me.abarrow.hash.md;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import me.abarrow.core.CryptoUtils;

import org.junit.Test;

public class MD5Test {
  
  private String MD5Case(String testString) throws IOException {
    return CryptoUtils.byteArrayToHexString(new MD5().hash().start(testString.getBytes()));
  }
  
  @Test
  public void MD5ProperlyHashesStrings() throws IOException {
    assertEquals("d41d8cd98f00b204e9800998ecf8427e", MD5Case(""));
    assertEquals("0cc175b9c0f1b6a831c399e269772661", MD5Case("a"));
    assertEquals("900150983cd24fb0d6963f7d28e17f72", MD5Case("abc"));
    assertEquals("f96b697d7cb7938d525a2f31aaf161d0", MD5Case("message digest"));
    assertEquals("c3fcd3d76192e4007dfb496cca67e13b", MD5Case("abcdefghijklmnopqrstuvwxyz"));
    assertEquals("d174ab98d277d9f5a5611c2c9f419d9f", MD5Case("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
    assertEquals("57edf4a22be3c955ac49da2e2107b67a", MD5Case("12345678901234567890123456789012345678901234567890123456789012345678901234567890"));
  }
}
