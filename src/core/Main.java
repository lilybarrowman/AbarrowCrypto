package core;

import des.DES;
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

    boolean useSlowRandom = true;
    
    RandomVisualizer.start(useSlowRandom ? new HasherRandom() : new HasherRandom(new Bcrypt("123456890123456".getBytes(), 4),
        new byte[0]));*/
    
    DES des = new DES(PairityBitCodec.encode("1234567".getBytes(), true), true);
    

  }

}
