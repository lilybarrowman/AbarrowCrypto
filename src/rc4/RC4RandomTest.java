package rc4;

import org.junit.Test;

import random.RandomStreamCipher;
import core.CryptoUtils;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;



public class RC4RandomTest {
  
  @Test
  public void testRC4Random() {
    testExample("Key", "Plaintext", "bbf316e8d940af0ad3", 0);
    testExample("Wiki", "pedia", "1021bf0420", 0);
    testExample("Secret", "Attack at dawn", "45a01f645fc35b383552544b9bf5", 0);
    testExample("SuperSpecial123+", "Never", "fdcbfff62f", 0);
    testExample("SuperSpecial123+", "Never", "059856a3e6", 8);
    testExample("SuperSpecial123+", "Never", "5c0c358c74", 384);

  }
  
  public void testExample(String keyString, String input, String expectedEncoding, int drop) {
    byte[] key = keyString.getBytes();
    byte[] original =  input.getBytes();
    byte[] encoded = (new RandomStreamCipher(new RC4Random(key), drop)).codec(original);
    byte[] decoded = (new RandomStreamCipher(new RC4Random(key), drop)).codec(encoded);
    
    assertArrayEquals(original, decoded);
    assertEquals(expectedEncoding, CryptoUtils.byteArrayToHexString(encoded));
  }

}
