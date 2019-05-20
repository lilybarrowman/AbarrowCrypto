package me.abarrow.pbkdf2;

import static org.junit.Assert.assertEquals;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.hash.sha.SHA1;
import me.abarrow.hash.sha.SHA256;
import me.abarrow.mac.hmac.HMAC;

import org.junit.Test;

public class PBKDF2Test {
  @Test
  public void testHMAC() throws CryptoException {
    assertEquals("e1cb00b9817c198f4fdb29865387bdf8", CryptoUtils.byteArrayToHexString(PBKDF2.generateKey(new HMAC(
        new SHA1()), "howaboutthemapples".getBytes(), "cows".getBytes(), 1000, 16)));
    
    assertEquals("3b9c78dabf0ad12ac6fa1676648670b896bd53e2b56df25c39bdbc2a0eb4d5ee", CryptoUtils.byteArrayToHexString(PBKDF2.generateKey(new HMAC(
        new SHA1()), "thepasswordisnotlucky".getBytes(), "somethingyourwaifuntr".getBytes(), 10000, 32)));
    
    assertEquals("55ac046e56e3089fec1691c22544b605f94185216dde0465e68b9d57c20dacbc49ca9cccf179b645991664b39d77ef317c71b845b1e30bd509112041d3a19783", CryptoUtils.byteArrayToHexString(PBKDF2.generateKey(new HMAC(
        new SHA256()), "passwd".getBytes(), "salt".getBytes(), 1, 64)));
    
    assertEquals("9aefe7250d0c5b2bbd7ee32458c057f97aeb564a1c8833f2d93b198af187970770790dd556950e34b95d8075be864528531e3d159c907684805d591126e9f6ee", CryptoUtils.byteArrayToHexString(PBKDF2.generateKey(new HMAC(
        new SHA256()), "passwd".getBytes(), "salt".getBytes(), 100, 64)));
    
    assertEquals("15361a12e9cdf546262d468fe84b03a9bdc1e711b99d0429db9f8d9167e5236676af06f0e4ffe16ee6a2689fd20f8070d36f55f3e5f171bc39ce670f4fbb78e8", CryptoUtils.byteArrayToHexString(PBKDF2.generateKey(new HMAC(
        new SHA256()), "passwd".getBytes(), "salt".getBytes(), 100000, 64)));
  }
}
