package hmac;

import static org.junit.Assert.*;
import md.MD5;

import org.junit.Test;

import sha.SHA1;

public class HMACTest {

  @Test
  public void testHMAC() {
    assertEquals(((new HMAC(new SHA1())).computeHashString("", "")), "fbdb1d1b18aa6c08324b7d64b71fb76370690e1d");

    assertEquals(((new HMAC(new SHA1())).computeHashString("key", "The quick brown fox jumps over the lazy dog")),
        "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9");
    
    assertEquals(((new HMAC(new MD5())).computeHashString("", "")), "74e6f7298a9c2d168935f58c001bad88");

    assertEquals(((new HMAC(new MD5())).computeHashString("key", "The quick brown fox jumps over the lazy dog")),
        "80070713463e7749b90c2dc24911e275");
  }

}
