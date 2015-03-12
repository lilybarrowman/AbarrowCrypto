package core;

public interface CipherMaker {
  public Cipher makeCipher(byte[] key);
  public int[] getValidKeyLengths();
}
