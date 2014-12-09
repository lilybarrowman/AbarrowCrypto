package core;

public abstract class Hasher {
    
  public abstract Hasher addBytes(byte[] bytes);
  
  public abstract byte[] computeHash();
  
  public abstract Hasher reset();
  
  public abstract int getBlockBytes();
  
  public abstract int getHashByteLength();
  
  public final Hasher addString(String string) {
    addBytes(string.getBytes());
    return this;
  }
  
  public String computeHashString() {
    return CryptoUtils.byteArrayToHexString(computeHash());
  }

}
