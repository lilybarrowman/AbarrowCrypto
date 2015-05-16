package padding;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

public class PKCS7Test {
  @Test
  public void testPadding() {
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54, -89, 1 }, PKCS7.pad(new byte[] { 67, 46, 12, 13, 54, -89 }, 7));
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54, 3, 3, 3 }, PKCS7.pad(new byte[] { 67, 46, 12, 13, 54 }, 8));
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54, 5, 5, 5, 5, 5 }, PKCS7.pad(new byte[] { 67, 46, 12, 13, 54 }, 5));
  }

  public void testUnpadding() {
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54, -89 }, PKCS7.unpad(new byte[] { 67, 46, 12, 13, 54, -89, 1 }));
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54 }, PKCS7.unpad(new byte[] { 67, 46, 12, 13, 54, 3, 3, 3 }));
    assertArrayEquals(new byte[] { 67, 46, 12, 13, 54 }, PKCS7.unpad(new byte[] { 67, 46, 12, 13, 54, 5, 5, 5, 5, 5 }));
  }

}
