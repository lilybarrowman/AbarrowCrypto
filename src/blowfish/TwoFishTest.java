package blowfish;

import static org.junit.Assert.assertArrayEquals;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

public class TwoFishTest {

  @Test
  public void testKeyExpansion() {

    assertArrayEquals(new int[] { 0x52c54dde, 0x11f0626d, 0x7cac9d4a, 0x4d1b4aaa, 0xb7b83a10, 0x1e7d0beb, 0xee9c341f,
        0xcfe14be4, 0xf98ffef9, 0x9c5b3c17, 0x15a48310, 0x342a4d81, 0x424d89fe, 0xc14724a7, 0x311b834c, 0xfde87320,
        0x3302778f, 0x26cd67b4, 0x7a6c6362, 0xc2baf60e, 0x3411b994, 0xd972c87f, 0x84adb1ea, 0xa7dee434, 0x54d2960f,
        0xa2f7caa8, 0xa6b8ff8c, 0x8014c425, 0x6a748d1c, 0xedbaf720, 0x928ef78c, 0x0338ee13, 0x9949d6be, 0xc8314176,
        0x07c07d68, 0xecae7ea7, 0x1fe71844, 0x85c05c89, 0xf298311e, 0x696ea672 }, TwoFish
        .expandRoundKeys(DatatypeConverter.parseHexBinary("00000000000000000000000000000000")));

    assertArrayEquals(new int[] { 0x38394a24, 0xc36d1175, 0xe802528f, 0x219bfeb4, 0xb9141ab4, 0xbd3e70cd, 0xaf609383,
        0xfd36908a, 0x03efb931, 0x1d2ee7ec, 0xa7489d55, 0x6e44b6e8, 0x714ad667, 0x653ad51f, 0xb6315b66, 0xb27c05af,
        0xa06c8140, 0x9853d419, 0x4016e346, 0x8d1c0dd4, 0xf05480be, 0xb6af816f, 0x2d7dc789, 0x45b7bd3a, 0x57f8a163,
        0x2befda69, 0x26ae7271, 0xc2900d79, 0xed323794, 0x3d3ffd80, 0x5de68e49, 0x9c3d2478, 0xdf326fe3, 0x5911f70d,
        0xc229f13b, 0xb1364772, 0x4235364d, 0x0cec363a, 0x57c8dd1f, 0x6a1ad61e }, TwoFish
        .expandRoundKeys(DatatypeConverter.parseHexBinary("0123456789ABCDEFFEDCBA98765432100011223344556677")));

    assertArrayEquals(new int[] { 0x5ec769bf, 0x44d13c60, 0x76cd39b1, 0x16750474, 0x349c294b, 0xec21f6d6, 0x4fbd10b4,
        0x578da0ed, 0xc3479695, 0x9b6958fb, 0x6a7fbc4e, 0x0bf1830b, 0x61b5e0fb, 0xd78d9730, 0x7c6cf0c4, 0x2f9109c8,
        0xe69ea8d1, 0xed99bdff, 0x35dc0bbd, 0xa03e5018, 0xfb18ea0b, 0x38bd43d3, 0x76191781, 0x37a9a0d3, 0x72427bea,
        0x911cc0b8, 0xf1689449, 0x71009ca9, 0xb6363e89, 0x494d9855, 0x590bbc63, 0xf95a28b5, 0xfb72b4e1, 0x2a43505c,
        0xbfd34176, 0x5c133d12, 0x3a9247f7, 0x9a3331dd, 0xee7515e6, 0xf0d54dcd }, TwoFish
        .expandRoundKeys(DatatypeConverter
            .parseHexBinary("0123456789ABCDEFFEDCBA987654321000112233445566778899AABBCCDDEEFF")));
  }

  @Test
  public void testSBoxCreation() {

    assertArrayEquals(new int[] { 0, 0 }, TwoFish.createSBoxes(DatatypeConverter
        .parseHexBinary("00000000000000000000000000000000")));
    
    assertArrayEquals(new int[] { 0x45661061, 0xb255bc4b, 0xb89ff6f2 }, TwoFish
        .createSBoxes(DatatypeConverter
            .parseHexBinary("0123456789ABCDEFFEDCBA98765432100011223344556677")));

    assertArrayEquals(new int[] { 0x8e4447f7, 0x45661061, 0xb255bc4b, 0xb89ff6f2 }, TwoFish
        .createSBoxes(DatatypeConverter
            .parseHexBinary("0123456789ABCDEFFEDCBA987654321000112233445566778899AABBCCDDEEFF")));
  }

  @Test
  public void testEncryption() {
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("9F589F5CF6122C32B6BFEC2F2AE8C35A"), new TwoFish(
        DatatypeConverter.parseHexBinary("00000000000000000000000000000000"))
        .encrypt(DatatypeConverter.parseHexBinary("00000000000000000000000000000000")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("CFD1D2E5A9BE9CDF501F13B892BD2248"), new TwoFish(
        DatatypeConverter.parseHexBinary("0123456789ABCDEFFEDCBA98765432100011223344556677"))
        .encrypt(DatatypeConverter.parseHexBinary("00000000000000000000000000000000")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("37527BE0052334B89F0CFCCAE87CFA20"), new TwoFish(
        DatatypeConverter.parseHexBinary("0123456789ABCDEFFEDCBA987654321000112233445566778899AABBCCDDEEFF"))
        .encrypt(DatatypeConverter.parseHexBinary("00000000000000000000000000000000")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("6CB4561C40BF0A9705931CB6D408E7FA"), new TwoFish(
        DatatypeConverter.parseHexBinary("D43BB7556EA32E46F2A282B7D45B4E0D57FF739D4DC92C1BD7FC01700CC8216F"))
        .encrypt(DatatypeConverter.parseHexBinary("90AFE91BB288544F2C32DC239B2635E6")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("E69465770505D7F80EF68CA38AB3A3D6"), new TwoFish(
        DatatypeConverter.parseHexBinary("6CB4561C40BF0A9705931CB6D408E7FA90AFE91BB288544F2C32DC239B2635E6"))
        .encrypt(DatatypeConverter.parseHexBinary("3059D6D61753B958D92F4781C8640E58")));
  }
  
  @Test
  public void testDecryption() {
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("00000000000000000000000000000000"), new TwoFish(
        DatatypeConverter.parseHexBinary("00000000000000000000000000000000"))
        .decrypt(DatatypeConverter.parseHexBinary("9F589F5CF6122C32B6BFEC2F2AE8C35A")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("00000000000000000000000000000000"), new TwoFish(
        DatatypeConverter.parseHexBinary("0123456789ABCDEFFEDCBA98765432100011223344556677"))
        .decrypt(DatatypeConverter.parseHexBinary("CFD1D2E5A9BE9CDF501F13B892BD2248")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("00000000000000000000000000000000"), new TwoFish(
        DatatypeConverter.parseHexBinary("0123456789ABCDEFFEDCBA987654321000112233445566778899AABBCCDDEEFF"))
        .decrypt(DatatypeConverter.parseHexBinary("37527BE0052334B89F0CFCCAE87CFA20")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("90AFE91BB288544F2C32DC239B2635E6"), new TwoFish(
        DatatypeConverter.parseHexBinary("D43BB7556EA32E46F2A282B7D45B4E0D57FF739D4DC92C1BD7FC01700CC8216F"))
        .decrypt(DatatypeConverter.parseHexBinary("6CB4561C40BF0A9705931CB6D408E7FA")));
    
    assertArrayEquals(DatatypeConverter.parseHexBinary("3059D6D61753B958D92F4781C8640E58"), new TwoFish(
        DatatypeConverter.parseHexBinary("6CB4561C40BF0A9705931CB6D408E7FA90AFE91BB288544F2C32DC239B2635E6"))
        .decrypt(DatatypeConverter.parseHexBinary("E69465770505D7F80EF68CA38AB3A3D6")));
  }

}
