package me.abarrow.cipher.blowfish;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BcryptTest {
  @Test
  public void testBcrypt() {
    long before = System.nanoTime();
    
    assertEquals(true, Bcrypt.verifyPassword("$2y$12$mACnM5lzNigHMaf7O1py1O3vlf6.BA8k8x3IoJ.Tq3IB/2e7g61Km",
        "correctbatteryhorsestapler".getBytes()));
    
    assertEquals(true, Bcrypt.verifyPassword("$2a$12$d1fF/ecIiO6IWVKw/tkSQuLwHUTyUoRbZlW7M/34zEgYmonZTY4DG",
        "eventhemostevillikerabbits".getBytes()));
        
    
    long after = System.nanoTime();

    System.out.println("Bcrypt took " + Math.round((after - before) / 1000000D) + "ms");
  }
}
