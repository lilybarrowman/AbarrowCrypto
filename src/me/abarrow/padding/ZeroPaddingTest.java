package me.abarrow.padding;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import me.abarrow.core.CryptoException;

import org.junit.Test;

public class ZeroPaddingTest {
  @Test
  public void testPadding() throws IOException, InterruptedException, CryptoException {    
    testPad(7, new byte[] { 67, 46, 12, 13, 54, -89 }, new byte[] { 67, 46, 12, 13, 54, -89, 0 });
    testPad(5, new byte[] { 67, 46, 12, 13, 54 }, new byte[] { 67, 46, 12, 13, 54});
    testPad(8, new byte[] { 67, 46, 12, 13, 54 }, new byte[] { 67, 46, 12, 13, 54, 0, 0, 0 });
  }
  
  public void testPad(int blockSize, byte[] input, byte[] expected) throws InterruptedException, IOException, CryptoException {
    ZeroPadding padding = new ZeroPadding(blockSize);
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    padding.pad().startSync(new ByteArrayInputStream(input), o);
    assertArrayEquals(expected, o.toByteArray());
    assertArrayEquals(expected, padding.pad(input));
  }
  
  public void testUnpad(int blockSize, byte[] input, byte[] expected) throws InterruptedException, IOException {
    ZeroPadding padding = new ZeroPadding(blockSize);
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    padding.unpad().startSync(new ByteArrayInputStream(input), o);
    assertArrayEquals(expected, o.toByteArray());
    assertArrayEquals(expected, padding.unpad(input));
  }

  @Test
  public void testUnpadding() throws IOException, InterruptedException {    
    testUnpad(7, new byte[] { 67, 46, 12, 13, 54, -89, 0 }, new byte[] { 67, 46, 12, 13, 54, -89, 0 });
  }
}
