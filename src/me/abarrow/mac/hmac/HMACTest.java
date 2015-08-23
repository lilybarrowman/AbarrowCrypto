package me.abarrow.mac.hmac;

import static org.junit.Assert.*;
import java.io.IOException;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import me.abarrow.core.CryptoException;
import me.abarrow.hash.Hasher;
import me.abarrow.hash.md.MD5;
import me.abarrow.hash.sha.SHA1;

import org.junit.Test;

public class HMACTest {

  @Test
  public void testHMAC() throws CryptoException, IOException {
    testSuccessfulCase(new SHA1(), new byte[0], new byte[0], parseHexBinary("fbdb1d1b18aa6c08324b7d64b71fb76370690e1d"));
    testSuccessfulCase(new SHA1(), "key".getBytes(), "The quick brown fox jumps over the lazy dog".getBytes(),
        parseHexBinary("de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9"));
    testSuccessfulCase(new MD5(), new byte[0], new byte[0], parseHexBinary("74e6f7298a9c2d168935f58c001bad88"));
    testSuccessfulCase(new MD5(), "key".getBytes(), "The quick brown fox jumps over the lazy dog".getBytes(),
        parseHexBinary("80070713463e7749b90c2dc24911e275"));

  }

  private void testSuccessfulCase(Hasher hash, byte[] key, byte[] data, byte[] expectedTag) throws CryptoException,
      IOException {
    HMAC mac = new HMAC(hash, key);

    byte[] taggedData = new byte[data.length + expectedTag.length];
    System.arraycopy(data, 0, taggedData, 0, data.length);
    System.arraycopy(expectedTag, 0, taggedData, data.length, expectedTag.length);

    byte[] badlyTaggedData = new byte[taggedData.length];
    System.arraycopy(data, 0, badlyTaggedData, 0, data.length);

    assertArrayEquals(expectedTag, mac.tag(data, true));
    assertArrayEquals(taggedData, mac.tag(data, false));    
    assertArrayEquals(expectedTag, mac.tag(true).start(data));
    assertArrayEquals(taggedData, mac.tag(false).start(data));
    
    assertArrayEquals(data, mac.checkTag(taggedData, false));
    assertArrayEquals(data, mac.checkTag(false).start(taggedData));

    
    try {
      mac.checkTag(taggedData, true);
    } catch (CryptoException e) {
      fail(e.getMessage());
    }
    try {
      mac.checkTag(true).start(taggedData);
    } catch(IOException e) {
      fail(e.getMessage());
    }

    
    assertArrayEquals(taggedData, mac.tag(data, false));    
    assertArrayEquals(expectedTag, mac.tag(true).start(data));
    assertArrayEquals(taggedData, mac.tag(false).start(data));
    

    try {
      mac.checkTag(badlyTaggedData, false);
      fail("Checking badlyTaggedData was ok.");
    } catch (CryptoException e) {
    }

    try {
      mac.checkTag(badlyTaggedData, true);
      fail("Checking badlyTaggedData was ok.");
    } catch (CryptoException e) {
    }

    try {
      mac.checkTag(false).start(badlyTaggedData);
      fail("Checking badlyTaggedData was ok.");
    } catch (IOException e) {
    }

    try {
      mac.checkTag(true).start(badlyTaggedData);
      fail("Checking badlyTaggedData was ok.");
    } catch (IOException e) {
    }
  }

}
