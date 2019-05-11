package me.abarrow.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import me.abarrow.core.CryptoUtils;

public class PrefixStream extends InputStream {
  
  private byte[] prefix;
  private InputStream inner;
  private int prefixDesiredLength;
  
  public PrefixStream(InputStream in, int prefixLength) throws IOException {
    inner = in;
    prefixDesiredLength = prefixLength;
    prefix = new byte[prefixDesiredLength];
    int read = inner.read(prefix, 0, prefixDesiredLength);
    if (read == -1) {
      prefix = new byte[0];
    } else if (read != prefixDesiredLength) {
      byte[] other = Arrays.copyOf(prefix, read);
      CryptoUtils.fillWithZeroes(prefix);
      prefix = other;
    }
  }
  
  public boolean hasFullPrefix() {
    return (prefixDesiredLength == prefix.length);
  }
  
  public byte[] getPrefix() {
    return Arrays.copyOf(prefix, prefix.length);
  }
  
  @Override
  public int read() throws IOException {
    return inner.read();
  }
  
  @Override 
  public int read(byte[] b, int off, int len) throws IOException {
    return inner.read(b, off, len);
  }
  
  @Override
  public void close() throws IOException {
    CryptoUtils.fillWithZeroes(prefix);
    inner.close();
  }
  
  @Override
  public boolean markSupported() {
    return false;
  }
  
  @Override
  public long skip(long n) throws IOException {
    return inner.skip(n);
  }

}
