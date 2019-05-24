package me.abarrow.cipher;

import static org.junit.Assert.assertArrayEquals;

import me.abarrow.cipher.aes.AES;
import me.abarrow.cipher.blowfish.TwoFish;
import me.abarrow.cipher.serpent.Serpent;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;

import org.junit.Test;

public class CompoundBlockCipherTest {
  @Test
  public void testEncoding() throws CryptoException {

    byte[] key = CryptoUtils.parseHexString("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f");

    assertArrayEquals(CryptoUtils.parseHexString("8ea2b7ca516745bfeafc49904b496089"), new AES(key)
        .encryptBlock(CryptoUtils.parseHexString("00112233445566778899aabbccddeeff")));

    assertArrayEquals(CryptoUtils.parseHexString("becb3ca9d8a147a664e3b909c3c7d30f"), new TwoFish(key)
        .encryptBlock(CryptoUtils.parseHexString("8ea2b7ca516745bfeafc49904b496089")));

    assertArrayEquals(CryptoUtils.parseHexString("2a1c632ae42cf9dc54056182197427dd"), new Serpent(key)
        .encryptBlock(CryptoUtils.parseHexString("becb3ca9d8a147a664e3b909c3c7d30f")));

    assertArrayEquals(CryptoUtils.parseHexString("2a1c632ae42cf9dc54056182197427dd"), new Serpent(key)
        .encryptBlock(CryptoUtils.parseHexString("becb3ca9d8a147a664e3b909c3c7d30f")));

    assertArrayEquals(CryptoUtils.parseHexString("2a1c632ae42cf9dc54056182197427dd"), new CompoundBlockCipher(
        new BlockCipher[] { new AES(key), new TwoFish(key), new Serpent(key) }).encryptBlock(CryptoUtils.
        parseHexString("00112233445566778899aabbccddeeff")));

  }
}
