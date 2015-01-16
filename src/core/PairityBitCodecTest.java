package core;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class PairityBitCodecTest {
  @Test
  public void testPairityBitCodec() {
    byte[] oddPairity = new byte[] {
        // a 1000 0001
        (byte) 0x81,
        // b 0111 0011
        (byte) 0x73,
        // c 0110 1101
        (byte) 0x6D,
        // d 1010 0011
        (byte) 0xa3,
        // e 1111 1111
        (byte) 0xff,
        // f 0001 0001
        (byte) 0x11,
        // g 0110 0001
        (byte) 0x61,
        // h 0000 1001
        (byte) 0x09 };
    
    byte[] evenPairity = new byte[] {
        // a 1000 0000
        (byte) 0x80,
        // b 0111 0010
        (byte) 0x72,
        // c 0110 1100
        (byte) 0x6c,
        // d 1010 0010
        (byte) 0xa2,
        // e 1111 1110
        (byte) 0xfe,
        // f 0001 0000
        (byte) 0x10,
        // g 0110 0000
        (byte) 0x60,
        // h 0000 1000
        (byte) 0x08 };
        
    byte[] expectedOut = new byte[]{
    // 1000 000P0  -> 10000000
    (byte)0x80,
    // 1110 01P01  -> 11100101
    (byte)0xe5,
    // 1011 0P101  -> 10110101
    (byte)0xb5,
    // 0001 P1111  -> 00011111 
    (byte)0x1f,
    // 111P0 0010  -> 11100010
    (byte)0xe2,
    // 00P01 1000  -> 00011000
    (byte)0x18,
    // 0P000 0000P -> 00000100
    (byte)0x04};
    
    assertArrayEquals(PairityBitCodec.decode(oddPairity, PairityBitType.ODD), expectedOut);
    assertArrayEquals(PairityBitCodec.decode(evenPairity, PairityBitType.EVEN), expectedOut);
    
    assertArrayEquals(PairityBitCodec.encode(expectedOut, PairityBitType.ODD), oddPairity);
    assertArrayEquals(PairityBitCodec.encode(expectedOut, PairityBitType.EVEN), evenPairity);
    
    assertArrayEquals(PairityBitCodec.encode(oddPairity, PairityBitType.NONE), oddPairity);
    assertArrayEquals(PairityBitCodec.decode(oddPairity, PairityBitType.NONE), oddPairity);
  }
}
