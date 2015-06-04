package pbkdf2;

import java.util.Arrays;

import mac.hmac.HMAC;
import core.CryptoUtils;

public class PBKDF2 {
  public static byte[] generateKey(HMAC hmac, byte[] password, byte[] salt, int interations, int desiredLength) {
    byte[] saltedNumber = new byte[4 + salt.length];
    System.arraycopy(salt, 0, saltedNumber, 0, salt.length);
    int hmacLength = hmac.getHMACByteLength();
    int numChains = (desiredLength + hmacLength - 1) / hmacLength;
    byte[] key = new byte[desiredLength];
    for (int i = 0; i < numChains; i++) {
      byte[] keyFragment = null;
      byte[] xoredFragments = null;
      for (int n = 0; n < interations; n++) {
        
        if (n == 0) {
          keyFragment = hmac.computeHash(password, CryptoUtils.intToBytes(i + 1, saltedNumber, salt.length, false));
          xoredFragments = Arrays.copyOf(keyFragment, keyFragment.length);
        } else {
          hmac.computeHash(password, keyFragment, keyFragment, 0);
          CryptoUtils.xorByteArrays(keyFragment, xoredFragments, xoredFragments);
        }
      }
      System.arraycopy(xoredFragments, 0, key, i * hmacLength, (i == (numChains - 1)) ? desiredLength - hmacLength * i
          : hmacLength);
    }
    return key;
  }
}
