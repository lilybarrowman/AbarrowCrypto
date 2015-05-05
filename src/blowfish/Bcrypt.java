package blowfish;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.mutable.MutableInt;

import random.RandomKeyMaker;
import core.Base64Codec;
import core.CryptoUtils;
import core.Hasher;

public class Bcrypt extends Hasher {
  
  private byte[] salt;
  private int rounds;
  
  private byte[] bytesToHash;
  
  private boolean isV2Y;
  
  public Bcrypt() {
    this(RandomKeyMaker.makeKey(16), 10);
  }

  
  public Bcrypt(byte[] hashSalt, int hashRounds) {
    this(hashSalt, hashRounds, true);
  }
  
  public Bcrypt(byte[] hashSalt, int hashRounds, boolean isUsingV2Y) {

    salt = hashSalt;
    rounds = hashRounds;
    bytesToHash = new byte[]{};
    isV2Y = isUsingV2Y;
  }

  @Override
  public Hasher addBytes(byte[] bytes) {
    byte[] bigger = new byte[bytesToHash.length + bytes.length];
    
    System.arraycopy(bytesToHash, 0, bigger, 0, bytesToHash.length);
    System.arraycopy(bytes, 0, bigger, bytesToHash.length, bytesToHash.length + bytes.length);

    Arrays.fill(bytesToHash, CryptoUtils.ZERO_BYTE);
    
    bytesToHash = bigger;
    
    return this;
  }

  @Override
  public byte[] computeHash(byte[] out , int start) {
    
    BlowfishCipher cipher = new BlowfishCipher(ArrayUtils.add(bytesToHash, (byte)0), salt, rounds);
    
    byte[] cipherText = "OrpheanBeholderScryDoubt".getBytes();
    
    int[] cipherInts = CryptoUtils.intArrayFromBytes(cipherText, 0, cipherText.length);
    
    MutableInt a = new MutableInt(cipherInts[0]);
    MutableInt b = new MutableInt(cipherInts[1]);
    MutableInt c = new MutableInt(cipherInts[2]);
    MutableInt d = new MutableInt(cipherInts[3]);
    MutableInt e = new MutableInt(cipherInts[4]);
    MutableInt f = new MutableInt(cipherInts[5]); 
    
    for (int n = 0; n < 64; n++) {
      cipher.encryptBlock(a, b);
      cipher.encryptBlock(c, d);
      cipher.encryptBlock(e, f);
    }
    
    CryptoUtils.intToBytes(a.intValue(), cipherText, 0);
    CryptoUtils.intToBytes(b.intValue(), cipherText, 4);
    CryptoUtils.intToBytes(c.intValue(), cipherText, 8);
    CryptoUtils.intToBytes(d.intValue(), cipherText, 12);
    CryptoUtils.intToBytes(e.intValue(), cipherText, 16);
    CryptoUtils.intToBytes(f.intValue(), cipherText, 20);
    
    System.arraycopy(cipherText, 0, out, start, cipherText.length);
    
    return out;
  }
  
  @Override
  public String computeHashString() {
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
    str.append(Base64Codec.getOpenBSDBase64Codec().encode(computeHash(), 23).substring(0, 31));
    
    return str.toString();
  }

  @Override
  public Hasher reset() {
    if (bytesToHash != null) {
      CryptoUtils.fillWithZeroes(bytesToHash);
    }
    bytesToHash = new byte[]{};
    return this;
  }

  @Override
  public int getBlockBytes() {
    return 8;
  }

  @Override
  public int getHashByteLength() {
    return 24;
  }

  public static boolean verifyPassword(String hash, String password) {
    
    if(hash.length() != 60) {
      return false;
    }
    
    int firstDollar = hash.indexOf('$');
    
    if (firstDollar == -1) {
      return false;
    }
    
    int secondDollar = hash.indexOf('$', firstDollar + 1);
    
    if (secondDollar == -1) {
      return false;
    }
    
    
    String version = hash.substring(firstDollar + 1, secondDollar);
    
    boolean isUsingV2Y = version.endsWith("2y");
    
    if(!version.equals("2a") && !isUsingV2Y) {
      return false;
    }
    
    int thirdDollar = hash.indexOf('$', secondDollar + 1);
    
    if (thirdDollar == -1) {
      return false;
    }
    
    String costString = hash.substring(secondDollar + 1, thirdDollar);
    int cost;
    try {
      cost = Integer.parseInt(costString);
    } catch (NumberFormatException e) {
      return false;
    }
    
    byte[] salt = Base64Codec.getOpenBSDBase64Codec().decode(hash.substring(thirdDollar + 1, thirdDollar + 23));
     
    return new Bcrypt(salt, cost, isUsingV2Y).addBytes(password.getBytes()).computeHashString().equals(hash);
    
  }


  @Override
  protected void hashBlock(byte[] data, int index) {    
  }
}
