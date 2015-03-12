package core;

public interface Cipher {
  public byte[] encrypt(byte[] input);
  public byte[] decrypt(byte[] input);
  public int cipherTextLength(int plainTextLength);
}
