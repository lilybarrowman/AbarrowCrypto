package random;

import static org.junit.Assert.assertArrayEquals;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import cipher.aes.AES;

public class CTRModeCipherTest {
  @Test
  public void testEncoding() {

    // partial block
    assertArrayEquals(DatatypeConverter.parseHexBinary("b088b0a1a58c28a0"), new RandomStreamCipher(new CTRModeRandom(
        new AES(DatatypeConverter.parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")), DatatypeConverter
            .parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))).codec(DatatypeConverter
        .parseHexBinary("3243f6a8885a308d")));

    // single block
    assertArrayEquals(DatatypeConverter.parseHexBinary("b088b0a1a58c28a055e2656f5ee996a6"), new RandomStreamCipher(
        new CTRModeRandom(new AES(DatatypeConverter.parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")),
            DatatypeConverter.parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))).codec(DatatypeConverter
        .parseHexBinary("3243f6a8885a308d313198a2e0370734")));

    assertArrayEquals(DatatypeConverter.parseHexBinary("3243f6a8885a308d313198a2e0370734"), new RandomStreamCipher(
        new CTRModeRandom(new AES(DatatypeConverter.parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")),
            DatatypeConverter.parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))).codec(DatatypeConverter
        .parseHexBinary("b088b0a1a58c28a055e2656f5ee996a6")));

    // repeated block
    assertArrayEquals(DatatypeConverter
        .parseHexBinary("b088b0a1a58c28a055e2656f5ee996a6c13906506eafe58d2419f42fa70a5bde"), new RandomStreamCipher(
        new CTRModeRandom(new AES(DatatypeConverter.parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")),
            DatatypeConverter.parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))).codec(DatatypeConverter
        .parseHexBinary("3243f6a8885a308d313198a2e03707343243f6a8885a308d313198a2e0370734")));

    assertArrayEquals(DatatypeConverter
        .parseHexBinary("3243f6a8885a308d313198a2e03707343243f6a8885a308d313198a2e0370734"), new RandomStreamCipher(
        new CTRModeRandom(new AES(DatatypeConverter.parseHexBinary("2b7e151628aed2a6abf7158809cf4f3c")),
            DatatypeConverter.parseHexBinary("da39a3ee5e6b4b0d3255bfef95601890"))).codec(DatatypeConverter
        .parseHexBinary("b088b0a1a58c28a055e2656f5ee996a6c13906506eafe58d2419f42fa70a5bde")));

  }
}
