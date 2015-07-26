package me.abarrow.cipher.blowfish;

import java.util.Arrays;

import me.abarrow.base64.Base64Codec;
import me.abarrow.core.CryptoUtils;

public class Bcrypt {
  
  public static byte[] computeHash(int rounds, byte[] salt, byte[] bytesToHash) {  
    byte[] bytes = Arrays.copyOf(bytesToHash, bytesToHash.length + 1);
    
    BlowfishCipher cipher = new BlowfishCipher(bytes, salt, rounds);
    
    byte[] cipherText = "OrpheanBeholderScryDoubt".getBytes();
    
    int[] cipherInts = CryptoUtils.intArrayFromBytes(cipherText, 0, cipherText.length);
    
    int[] ab = new int[]{cipherInts[0], cipherInts[1]};
    int[] cd = new int[]{cipherInts[2], cipherInts[3]};
    int[] ef = new int[]{cipherInts[4], cipherInts[5]};
    CryptoUtils.fillWithZeroes(cipherInts);
    
    for (int n = 0; n < 64; n++) {
      cipher.encryptBlock(ab);
      cipher.encryptBlock(cd);
      cipher.encryptBlock(ef);
    }
    
    CryptoUtils.intToBytes(ab[0], cipherText, 0);
    CryptoUtils.intToBytes(ab[1], cipherText, 4);
    CryptoUtils.intToBytes(cd[0], cipherText, 8);
    CryptoUtils.intToBytes(cd[1], cipherText, 12);
    CryptoUtils.intToBytes(ef[0], cipherText, 16);
    CryptoUtils.intToBytes(ef[1], cipherText, 20);
    
    byte[] out = new byte[24];
    
    System.arraycopy(cipherText, 0, out, 0, cipherText.length);
    CryptoUtils.fillWithZeroes(bytes);
    CryptoUtils.fillWithZeroes(ab);
    CryptoUtils.fillWithZeroes(cd);
    CryptoUtils.fillWithZeroes(ef);
    
    return out;
  }
  
  public static String computeHashString(boolean isV2Y, int rounds, byte[] salt, byte[] bytesToHash) {
    StringBuilder str = new StringBuilder();
    
    if (isV2Y) {
      str.append("$2y$");
    } else {
      str.append("$2a$");
    }
    
    String roundString = Integer.toString(rounds);
    if (roundString.length() == 1) {
      str.append('0');
    }
    str.append(roundString);
    
    str.append('$');
    
    //22 character salt
    str.append(Base64Codec.getOpenBSDBase64Codec().encode(salt).substring(0, 22));
    
    //31 character hash
    str.append(Base64Codec.getOpenBSDBase64Codec().encode(computeHash(rounds, salt, bytesToHash), 23).substring(0, 31));
    
    return str.toString();
  }

  public static boolean verifyPassword(String bcryptString, byte[] password) {
    
    if(bcryptString.length() != 60) {
      return false;
    }
    
    int firstDollar = bcryptString.indexOf('$');
    if (firstDollar == -1) {
      return false;
    }
    
    int secondDollar = bcryptString.indexOf('$', firstDollar + 1);
    if (secondDollar == -1) {
      return false;
    }
    
    
    String version = bcryptString.substring(firstDollar + 1, secondDollar);
    
    boolean isUsingV2Y = version.endsWith("2y");
    if(!version.equals("2a") && !isUsingV2Y) {
      return false;
    }
    
    int thirdDollar = bcryptString.indexOf('$', secondDollar + 1);
    if (thirdDollar == -1) {
      return false;
    }
    
    String costString = bcryptString.substring(secondDollar + 1, thirdDollar);
    int cost;
    try {
      cost = Integer.parseInt(costString);
    } catch (NumberFormatException e) {
      return false;
    }
    
    Base64Codec bsdBase64 = Base64Codec.getOpenBSDBase64Codec();  
    byte[] salt = bsdBase64.decode(bcryptString.substring(thirdDollar + 1, thirdDollar + 23));
    byte[] hash = bsdBase64.decode(bcryptString.substring(thirdDollar + 23)); 
    byte[] computedHash = computeHash(cost, salt, password);    
    boolean isEqual = CryptoUtils.subArrayEquals(computedHash, 0, hash, 0, 23);
    CryptoUtils.fillWithZeroes(computedHash);
    return isEqual;
  }
}
