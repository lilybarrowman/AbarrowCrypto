package core;

public abstract class StreamCipher implements Cipher {
  
  
  public abstract byte[] codec(byte[] input);
  
  @Override
  public final byte[] decrypt(byte[] input) {
    return this.codec(input);
  }
  
  @Override
  public final byte[] encrypt(byte[] input) {
    return this.codec(input);
  }
  
  @Override
  public final int cipherTextLength(int plainTextLength) {
    return plainTextLength;
  }
}
