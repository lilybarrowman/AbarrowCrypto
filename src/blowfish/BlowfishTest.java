package blowfish;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import core.CryptoUtils;

public class BlowfishTest {
  @Test
  public void testBlowfish() {
    assertEquals(CryptoUtils.byteArrayToHexString(new BlowfishCipher("key12345".getBytes()).encrypt("abcdefgh"
        .getBytes())), "80a89f2a52eba810");

    assertEquals(CryptoUtils.byteArrayToHexString(new BlowfishCipher("key1".getBytes()).encrypt("abcd".getBytes())),
        "7e5cf102a1aefd73");

    assertEquals(CryptoUtils.byteArrayToHexString(new BlowfishCipher("mynameisadamfearme22".getBytes())
    .encrypt("The secret code word is cake.".getBytes())),
        "c09b52fb70b6baef9725084018dc742f96bc1f0242415178de4759db85b5047f");
        
    
    assertEquals(CryptoUtils.byteArrayToHexString(new BlowfishCipher("mynameisadamfearme".getBytes())
    .encrypt("The secret code word is cake.".getBytes())),
        "011fbdabd42d1cebd1c50da290fbdebc5d5bd3b2b8c02a955870eb8a3be435bd");
  }
}
