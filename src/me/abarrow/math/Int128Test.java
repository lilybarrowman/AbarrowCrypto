package me.abarrow.math;

import static org.junit.Assert.*;

import org.junit.Test;

public class Int128Test {
	
	@Test
	public void shiftLeftOne() {
		Int128 var = Int128.parseBigEndianHex("80000000800000000000ffff00008421");
		assertEquals(1, var.shiftLeftOne());
		assertEquals("00000001000000000001fffe00010842", var.toHexString());
		assertEquals(0, var.shiftLeftOne());
		assertEquals("00000002000000000003fffc00021084", var.toHexString());
	}

	@Test
	public void parseBigEndianHex() {
		{
			String hexString = "0388dace60b6a392f328c2b971b2fe78";
			Int128 parsed = Int128.parseBigEndianHex(hexString);
			int[] words = parsed.getWordsCopy();
			assertEquals(0x71b2fe78, words[0]);
			assertEquals(0xf328c2b9, words[1]);
			assertEquals(0x60b6a392, words[2]);
			assertEquals(0x0388dace, words[3]);
			assertEquals(hexString, parsed.toHexString());
		}
		{
			String hexString = "66e94bd4ef8a2c3b884cfa59ca342b2e";
			Int128 parsed = Int128.parseBigEndianHex(hexString);
			int[] words = parsed.getWordsCopy();
			assertEquals(0xca342b2e, words[0]);
			assertEquals(0x884cfa59, words[1]);
			assertEquals(0xef8a2c3b, words[2]);
			assertEquals(0x66e94bd4, words[3]);
			assertEquals(hexString, parsed.toHexString());
		}
	}
	
	@Test
	public void finite_times() {
		Int128 spare = new Int128();
		Int128 dest = new Int128();
		Int128 left = Int128.parseLittleBitEndianHex("0388dace60b6a392f328c2b971b2fe78");
		Int128 right = Int128.parseLittleBitEndianHex("66e94bd4ef8a2c3b884cfa59ca342b2e");
		
		String expected = Int128.parseLittleBitEndianHex("5e2ec746917062882c85b0685353deb7").toHexString();
		Int128.finite_times(left, right, dest, spare);
		assertEquals(expected, dest.toHexString());

	}

}
