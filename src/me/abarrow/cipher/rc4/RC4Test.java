package me.abarrow.cipher.rc4;

import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;



public class RC4Test {
  
  @Test
  public void testRC4() throws IOException, CryptoException {
    testExample("Key", "Plaintext", "bbf316e8d940af0ad3", 0);
    testExample("Wiki", "pedia", "1021bf0420", 0);
    testExample("Secret", "Attack at dawn", "45a01f645fc35b383552544b9bf5", 0);
    testExample("SuperSpecial123+", "Never", "fdcbfff62f", 0);
    testExample("SuperSpecial123+", "Never", "059856a3e6", 8);
    testExample("SuperSpecial123+", "Never", "5c0c358c74", 384);

  }
  
  public void testExample(String keyString, String input, String expectedEncoding, int drop) throws IOException, CryptoException {
    byte[] key = keyString.getBytes();
    byte[] original =  input.getBytes();
    byte[] encoded = new RC4(null, key, drop).encrypt().startSync(original);
    byte[] decoded = new RC4(null, key, drop).decrypt().startSync(encoded);
    
    assertArrayEquals(original, decoded);
    assertEquals(expectedEncoding, CryptoUtils.byteArrayToHexString(encoded));
  }

}
