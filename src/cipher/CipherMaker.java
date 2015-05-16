package cipher;


public interface CipherMaker {
  public Cipher makeCipher(byte[] key);
  public int[] getValidKeyLengths();
}
