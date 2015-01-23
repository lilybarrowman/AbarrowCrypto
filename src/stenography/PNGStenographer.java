package stenography;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import core.CryptoUtils;

public class PNGStenographer {

  public static boolean canImageHoldData(int numBytes, int wide, int high) {
    int numPixels = 12 + (numBytes + 2) / 3 * 4;

    return (wide * high) > numPixels;
  }
  
  public static void encode(StenData data, BufferedImage source, File dest) throws IOException {
    byte[] input = data.bytes;
    
    int wide = source.getWidth();
    int high = source.getHeight();
    
    if (!canImageHoldData(input.length, wide, high)) {
      throw new IllegalArgumentException("The image is too small to store that data!");
    }
    
    int[] pixelData = new int[wide * high];
    source.getRGB(0, 0, wide, high, pixelData, 0, wide);

    byte[] lenBytes = CryptoUtils.intToBytes(input.length, new byte[8], 0);
    CryptoUtils.intToBytes(data.plainLen, lenBytes, 4);

    for (int i = 0; i < 3; i++) {
      pixelData[i] = encodeByte(pixelData[i], lenBytes[i]);
    }
    
    
    encodeBytes(pixelData, 0, lenBytes, 0);
    encodeBytes(pixelData, 4, lenBytes, 3);
    encodeBytes(pixelData, 8, lenBytes, 6);

    int pixelPos = 12;
    int dataPos = 0;

    while(dataPos < input.length) { 
      encodeBytes(pixelData, pixelPos, input, dataPos);
      dataPos += 3;
      pixelPos += 4;
    }

    
    BufferedImage resultImage = new BufferedImage(wide, high, BufferedImage.TYPE_INT_RGB);

    resultImage.setRGB(0, 0, wide, high, pixelData, 0, wide);

    ImageIO.write(resultImage, "png", dest);

  }
  
  private static void encodeBytes(int[] pixelData, int pixelPos, byte[] data, int dataPos) {
    int modA = data[dataPos] & 0xff;
    //the loop over here might leave some nonsense data in the image but will simplify the array logic and allow the algorithm to run faster 
    int modB = data[(dataPos + 1) % data.length] & 0xff;
    int modC = data[(dataPos + 2) % data.length] & 0xff;
    
    int src = pixelData[pixelPos];
    pixelData[pixelPos] = (src & 0xff000000) + (src & 0xfc0000) + ((modA & 0xc0) << 10) + (src & 0xfc00) + ((modA & 0x30) << 4) + (src & 0xfc) + ((modA & 0xc) >>> 2);

    src = pixelData[pixelPos + 1];
    pixelData[pixelPos + 1] = (src & 0xff000000) + (src & 0xfc0000) + ((modA & 0x3) << 16) + (src & 0xfc00) + ((modB & 0xc0) << 2) + (src & 0xfc) + ((modB & 0x30) >>> 4);
    
    src = pixelData[pixelPos + 2];
    pixelData[pixelPos + 2] = (src & 0xff000000) + (src & 0xfc0000) + ((modB & 0xc) << 14) + (src & 0xfc00) + ((modB & 0x3) << 8) + (src & 0xfc) + ((modC & 0xc0) >>> 6);
   
    src = pixelData[pixelPos + 3];
    pixelData[pixelPos + 3] = (src & 0xff000000) + (src & 0xfc0000) + ((modC & 0x30) << 12) + (src & 0xfc00) + ((modC & 0xc) << 6) + (src & 0xfc) + (modC & 0x3);
  }
  
  private static void decodeBytes(int[] pixelData, int pixelPos, byte[] output, int outPos) {
    int pixA = pixelData[pixelPos];
    int pixB = pixelData[pixelPos + 1];
    int pixC = pixelData[pixelPos + 2];
    int pixD = pixelData[pixelPos + 3];
    output[outPos] = (byte)(((pixA & 0x30000) >>> 10) + ((pixA & 0x300) >>> 4) + ((pixA & 0x3) << 2) + ((pixB & 0x30000) >>> 16));
    //consider refactoring this code so there aren't any if statements
    if((outPos + 1) < output.length) {
      output[outPos + 1] = (byte)(((pixB & 0x300) >>> 2) + ((pixB & 0x3) << 4) + ((pixC & 0x30000) >>> 14) + ((pixC & 0x300) >>> 8));
    }
    if((outPos + 2) < output.length) {
      output[outPos + 2] = (byte)(((pixC & 0x3) << 6) + ((pixD & 0x30000) >>> 12) + ((pixD & 0x300) >> 6) + (pixD & 0x3));
    }
  }

  public static StenData decode(BufferedImage source) {

    int wide = source.getWidth();
    int high = source.getHeight();

    byte[] lenBytes = new byte[8];

    int[] sourceData = new int[wide * high];
    source.getRGB(0, 0, wide, high, sourceData, 0, wide);

    decodeBytes(sourceData, 0, lenBytes, 0);
    decodeBytes(sourceData, 4, lenBytes, 3);
    decodeBytes(sourceData, 8, lenBytes, 6);

    int codedLen = CryptoUtils.intFromBytes(lenBytes, 0);

    int plainLen = CryptoUtils.intFromBytes(lenBytes, 4);

    byte[] stenData = new byte[codedLen];
    
    int pixelPos = 12;
    int dataPos = 0;
    
    while(dataPos < codedLen) {
      decodeBytes(sourceData, pixelPos, stenData, dataPos);
      pixelPos += 4;
      dataPos += 3;
    }

    return new StenData(stenData, plainLen);

  }

}
