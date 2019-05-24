package me.abarrow.cipher.serpent;

import static org.junit.Assert.assertArrayEquals;

import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;

import org.junit.Test;

public class SerpentTest {
  @Test
  public void testEncoding() throws CryptoException {
    
    assertArrayEquals(CryptoUtils.parseHexString("264E5481EFF42A4606ABDA06C0BFDA3D"), new Serpent(CryptoUtils
        .parseHexString("80000000000000000000000000000000")).encryptBlock(CryptoUtils
        .parseHexString("00000000000000000000000000000000")));
    
    
    assertArrayEquals(CryptoUtils.parseHexString("00000000000000000000000000000000"), new Serpent(CryptoUtils
        .parseHexString("80000000000000000000000000000000")).decryptBlock(CryptoUtils
        .parseHexString("264E5481EFF42A4606ABDA06C0BFDA3D")));
    
    
    assertArrayEquals(CryptoUtils.parseHexString("EA024714AD5C4D84EA024714AD5C4D84"), new Serpent(CryptoUtils
        .parseHexString("2BD6459F82C5B300952C49104881FF48")).encryptBlock(CryptoUtils
        .parseHexString("BEB6C069393822D3BE73FF30525EC43E")));
    
    
    assertArrayEquals(CryptoUtils.parseHexString("BEB6C069393822D3BE73FF30525EC43E"), new Serpent(CryptoUtils
        .parseHexString("2BD6459F82C5B300952C49104881FF48")).decryptBlock(CryptoUtils
        .parseHexString("EA024714AD5C4D84EA024714AD5C4D84")));
    
    
    assertArrayEquals(CryptoUtils.parseHexString("A223AA1288463C0E2BE38EBD825616C0"), new Serpent(CryptoUtils
        .parseHexString("8000000000000000000000000000000000000000000000000000000000000000")).encryptBlock(CryptoUtils
        .parseHexString("00000000000000000000000000000000")));
    
    
    assertArrayEquals(CryptoUtils.parseHexString("00000000000000000000000000000000"), new Serpent(CryptoUtils
        .parseHexString("8000000000000000000000000000000000000000000000000000000000000000")).decryptBlock(CryptoUtils
        .parseHexString("A223AA1288463C0E2BE38EBD825616C0")));
    
    
    assertArrayEquals(CryptoUtils.parseHexString("EA024714AD5C4D84EA024714AD5C4D84"), new Serpent(CryptoUtils
        .parseHexString("2BD6459F82C5B300952C49104881FF482BD6459F82C5B300952C49104881FF48")).encryptBlock(CryptoUtils
        .parseHexString("677C8DFAA08071743FD2B415D1B28AF2")));
    
    
    assertArrayEquals(CryptoUtils.parseHexString("677C8DFAA08071743FD2B415D1B28AF2"), new Serpent(CryptoUtils
        .parseHexString("2BD6459F82C5B300952C49104881FF482BD6459F82C5B300952C49104881FF48")).decryptBlock(CryptoUtils
        .parseHexString("EA024714AD5C4D84EA024714AD5C4D84")));
    
  }
}
