package core;

public class CryptoException extends Exception {
  private static final long serialVersionUID = 8615144681824758259L;
  public static final String INVALID_LENGTH = "Source length is not a multiple of the block size"
      + "therefore cannot it be properly processed.";
  
  public static final String NO_KEY = "No key has been provided for this cipher.";
  
  public static final String NO_BLOCK_SIZE = "No block size has been provided for this cipher.";
  
  public static final String NO_IV = "No IV has been provided for this cipher.";
  
  public CryptoException(String error) {
    super(error);
  }

}
