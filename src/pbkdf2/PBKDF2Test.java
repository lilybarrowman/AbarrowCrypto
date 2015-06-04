package pbkdf2;

import static org.junit.Assert.assertEquals;
import hash.sha.SHA1;
import mac.hmac.HMAC;

import org.junit.Test;

import core.CryptoUtils;

public class PBKDF2Test {
  @Test
  public void testHMAC() {
    assertEquals("e1cb00b9817c198f4fdb29865387bdf8", CryptoUtils.byteArrayToHexString(PBKDF2.generateKey(new HMAC(
        new SHA1()), "howaboutthemapples".getBytes(), "cows".getBytes(), 1000, 16)));
    
    assertEquals("3b9c78dabf0ad12ac6fa1676648670b896bd53e2b56df25c39bdbc2a0eb4d5ee", CryptoUtils.byteArrayToHexString(PBKDF2.generateKey(new HMAC(
        new SHA1()), "thepasswordisnotlucky".getBytes(), "somethingyourwaifuntr".getBytes(), 10000, 32)));
  }
}
