package core;

import org.apache.commons.lang.ArrayUtils;

import des.DES;
import des.TripleDES;
import blowfish.Bcrypt;
import blowfish.BlowfishCipher;
import md.MD5;
import hmac.HMAC;
import random.HasherRandom;
import random.RandomVisualizer;
import random.RandomSymmetricStreamCipher;
import rc4.RC4Random;
import sha.SHA1;
import sha.SHA512;
import stenography.StenographyDemo;

public class Main {

  public static void main(String[] args) {
    /*System.out.println((new SHA512()).addString("abc").computeHashString());
    System.out.println((new SHA1()).addString("abc").computeHashString());
    System.out.println((new MD5()).addString("abc").computeHashString());

    System.out.println(new HMAC(new SHA1()).computeHashString("key", "The quick brown fox jumps over the lazy dog"));
    System.out.println(new HMAC(new MD5()).computeHashString("key", "The quick brown fox jumps over the lazy dog"));

    byte[] key = "Key".getBytes();
    byte[] original = "Plaintext".getBytes();
    byte[] encoded = new RandomSymmetricStreamCipher(new RC4Random(key)).codec(original);
    byte[] decoded = new RandomSymmetricStreamCipher(new RC4Random(key)).codec(encoded);

    System.out.println(CryptoUtils.byteArrayToHexString(original));
    System.out.println(CryptoUtils.byteArrayToHexString(encoded));
    System.out.println(CryptoUtils.byteArrayToHexString(decoded));

    System.out.println(CryptoUtils.byteArrayToHexString(new BlowfishCipher("key12345".getBytes()).encrypt("abcdefgh"
        .getBytes())));

    System.out.println(Base64Codec.getStandardBase64Codec().encode("abc".getBytes()));

    System.out.println(new Bcrypt("Twentytwocharactersalt".getBytes(), 10).addBytes("SuperSecurePassword".getBytes())
        .computeHashString());

    DES des = new DES("12345678".getBytes(), PairityBitType.NONE);
    byte[] out = des.encrypt("abc".getBytes());
    System.out.println(CryptoUtils.byteArrayToHexString(out));

    System.out.println(CryptoUtils.byteArrayToHexString(
        new TripleDES("AllYourPasswordsAreWayTooShort".getBytes(), PairityBitType.NONE).encrypt("This is a super secret and secure message!".getBytes())));*/
    
    /*boolean useFasterRandom = true;
    RandomVisualizer.start(useFasterRandom ? new HasherRandom() : new HasherRandom(new Bcrypt("123456890123456".getBytes(), 4),
        new byte[0]));*/
    
    StenographyDemo.start();    
  }

}
