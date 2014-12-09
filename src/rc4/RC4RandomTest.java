package rc4;

import org.junit.Test;

import random.RandomSymmetricStreamCipher;
import core.CryptoUtils;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;



public class RC4RandomTest {
  
  @Test
  public void testRC4Random() {
    testExample("Key", "Plaintext", "bbf316e8d940af0ad3");
    testExample("Wiki", "pedia", "1021bf0420");
    testExample("Secret", "Attack at dawn", "45a01f645fc35b383552544b9bf5");
  }
  
  public void testExample(String keyString, String input, String expectedEncoding) {
    byte[] key = keyString.getBytes();
    byte[] original =  input.getBytes();
    byte[] encoded = (new RandomSymmetricStreamCipher(new RC4Random(key))).codec(original);
    byte[] decoded = (new RandomSymmetricStreamCipher(new RC4Random(key))).codec(encoded);
    
    assertArrayEquals(original, decoded);
    assertEquals(CryptoUtils.byteArrayToHexString(encoded), expectedEncoding);
  }

}
