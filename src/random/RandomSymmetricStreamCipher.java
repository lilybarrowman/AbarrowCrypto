package random;

import java.util.Arrays;
import java.util.Random;

import core.CryptoUtils;
import core.SymmetricStreamCipher;

public class RandomSymmetricStreamCipher extends SymmetricStreamCipher {
  
  private final Random random;
  
  public RandomSymmetricStreamCipher(Random r) {
    this(r, 0);
  }
  
  public RandomSymmetricStreamCipher(Random r, int bytesToDrop) {
    random = r;
    
    byte[] discard = new byte[bytesToDrop];
    random.nextBytes(discard);
    
    Arrays.fill(discard, CryptoUtils.ZERO_BYTE);
  }

  @Override
  public byte[] codec(byte[] input) {
    byte[] output = new byte[input.length];

    random.nextBytes(output);

    for (int n = 0; n < input.length; n++) {
      output[n] = (byte) ((output[n] ^ input[n]) & 0xff);
    }

    return output;
  }

}
