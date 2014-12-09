package core;

import static org.junit.Assert.*;

import org.junit.Test;

public class CryptoUtilsTest {

  @Test
  public void testIntFromBytes() {
    byte[] bytes = new byte[4];
    for (int n = 0; n < 10240; n++) {
      CryptoUtils.intToBytes(n, bytes, 0);
      int decoded = CryptoUtils.intFromBytes(bytes, 0);
      assertEquals(n, decoded);
    }
  }

  @Test
  public void testPermuteByteArray() {
    byte[] input = new byte[] { 56, 67, -34, 88 };
    int[] permutation = new int[] { 3, 1, 2, 0 };
    byte[] output = CryptoUtils.permuteByteArray(input, new byte[4], permutation);

    assertArrayEquals(output, new byte[] { 88, 67, -34, 56 });

    assertArrayEquals(CryptoUtils.permuteByteArray(output, new byte[4], permutation), input);
  }

  @Test
  public void testCopyBitsFromByteArray() {

    assertArrayEquals(CryptoUtils.copyBitsFromByteArray(new byte[] {
        // 0011 1111
        0x3f,
        // 0010 00010
        0x22 }, 1, 15, new byte[2], 0), new byte[] {
        // 0111 1110
        0x7e,
        // 0100 0100
        0x44 });

    byte[] example = "Example strings are horrible".getBytes();
    assertArrayEquals(CryptoUtils.copyBitsFromByteArray(example, 0, example.length * 8, new byte[example.length], 0),
        example);

    byte[] splitInHalf = new byte[] { (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36,
        (byte) 0x37 };
    assertArrayEquals(CryptoUtils.copyBitsFromByteArray(splitInHalf, 0, 28, new byte[4], 0), new byte[] { (byte) 0x31,
        (byte) 0x32, (byte) 0x33, (byte) 0x30 });
    assertArrayEquals(CryptoUtils.copyBitsFromByteArray(splitInHalf, 28, 28, new byte[4], 0), new byte[] { (byte) 0x43,
        (byte) 0x53, (byte) 0x63, (byte) 0x70 });

  }
  
  @Test
  public void rotateByteArray() {
    byte[] example = new byte[]{
        //0110 1111
        (byte)0x6f,
        //1100 1001
        (byte)0xc9
    };
    assertArrayEquals(CryptoUtils.rotateByteArray(example, 16, 4), new byte[]{
      //1001 0110
      (byte)0x96,
      //1111 0110
      (byte)0xfc
    });
    
    assertArrayEquals(CryptoUtils.rotateByteArray(example, 16, -4), new byte[]{
      //1111 1100
      (byte)0xfc,
      //1001 0110
      (byte)0x96
    });
    
    //in this case we're only rotating part of the array
    assertArrayEquals(CryptoUtils.rotateByteArray(example, 12, -4), new byte[]{
      //1111 1100
      (byte)0xfc,
      //0110 0000
      (byte)0x60
    });
  }

}
