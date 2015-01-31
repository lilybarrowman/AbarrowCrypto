package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.ArrayUtils;

import des.DES;
import des.TripleDES;
import aes.AES;
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
import wavtools.ArraySampleData;
import wavtools.WavSampleData;

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
        new TripleDES("AllYourPasswordsAreWayTooShort".getBytes(), PairityBitType.NONE).encrypt("This is a super secret and secure message!".getBytes())));
    
    System.out.println(CryptoUtils.byteArrayToHexString(new AES(DatatypeConverter
        .parseHexBinary("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f")).encrypt(DatatypeConverter
        .parseHexBinary("00112233445566778899aabbccddeeff"))));*/
    
    /*boolean useFasterRandom = true;
    RandomVisualizer.start(useFasterRandom ? new HasherRandom() : new HasherRandom(new Bcrypt("123456890123456".getBytes(), 4),
        new byte[0]));*/
    
    StenographyDemo.start();
    
    
    
    /*int STD_FREQ = 44100;
    
    int samplesPerChannel = 441000;
    
    int channels = 2;
    short[] samples = new short[samplesPerChannel * channels];
    for (int i = 0; i < samplesPerChannel; i++) {
      samples[2 * i] = (short) (Short.MAX_VALUE * Math.sin(i / 25f) * (1 + Math.sin(i * Math.PI * 2d / 44100)) / 2);
      samples[2 * i + 1] = (short) (Short.MAX_VALUE * Math.sin(i / 25f) * (1 + Math.cos(i * Math.PI * 2d / 44100)) / 2);
    }*/
    
    /*byte[] hidden = new byte[100];
    new HasherRandom().nextBytes(hidden);
    
    for (int i = 0; i < hidden.length; i++) {
      int stenByte = hidden[i] & 0xff;
      samples[4 * i] = (short)((samples[i] & 0xfffc) + ((stenByte & 0xc0) >>> 6));
      samples[4 * i + 1] = (short)((samples[i + 1] & 0xfffc) + ((stenByte & 0x30) >>> 4));
      samples[4 * i + 2] = (short)((samples[i + 2] & 0xfffc) + ((stenByte & 0xc) >>> 2));
      samples[4 * i + 3] = (short)((samples[i + 3] & 0xfffc) + (stenByte & 0x3));
    }*/
    
    /*try {
      WavSampleData.writeWav(new ArraySampleData(samples, channels, STD_FREQ, samplesPerChannel), new FileOutputStream("loop.wav"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    try {
      WavSampleData dat = new WavSampleData(new FileInputStream("loop.wav"));
      
      int chans = dat.getNumChannels();
      int numSamps = dat.getSamplesRemaining();
      
      short[] samplesDecoded = new short[numSamps * chans];
      
      int samplePos = 0;
      while (dat.getSamplesRemaining() > 0) {
        int grabbed = dat.getSamples(samplesDecoded, samplePos, dat.getSamplesRemaining());
        samplePos += grabbed;
      }
      
      
      for(int n = 0; n < numSamps; n++) {
        System.out.println((samples[n] & 0xffff) + " " + (samplesDecoded[n] & 0xffff));
        if (samples[n] != samplesDecoded[n]) {
          System.out.println("Failure!");
          return;
        }
      }
      System.out.println("Success!");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }*/
    
    
    
  }

}
