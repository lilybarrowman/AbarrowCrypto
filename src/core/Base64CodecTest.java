package core;

import static org.junit.Assert.*;

import org.junit.Test;

public class Base64CodecTest {

  private static final String ALL_YOUR_BASE_BASE64 = "QWxsIHlvdXIgYmFzZSBhcmUgYmVsb25nIHRvIHVzLg==";
  private static final String ALL_YOUR_BASE_STR = "All your base are belong to us.";
  private static final String ABC_BASE64 = "YWJj";
  private static final String AB_BASE64 = "YWI=";
  private static final String A_BASE64 = "YQ==";
  private static final String ABC_STR = "abc";
  private static final String AB_STR = "ab";
  private static final String A_STR = "a";

  @Test
  public void testBase64Encoding() {
    assertEquals(Base64Codec.getStandardBase64Codec().encode(A_STR.getBytes()), A_BASE64);
    assertEquals(Base64Codec.getStandardBase64Codec().encode(AB_STR.getBytes()), AB_BASE64);
    assertEquals(Base64Codec.getStandardBase64Codec().encode(ABC_STR.getBytes()), ABC_BASE64);
    assertEquals(Base64Codec.getStandardBase64Codec().encode(ALL_YOUR_BASE_STR.getBytes()), ALL_YOUR_BASE_BASE64);
  }
  
  @Test
  public void testBase64Decoding() {
    
    assertArrayEquals(Base64Codec.getStandardBase64Codec().decode(A_BASE64), A_STR.getBytes()); 
    assertArrayEquals(Base64Codec.getStandardBase64Codec().decode(AB_BASE64), AB_STR.getBytes());
    assertArrayEquals(Base64Codec.getStandardBase64Codec().decode(ABC_BASE64), ABC_STR.getBytes());
    assertArrayEquals(Base64Codec.getStandardBase64Codec().decode(ALL_YOUR_BASE_BASE64), ALL_YOUR_BASE_STR.getBytes());
  }
  
}
