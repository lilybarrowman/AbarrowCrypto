package me.abarrow.cipher.mode;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

import me.abarrow.cipher.aes.AES;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;

import org.junit.Test;

public class CTRModeTest {
  @Test
  public void testEncoding() throws CryptoException, IOException {

    // partial block
    assertArrayEquals(CryptoUtils.parseHexString("b088b0a1a58c28a0"), new CTRMode(new AES(
        CryptoUtils.parseHexString("2b7e151628aed2a6abf7158809cf4f3c")), CryptoUtils.parseHexString("da39a3ee5e6b4b0d3255bfef95601890"))
        .setIVPrepending(false).encrypt().runSync(CryptoUtils.parseHexString("3243f6a8885a308d")));

    // single block
    assertArrayEquals(CryptoUtils.parseHexString("b088b0a1a58c28a055e2656f5ee996a6"), new CTRMode(new AES(
        CryptoUtils.parseHexString("2b7e151628aed2a6abf7158809cf4f3c")), CryptoUtils.parseHexString("da39a3ee5e6b4b0d3255bfef95601890"))
        .setIVPrepending(false).encrypt().runSync(CryptoUtils.parseHexString("3243f6a8885a308d313198a2e0370734")));

    assertArrayEquals(CryptoUtils.parseHexString("3243f6a8885a308d313198a2e0370734"), new CTRMode(new AES(
        CryptoUtils.parseHexString("2b7e151628aed2a6abf7158809cf4f3c")), CryptoUtils.parseHexString("da39a3ee5e6b4b0d3255bfef95601890"))
        .setIVPrepending(false).decrypt().runSync(CryptoUtils.parseHexString("b088b0a1a58c28a055e2656f5ee996a6")));

    // iv prepending
    assertArrayEquals(CryptoUtils.parseHexString("da39a3ee5e6b4b0d3255bfef95601890b088b0a1a58c28a055e2656f5ee996a6"),
        new CTRMode(new AES(CryptoUtils.parseHexString("2b7e151628aed2a6abf7158809cf4f3c")),
            CryptoUtils.parseHexString("da39a3ee5e6b4b0d3255bfef95601890"))
            .encrypt().runSync(CryptoUtils.parseHexString("3243f6a8885a308d313198a2e0370734")));

    assertArrayEquals(CryptoUtils.parseHexString("3243f6a8885a308d313198a2e0370734"), new CTRMode(new AES(
        CryptoUtils.parseHexString("2b7e151628aed2a6abf7158809cf4f3c")), CryptoUtils.parseHexString("2b7e151628aed2a6abf7158809cf4f3c"))
        .decrypt().runSync(CryptoUtils.parseHexString("da39a3ee5e6b4b0d3255bfef95601890b088b0a1a58c28a055e2656f5ee996a6")));

    // repeated block
    assertArrayEquals(CryptoUtils.parseHexString("b088b0a1a58c28a055e2656f5ee996a6c13906506eafe58d2419f42fa70a5bde"),
        new CTRMode(new AES(CryptoUtils.parseHexString("2b7e151628aed2a6abf7158809cf4f3c")),
            CryptoUtils.parseHexString("da39a3ee5e6b4b0d3255bfef95601890")).setIVPrepending(false).encrypt().runSync(
            CryptoUtils.parseHexString("3243f6a8885a308d313198a2e03707343243f6a8885a308d313198a2e0370734")));

    assertArrayEquals(CryptoUtils.parseHexString("3243f6a8885a308d313198a2e03707343243f6a8885a308d313198a2e0370734"),
        new CTRMode(new AES(CryptoUtils.parseHexString("2b7e151628aed2a6abf7158809cf4f3c")),
            CryptoUtils.parseHexString("da39a3ee5e6b4b0d3255bfef95601890")).setIVPrepending(false).decrypt().runSync(
            CryptoUtils.parseHexString("b088b0a1a58c28a055e2656f5ee996a6c13906506eafe58d2419f42fa70a5bde")));

  }
}
