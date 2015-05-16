package hash.md;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MD5Test {
  @Test
  public void SHA1ProperlyHashesStrings() {
    assertEquals((new MD5()).addString("").computeHashString(), "d41d8cd98f00b204e9800998ecf8427e");
    assertEquals((new MD5()).addString("a").computeHashString(), "0cc175b9c0f1b6a831c399e269772661");
    assertEquals((new MD5()).addString("abc").computeHashString(), "900150983cd24fb0d6963f7d28e17f72");
    assertEquals((new MD5()).addString("message digest").computeHashString(),
        "f96b697d7cb7938d525a2f31aaf161d0");
    assertEquals((new MD5()).addString("abcdefghijklmnopqrstuvwxyz").computeHashString(),
        "c3fcd3d76192e4007dfb496cca67e13b");
    assertEquals((new MD5()).addString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")
        .computeHashString(), "d174ab98d277d9f5a5611c2c9f419d9f");
    assertEquals((new MD5()).addString(
        "12345678901234567890123456789012345678901234567890123456789012345678901234567890").computeHashString(),
        "57edf4a22be3c955ac49da2e2107b67a");
  }
}
