package me.abarrow.pbkdf2;

import java.util.Arrays;

import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.mac.hmac.HMAC;

public class PBKDF2 {
  public static byte[] generateKey(HMAC hmac, byte[] password, byte[] salt, int interations, int desiredLength) throws CryptoException {
    hmac.setMACKey(password);
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
          keyFragment = hmac.tag(CryptoUtils.intToBytes(i + 1, saltedNumber, salt.length, false), true);
          xoredFragments = Arrays.copyOf(keyFragment, keyFragment.length);
        } else {
          byte[] temp = hmac.tag(keyFragment, true);
          keyFragment = temp;
          CryptoUtils.xorByteArrays(keyFragment, xoredFragments, xoredFragments);
        }
      }
      System.arraycopy(xoredFragments, 0, key, i * hmacLength, (i == (numChains - 1)) ? desiredLength - hmacLength * i
          : hmacLength);
    }
    hmac.removeMACKey();
    return key;
  }
}
