package stenography;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import core.CryptoUtils;

public class PNGStenographer {
  
  public static void encode(StenData data, BufferedImage source, File dest) throws IOException {
    byte[] output = data.bytes;
    
    int wide = source.getWidth();
    int high = source.getHeight();
    
    if (wide * high <  (4 + output.length)) {
      throw new IllegalArgumentException("The image is too small to store that data!");
    }
    
    int[] sourceData = new int[wide * high];
    source.getRGB(0, 0, wide, high, sourceData, 0, wide);
   
    byte[] lenBytes = CryptoUtils.intToBytes(output.length, new byte[8], 0);
    CryptoUtils.intToBytes(data.plainLen, lenBytes, 4);
    
    for (int i = 0; i < 8; i++) {
      sourceData[i] = encodeByte(sourceData[i], lenBytes[i]);
    }
    
    for (int i = 0; i < output.length; i++) {
      sourceData[i + 8] = encodeByte(sourceData[i + 8], output[i]);
    }
    
    BufferedImage resultImage = new BufferedImage(wide, high, BufferedImage.TYPE_INT_ARGB);
    
    resultImage.setRGB(0, 0, wide, high, sourceData, 0, wide);
    
    ImageIO.write(resultImage, "png", dest);
    
  }
  
  private static int encodeByte(int src, byte data) {
    int mod = data & 0xff;
    //first 6 bits of each channel and 2 bits from the mod
    int out = (src & 0xfc000000) + ((mod & 0xc0) << 18);
    out += (src & 0xfc0000) + ((mod & 0x30) << 12);
    out += (src & 0xfc00) + ((mod & 0xc) << 6);
    out += (src & 0xfc) + (mod & 0x3);
    //System.out.println(CryptoUtils.hexStringToBinaryString(CryptoUtils.intArrayToHexString(new int[]{src})));
    //System.out.println((mod & 0xc0) + " " + (mod & 0x30) + " " + (mod & 0xc) + " " + (mod & 0x3));
    //System.out.println(CryptoUtils.hexStringToBinaryString(CryptoUtils.intArrayToHexString(new int[]{out})));
    return out;
  }
  
  private static byte decodeByte(int src) {
    int out = (src & 0x3000000) >>> 18;
    out += (src & 0x30000) >>> 12;
    out += (src & 0x300) >>> 6;
    out += src & 0x3;
    return (byte)out;
  }
  
  public static StenData decode(BufferedImage source) {
    
    int wide = source.getWidth();
    int high = source.getHeight();
    
    
    byte[] lenBytes = new byte[8];
    
    int[] sourceData = new int[wide * high];
    source.getRGB(0, 0, wide, high, sourceData, 0, wide);
    
    for (int i = 0; i < 8; i++) {
      lenBytes[i] = decodeByte(sourceData[i]);
    }
    
    int codedLen = CryptoUtils.intFromBytes(lenBytes, 0);
    
    int plainLen = CryptoUtils.intFromBytes(lenBytes, 4);
    
    byte[] stenData = new byte[codedLen];
    
   
    for (int i = 0; i < codedLen; i++) {
      stenData[i] = decodeByte(sourceData[i + 8]);
    }
    
    return new StenData(stenData, plainLen);
    
  }

}
