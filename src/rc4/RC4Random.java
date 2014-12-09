package rc4;

import java.util.Random;

import random.RandomKeyMaker;
import core.CryptoUtils;

public class RC4Random extends Random {

  private static final long serialVersionUID = 8159407569578990959L;
  
  private static final int MAX_KEY_LENGTH = 256;
  
  
  private byte[] S;
  
  public RC4Random() {
    this(RandomKeyMaker.makeKey(RC4Random.MAX_KEY_LENGTH));
  }
  
  public RC4Random(long seed) {
    this(CryptoUtils.longToBytes(seed, new byte[8], 0));
  }
  
  public RC4Random(byte[] key) {  
    S = new byte[RC4Random.MAX_KEY_LENGTH];
    
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
  protected int next(int bits) {
    byte[] gen = new byte[4];
    nextBytes(gen);
    return CryptoUtils.intFromBytes(gen, 0) >>> (32 - bits);
  }
  
  @Override
  public void nextBytes(byte[] bytes) {
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
  }

}
