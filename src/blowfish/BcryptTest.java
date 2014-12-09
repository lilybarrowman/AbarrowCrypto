package blowfish;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BcryptTest {
  @Test
  public void testBcrypt() {
    long before = System.nanoTime();
    
    assertEquals(Bcrypt.verifyPassword("$2y$12$mACnM5lzNigHMaf7O1py1O3vlf6.BA8k8x3IoJ.Tq3IB/2e7g61Km",
        "correctbatteryhorsestapler"), true);
    
    long after = System.nanoTime();

    System.out.println("Bcrypt took " + Math.round((after - before) / 1000000D) + "ms");
  }
}
