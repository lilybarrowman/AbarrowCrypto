package cipher.serpent;

import static org.junit.Assert.assertArrayEquals;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import core.CryptoException;

public class SerpentTest {
  @Test
  public void testEncoding() throws CryptoException {
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("264E5481EFF42A4606ABDA06C0BFDA3D"), new Serpent(DatatypeConverter
        .parseHexBinary("80000000000000000000000000000000")).encryptBlock(DatatypeConverter
        .parseHexBinary("00000000000000000000000000000000")));
    
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("00000000000000000000000000000000"), new Serpent(DatatypeConverter
        .parseHexBinary("80000000000000000000000000000000")).decryptBlock(DatatypeConverter
        .parseHexBinary("264E5481EFF42A4606ABDA06C0BFDA3D")));
    
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("EA024714AD5C4D84EA024714AD5C4D84"), new Serpent(DatatypeConverter
        .parseHexBinary("2BD6459F82C5B300952C49104881FF48")).encryptBlock(DatatypeConverter
        .parseHexBinary("BEB6C069393822D3BE73FF30525EC43E")));
    
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("BEB6C069393822D3BE73FF30525EC43E"), new Serpent(DatatypeConverter
        .parseHexBinary("2BD6459F82C5B300952C49104881FF48")).decryptBlock(DatatypeConverter
        .parseHexBinary("EA024714AD5C4D84EA024714AD5C4D84")));
    
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("A223AA1288463C0E2BE38EBD825616C0"), new Serpent(DatatypeConverter
        .parseHexBinary("8000000000000000000000000000000000000000000000000000000000000000")).encryptBlock(DatatypeConverter
        .parseHexBinary("00000000000000000000000000000000")));
    
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("00000000000000000000000000000000"), new Serpent(DatatypeConverter
        .parseHexBinary("8000000000000000000000000000000000000000000000000000000000000000")).decryptBlock(DatatypeConverter
        .parseHexBinary("A223AA1288463C0E2BE38EBD825616C0")));
    
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("EA024714AD5C4D84EA024714AD5C4D84"), new Serpent(DatatypeConverter
        .parseHexBinary("2BD6459F82C5B300952C49104881FF482BD6459F82C5B300952C49104881FF48")).encryptBlock(DatatypeConverter
        .parseHexBinary("677C8DFAA08071743FD2B415D1B28AF2")));
    
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("677C8DFAA08071743FD2B415D1B28AF2"), new Serpent(DatatypeConverter
        .parseHexBinary("2BD6459F82C5B300952C49104881FF482BD6459F82C5B300952C49104881FF48")).decryptBlock(DatatypeConverter
        .parseHexBinary("EA024714AD5C4D84EA024714AD5C4D84")));
    
  }
}
