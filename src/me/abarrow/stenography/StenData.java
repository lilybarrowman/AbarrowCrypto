package me.abarrow.stenography;

public class StenData {
  public byte[] bytes;
  public int plainLen;
  
  public StenData(byte[] data, int plainTextBytes) {
    bytes = data;
    plainLen = plainTextBytes;
  }
}
