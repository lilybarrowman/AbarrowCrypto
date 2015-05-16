package padding;

public class PKCS7 {
  public static byte[] pad(byte[] input, int blockSize) {
    byte[] output = new byte[input.length + blockSize - (input.length % blockSize)];
    System.arraycopy(input, 0, output, 0, input.length);
    byte diff = (byte)(output.length - input.length);
    for(int n = input.length, end = output.length; n < end; n++) {
      output[n] = diff;
    }
    return output;
  }
  
  public static byte[] unpad(byte[] input) {
    int diff = input[input.length - 1];
    byte[] output = new byte[input.length - diff];
    System.arraycopy(input, 0, output, 0, output.length);
    return output;
  }
}
