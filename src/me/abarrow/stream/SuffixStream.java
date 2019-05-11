package me.abarrow.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import me.abarrow.core.CryptoUtils;

public class SuffixStream extends InputStream {
  
  private byte[] suffix;
  private byte[] buffer;
  private InputStream inner;
  private int suffixDesiredLength;
  
  class CopyFreeByteArrayOutputStream extends ByteArrayOutputStream {
    public byte[] getBuffer() {
      return buf;
    }
    
    public int getCount() {
      return count;
    }
  }
  
  public SuffixStream(InputStream in, int suffixLength) throws IOException {
    suffixDesiredLength = suffixLength;
    
    CopyFreeByteArrayOutputStream out = new CopyFreeByteArrayOutputStream();
    byte[] chunk = new byte[1024];
    while (true) {
      int read = in.read(chunk);
      if (read == chunk.length) {
        out.write(chunk);
      } else if (read == -1) {
        break;
      } else {
        out.write(chunk, 0, read);
      }
    }
    CryptoUtils.fillWithZeroes(chunk);
    in.close();    
    buffer = out.getBuffer();
    int count = out.getCount();
    out.close();

    
    if (count >= suffixDesiredLength) {
      suffix = new byte[suffixDesiredLength];
      System.arraycopy(buffer, count - suffixDesiredLength, suffix, 0, suffixDesiredLength);
      inner = new ByteArrayInputStream(buffer, 0, count - suffixDesiredLength);
    } else {
     suffix = Arrays.copyOf(buffer, count);
     inner = new ByteArrayInputStream(buffer, 0, 0);
    }
  }

  public boolean hasFullSuffix() {
    return (suffixDesiredLength == suffix.length);
  }
  
  public byte[] getSuffix() {
    return Arrays.copyOf(suffix, suffix.length);
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
    CryptoUtils.fillWithZeroes(suffix);
    CryptoUtils.fillWithZeroes(buffer);
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
