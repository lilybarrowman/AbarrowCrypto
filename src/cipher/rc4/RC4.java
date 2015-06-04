package cipher.rc4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import stream.StreamRunnable;
import cipher.Cipher;
import core.CryptoException;
import core.CryptoUtils;

public class RC4 implements Cipher {

  private static final long serialVersionUID = 8159407569578990959L;
  
  private static final int MAX_KEY_LENGTH = 256;
  
  
  private byte[] S;
  
  public RC4() {
  }
  
  public RC4(byte[] key) {  
    setKey(key);
  }
  
  public byte[] nextBytes(byte[] bytes) {
    int i = 0;
    int j = 0;
    byte temp;
    for(int n = 0; n < bytes.length; n ++) {
        i = (i + 1) % 256;
        j = (j + (S[i] & 0xff)) % 256;
        temp = S[i];
        S[i] = S[j];
        S[j] = temp;
        bytes[n] = S[((S[i] + S[j]) & 0xff) % 256];
    }
    return bytes;
  }

  @Override
  public byte[] encrypt(byte[] input) throws CryptoException {
    byte[] output = nextBytes(new byte[input.length]);
    CryptoUtils.xorByteArrays(input, output, output);
    return output;
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    return encrypt(input);
  }
  
  public RC4 drop(int amount) {
    CryptoUtils.fillWithZeroes(nextBytes(new byte[amount]));
    return this;
  }

  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        int blockSize = 16;
        byte[] block = new byte[blockSize];
        byte[] output = new byte[blockSize];
        while (true) {
          int read = in.read(block);
          if (read == -1) {
            break;
          }
          if (read == blockSize) {
            nextBytes(output);
          } else {
            CryptoUtils.fillWithZeroes(output);
            output = new byte[read];
            nextBytes(output);
          }
          CryptoUtils.xorByteArrays(output, 0, block, 0, output, 0, read);
          out.write(output, 0, read);
        }
        CryptoUtils.fillWithZeroes(output);
        CryptoUtils.fillWithZeroes(block);
      }
    };
  }

  @Override
  public StreamRunnable decrypt() {
    return encrypt();
  }

  @Override
  public void setKey(byte[] key) {
    S = new byte[RC4.MAX_KEY_LENGTH];
    int i;
    
    for (i = 0; i < S.length; i++) {
      S[i] = (byte)i;
    }
    
    int j =0;
    byte temp;
    
    for (i = 0; i < S.length; i++) {
      j = (j + (S[i] & 0xff) + (key[i % key.length] & 0xff)) % 256;
      temp = S[i];
      S[i] = S[j];
      S[j] = temp;
    }
  }

  @Override
  public void removeKey() {
    if (!hasKey()) {
      return;
    }
    CryptoUtils.fillWithZeroes(S);
    S = null;
  }

  @Override
  public boolean hasKey() {
    return S != null;
  }

  @Override
  public void setIV(byte[] initVector) {
  }
  
  @Override
  public boolean hasIV() {
    return false;
  }

  @Override
  public int getPaddingSize() {
    return 0;
  }

  

}
