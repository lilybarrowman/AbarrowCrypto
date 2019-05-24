package me.abarrow.padding;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import me.abarrow.cipher.aes.AES;
import me.abarrow.cipher.mode.CBCMode;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;

import org.junit.Test;

public class PKCS7Test {
  @Test
  public void testPadding() throws IOException, InterruptedException, CryptoException {
    testPad(7, new byte[] { 67, 46, 12, 13, 54, -89 }, new byte[] { 67, 46, 12, 13, 54, -89, 1 });
    testPad(5, new byte[] { 67, 46, 12, 13, 54 }, new byte[] { 67, 46, 12, 13, 54, 5, 5, 5, 5, 5 });
    testPad(8, new byte[] { 67, 46, 12, 13, 54 }, new byte[] { 67, 46, 12, 13, 54, 3, 3, 3 });
  }

  public void testPad(int blockSize, byte[] input, byte[] expected) throws InterruptedException, IOException,
      CryptoException {
    PKCS7 padding = new PKCS7(blockSize);
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    padding.pad().runSync(new ByteArrayInputStream(input), o);
    assertArrayEquals(expected, o.toByteArray());
    assertArrayEquals(expected, padding.pad(input));
  }

  public void testUnpad(int blockSize, byte[] input, byte[] expected) throws InterruptedException, IOException {
    PKCS7 padding = new PKCS7(blockSize);
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    padding.unpad().runSync(new ByteArrayInputStream(input), o);
    assertArrayEquals(expected, o.toByteArray());
    assertArrayEquals(expected, padding.unpad(input));
  }

  @Test
  public void testUnpadding() throws IOException, InterruptedException {
    testUnpad(7, new byte[] { 67, 46, 12, 13, 54, -89, 1 }, new byte[] { 67, 46, 12, 13, 54, -89 });
    testUnpad(5, new byte[] { 67, 46, 12, 13, 54, 5, 5, 5, 5, 5 }, new byte[] { 67, 46, 12, 13, 54 });
    testUnpad(8, new byte[] { 67, 46, 12, 13, 54, 3, 3, 3 }, new byte[] { 67, 46, 12, 13, 54 });
  }

  @Test
  public void cbcTest() throws CryptoException, IOException {
    assertArrayEquals(
        CryptoUtils.parseHexString("6232d0a50c4e00a30cfb161bcc3a4dd84079a729f94dde6429887d8ba50752c906cb7d66533a3344438d921755681a12"),
        new CBCMode(new AES(CryptoUtils.parseHexString("123afc45778932543cdabb432645123afc457789b2543cdabb432645b4326408")),
            new PKCS7(), CryptoUtils.parseHexString("8051f5bb13d68fcb5f8e25dd890228ac")).setIVPrepending(false)
            .encrypt().runSync(CryptoUtils.parseHexString("48656c6c6f20686f772061726520796f752c2061726520796f752061207261626269743f")));
    assertArrayEquals(
        CryptoUtils.parseHexString("48656c6c6f20686f772061726520796f752c2061726520796f752061207261626269743f"),
        new CBCMode(new AES(CryptoUtils.parseHexString("123afc45778932543cdabb432645123afc457789b2543cdabb432645b4326408")),
            new PKCS7(), CryptoUtils.parseHexString("8051f5bb13d68fcb5f8e25dd890228ac")).setIVPrepending(false)
            .decrypt().runSync(CryptoUtils.parseHexString("6232d0a50c4e00a30cfb161bcc3a4dd84079a729f94dde6429887d8ba50752c906cb7d66533a3344438d921755681a12")));
  }

}
