package core;

public abstract class SymmetricStreamCipher {
  
  
  public byte[] codec(byte[] input) {
    return codec(input, 0, input.length);
  }

  public abstract byte[] codec(byte[] input, int startPos, int bytesToCode);
  
  
  public String codec(String inputString) {
    return codec(inputString, 0, 2 * inputString.length());
  }

  public String codec(String inputString, int startPos, int bytesToCode) {
    return new String(codec(inputString.getBytes(), startPos, bytesToCode));
  }
}
