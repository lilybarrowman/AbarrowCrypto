package me.abarrow.cipher.mode;

import static org.junit.Assert.*;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import me.abarrow.core.CryptoUtils;
import me.abarrow.math.Int128;

public class GCMModeTest {

  @Test
  public void testGhash() {
    Int128 H = Int128.parseLittleBitEndianHex("b83b533708bf535d0aa6e52980d53b78");
    byte[] A = CryptoUtils.littleBitEndianToLittleEndian(DatatypeConverter.parseHexBinary("feedfacedeadbeeffeedfacedeadbeefabaddad2"));
    byte[] C = CryptoUtils.littleBitEndianToLittleEndian(DatatypeConverter.parseHexBinary("42831ec2217774244b7221b784d0d49ce3aa212f2c02a4e035c17e2329aca12e21d514b25466931c7d8f6a5aac84aa051ba30b396a0aac973d58e091"));
    Int128 expectedHash = Int128.parseLittleBitEndianHex("698e57f70e6ecc7fd9463b7260a9ae5f");
    Int128 hash = new Int128();
    GCMMode.ghash(H, A, C, hash);
    assertArrayEquals(expectedHash.getWordsCopy(), hash.getWordsCopy());
  }

}
