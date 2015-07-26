package me.abarrow.stenography;

import java.io.File;
import java.io.FileOutputStream;

import me.abarrow.core.CryptoUtils;
import wavtools.ArraySampleData;
import wavtools.WavSampleData;

public class WAVStenographer  extends Stenographer<WavSampleData, Exception> {

  @Override
  public boolean canSourceHoldData(int numBytes, WavSampleData source) {
    return ((source.getSamplesRemaining() * source.getNumChannels()) / 4) >= (numBytes + 8);
  }

  @Override
  public void encode(StenData data, WavSampleData source, File dest) throws Exception {
    int channels = source.getNumChannels();
    int numSamples = source.getSamplesRemaining();
    
    short[] samples = new short[numSamples * channels];
    
    int samplePos = 0;
    while (source.getSamplesRemaining() > 0) {
      int grabbed = source.getSamples(samples, samplePos, source.getSamplesRemaining());
      samplePos += grabbed;
    }
    

    byte[] input = data.bytes;
    
    byte[] lenBytes = CryptoUtils.intToBytes(input.length, new byte[8], 0);
    CryptoUtils.intToBytes(data.plainLen, lenBytes, 4);
    
    int lenBytesLength = lenBytes.length;
    
    for (int i = 0; i < lenBytesLength; i++) {
      encodeByte(samples, lenBytes[i], 4 * i);
    }
    
    samplePos = 4 * lenBytesLength;
    
    for (int i = 0; i < input.length; i++) {
      encodeByte(samples, input[i], samplePos);
      samplePos += 4;
    }
    
    WavSampleData.writeWav(new ArraySampleData(samples, source.getNumChannels(), source.getSampleRate(), numSamples), new FileOutputStream(dest));
    
  }

  private void encodeByte(short[] samples, byte hidden, int samplePos) {
    int stenByte = hidden & 0xff;
    samples[samplePos] = (short)((samples[samplePos] & 0xfffc) + ((stenByte & 0xc0) >>> 6));
    samples[samplePos + 1] = (short)((samples[samplePos + 1] & 0xfffc) + ((stenByte & 0x30) >>> 4));
    samples[samplePos + 2] = (short)((samples[samplePos + 2] & 0xfffc) + ((stenByte & 0xc) >>> 2));
    samples[samplePos + 3] = (short)((samples[samplePos + 3] & 0xfffc) + (stenByte & 0x3));
    /*System.out.println(CryptoUtils.byteArrayToHexString(CryptoUtils.intArrayToByteArray(new int[]{samples[samplePos] & 0xffff})));
    System.out.println(CryptoUtils.byteArrayToHexString(CryptoUtils.intArrayToByteArray(new int[]{samples[samplePos + 1] & 0xffff})));
    System.out.println(CryptoUtils.byteArrayToHexString(CryptoUtils.intArrayToByteArray(new int[]{samples[samplePos + 2] & 0xffff})));
    System.out.println(CryptoUtils.byteArrayToHexString(CryptoUtils.intArrayToByteArray(new int[]{samples[samplePos + 3] & 0xffff})));
    System.out.println(stenByte);*/
  }
  
  private byte decodeByte(short[] samples, int samplePos) {
    int out = ((samples[samplePos] & 0x0003) << 6) + ((samples[samplePos + 1] & 0x0003) << 4) + ((samples[samplePos + 2] & 0x0003) << 2) + (samples[samplePos + 3] & 0x0003);
    /*System.out.println(CryptoUtils.byteArrayToHexString(CryptoUtils.intArrayToByteArray(new int[]{samples[samplePos] & 0xffff})));
    System.out.println(CryptoUtils.byteArrayToHexString(CryptoUtils.intArrayToByteArray(new int[]{samples[samplePos + 1] & 0xffff})));
    System.out.println(CryptoUtils.byteArrayToHexString(CryptoUtils.intArrayToByteArray(new int[]{samples[samplePos + 2] & 0xffff})));
    System.out.println(CryptoUtils.byteArrayToHexString(CryptoUtils.intArrayToByteArray(new int[]{samples[samplePos + 3] & 0xffff})));
    System.out.println(out);*/
    return (byte)out;
  }

  @Override
  public StenData decode(WavSampleData source) throws Exception {   
    int channels = source.getNumChannels();
    int numSamples = source.getSamplesRemaining();
    
    short[] samples = new short[numSamples * channels];
    
    int samplePos = 0;
    while (source.getSamplesRemaining() > 0) {
      int grabbed = source.getSamples(samples, samplePos, source.getSamplesRemaining());
      samplePos += grabbed;
    }
    
    byte[] lenBytes = new byte[8];
    int lenBytesLength = lenBytes.length;
    for (int i = 0; i < lenBytesLength; i++) {
      lenBytes[i] = decodeByte(samples, i * 4);
    }
    
    int codedLen = CryptoUtils.intFromBytes(lenBytes, 0);

    int plainLen = CryptoUtils.intFromBytes(lenBytes, 4);
    
    byte[] stenData = new byte[codedLen];
    
    samplePos = 4 * lenBytesLength;
    
    for(int i = 0; i < codedLen; i++) {
      stenData[i] = decodeByte(samples, samplePos);
      samplePos += 4;
    }
    
    return new StenData(stenData, plainLen);
  }

}
