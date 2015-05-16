package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.ArrayUtils;

import cipher.aes.AES;
import cipher.blowfish.Bcrypt;
import cipher.blowfish.BlowfishCipher;
import cipher.des.DES;
import cipher.des.TripleDES;
import cipher.rc4.RC4Random;
import pbkdf2.PBKDF2;
import hash.md.MD5;
import hash.sha.SHA1;
import hash.sha.SHA2_512;
import hash.sha.SHA3;
import hmac.HMAC;
import random.HasherRandom;
import random.RandomVisualizer;
import random.RandomStreamCipher;
import stenography.StenographyDemo;
import wavtools.ArraySampleData;
import wavtools.WavSampleData;

public class Main {

  public static void main(String[] args) {
    //StenographyDemo.start();
    
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
    
    //ChatApp.start();
  }

}
