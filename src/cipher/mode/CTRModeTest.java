package cipher.mode;

import static org.junit.Assert.assertArrayEquals;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;

import org.junit.Test;

import core.CryptoException;
import cipher.IVGrabbingCipher;
import cipher.aes.AES;

public class CTRModeTest {
  @Test
  public void testEncoding() throws CryptoException {

    // partial block
    assertArrayEquals(parseHexBinary("b088b0a1a58c28a0"), new CTRMode(new AES(
        parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")), parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))
        .encrypt(parseHexBinary("3243f6a8885a308d")));

    // single block
    assertArrayEquals(parseHexBinary("b088b0a1a58c28a055e2656f5ee996a6"), new CTRMode(new AES(
        parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")), parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))
        .encrypt(parseHexBinary("3243f6a8885a308d313198a2e0370734")));
    
    assertArrayEquals(parseHexBinary("3243f6a8885a308d313198a2e0370734"), new CTRMode(new AES(
        parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")), parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))
        .decrypt(parseHexBinary("b088b0a1a58c28a055e2656f5ee996a6")));
    
    
    //iv grabbing
    assertArrayEquals(parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890b088b0a1a58c28a055e2656f5ee996a6"), new IVGrabbingCipher(new CTRMode(new AES(
        parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")), parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890")))
        .encrypt(parseHexBinary("3243f6a8885a308d313198a2e0370734")));
    
    assertArrayEquals(parseHexBinary("3243f6a8885a308d313198a2e0370734"), new IVGrabbingCipher(new CTRMode(new AES(
        parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")), parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")))
        .decrypt(parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890b088b0a1a58c28a055e2656f5ee996a6")));
    
    // repeated block
    assertArrayEquals(parseHexBinary("b088b0a1a58c28a055e2656f5ee996a6c13906506eafe58d2419f42fa70a5bde"),
        new CTRMode(new AES(parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")),
            parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))
            .encrypt(parseHexBinary("3243f6a8885a308d313198a2e03707343243f6a8885a308d313198a2e0370734")));

    assertArrayEquals(parseHexBinary("3243f6a8885a308d313198a2e03707343243f6a8885a308d313198a2e0370734"),
        new CTRMode(new AES(parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")),
            parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))
            .decrypt(parseHexBinary("b088b0a1a58c28a055e2656f5ee996a6c13906506eafe58d2419f42fa70a5bde")));

  }
}
