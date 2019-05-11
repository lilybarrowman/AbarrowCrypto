package me.abarrow.stream;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

public class SuffixStreamTest {

  @Test
  public void successfulTest() throws IOException {
    String s = "Food is the best!";
    InputStream inner = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    SuffixStream suffixed = new SuffixStream(inner, 4);
    
    assertTrue(suffixed.hasFullSuffix());
    byte[] suffix = suffixed.getSuffix();
    assertEquals(4, suffix.length);
    String suffixStr = new String(suffix, StandardCharsets.UTF_8);
    assertEquals("est!", suffixStr);
    
    byte[] remainingBuffer = new byte[256];
    int read = suffixed.read(remainingBuffer, 0, remainingBuffer.length);
    byte[] snipped = Arrays.copyOf(remainingBuffer, read);
    String prefixStr = new String(snipped, StandardCharsets.UTF_8);
    assertEquals("Food is the b", prefixStr);
  }
  
  @Test
  public void unsuccessfulTest() throws IOException {
    String s = "Food";
    InputStream inner = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    SuffixStream suffixed = new SuffixStream(inner, 8);
    
    assertTrue(!suffixed.hasFullSuffix());
    byte[] suffix = suffixed.getSuffix();
    assertEquals(4, suffix.length);
    String suffixStr = new String(suffix, StandardCharsets.UTF_8);
    assertEquals("Food", suffixStr);
    
    assertEquals(-1, suffixed.read());
  }

}
