package core;

import static org.junit.Assert.assertArrayEquals;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import serpent.Serpent;
import blowfish.TwoFish;
import aes.AES;

public class CompoundBlockCipherTest {
  @Test
  public void testEncoding() {

    byte[] key = DatatypeConverter.parseHexBinary("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f");

    assertArrayEquals(DatatypeConverter.parseHexBinary("8ea2b7ca516745bfeafc49904b496089"), new AES(key)
        .encrypt(DatatypeConverter.parseHexBinary("00112233445566778899aabbccddeeff")));

    assertArrayEquals(DatatypeConverter.parseHexBinary("becb3ca9d8a147a664e3b909c3c7d30f"), new TwoFish(key)
        .encrypt(DatatypeConverter.parseHexBinary("8ea2b7ca516745bfeafc49904b496089")));

    assertArrayEquals(DatatypeConverter.parseHexBinary("2a1c632ae42cf9dc54056182197427dd"), new Serpent(key)
        .encrypt(DatatypeConverter.parseHexBinary("becb3ca9d8a147a664e3b909c3c7d30f")));

    assertArrayEquals(DatatypeConverter.parseHexBinary("2a1c632ae42cf9dc54056182197427dd"), new Serpent(key)
        .encrypt(DatatypeConverter.parseHexBinary("becb3ca9d8a147a664e3b909c3c7d30f")));

    assertArrayEquals(DatatypeConverter.parseHexBinary("2a1c632ae42cf9dc54056182197427dd"), new CompoundBlockCipher(
        new BlockCipher[] { new AES(key), new TwoFish(key), new Serpent(key) }).encrypt(DatatypeConverter
        .parseHexBinary("00112233445566778899aabbccddeeff")));

  }
}
