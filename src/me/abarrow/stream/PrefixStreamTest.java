package me.abarrow.stream;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

public class PrefixStreamTest {

  @Test
  public void successfulTest() throws IOException {
    String s = "Food is the best!";
    InputStream inner = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    PrefixStream prefixed = new PrefixStream(inner, 4);
    
    assertTrue(prefixed.hasFullPrefix());
    byte[] prefix = prefixed.getPrefix();
    assertEquals(4, prefix.length);
    String prefixStr = new String(prefix, StandardCharsets.UTF_8);
    assertEquals("Food", prefixStr);
    
    byte[] remainingBuffer = new byte[256];
    int read = prefixed.read(remainingBuffer, 0, remainingBuffer.length);
    byte[] snipped = Arrays.copyOf(remainingBuffer, read);
    String suffixStr = new String(snipped, StandardCharsets.UTF_8);
    assertEquals(" is the best!", suffixStr);
  }
  
  @Test
  public void unsuccessfulTest() throws IOException {
    String s = "Food";
    InputStream inner = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    PrefixStream prefixed = new PrefixStream(inner, 8);
    
    assertTrue(!prefixed.hasFullPrefix());
    byte[] prefix = prefixed.getPrefix();
    assertEquals(4, prefix.length);
    String prefixStr = new String(prefix, StandardCharsets.UTF_8);
    assertEquals("Food", prefixStr);
    
    assertEquals(-1, prefixed.read());
  }

}
