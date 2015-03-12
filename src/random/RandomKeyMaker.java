package random;

import core.CryptoUtils;

public class RandomKeyMaker {
  
  public static byte[] makeKey(int length) {
    byte[] key = new byte[Math.max(8, length)];
        
    //time 
    CryptoUtils.longToBytes(System.nanoTime(), key, 0);
    
    Runtime runtime = Runtime.getRuntime();
    
    if(key.length >= 16) { 
      //free java ram
      CryptoUtils.longToBytes(runtime.freeMemory(), key, 8);
    }
    
    if(key.length >= 24) { 
      //free java ram after running gc
      runtime.gc();
      CryptoUtils.longToBytes(runtime.freeMemory(), key, 16);
    }
    
    return key;
  }
}
