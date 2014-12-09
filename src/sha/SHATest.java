package sha;

import static org.junit.Assert.*;

import org.junit.Test;

public class SHATest {

  private final String empty = "";

  private final String abc = "abc";

  private final String allYourBase = "All your base are belong to us.";

  private final String sixtyThreeCharacterLong = "In order to accurately differentiate between cats and dogs cry.";
  
  private final String sixtyFourCharacterLong = "In order to accurately differentiate big and small measure them.";

  private final String hundredAndTwentySevenCharacterLong = "The median programmer spends too long in front of computer screens."
      + " In order to solve this problem kill all of the programmers.";
  
  private final String hundredAndTwentyEightCharacterLong = "When trying to type a long, but ultimately finite amount of text"
      + " it is necessary to keep in mind all the different ways to fill.";

  private final String awfullyLongString = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\r\n"
      + "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\r\n"
      + "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\r\n"
      + "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\r\n"
      + "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\r\n"
      + "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

  @Test
  public void SHA1ProperlyHashesStrings() {
    assertEquals((new SHA1()).addString(empty).computeHashString(), "da39a3ee5e6b4b0d3255bfef95601890afd80709");
    assertEquals((new SHA1()).addString(abc).computeHashString(), "a9993e364706816aba3e25717850c26c9cd0d89d");
    assertEquals((new SHA1()).addString(allYourBase).computeHashString(), "6e65eeb0bab294dadf2297a7aa2315703ed5b958");
    assertEquals((new SHA1()).addString(awfullyLongString).computeHashString(), "593a433f1c6881b7f42854be3f079784880373fe");
    assertEquals((new SHA1()).addString(sixtyThreeCharacterLong).computeHashString(), "b96bad635c2ceff424ca1d05070d0507f19ec653");
    assertEquals((new SHA1()).addString(sixtyFourCharacterLong).computeHashString(), "c24d1507f46466ddb6d53e7d4850dfb2250e3ebb");
    assertEquals((new SHA1()).addString(hundredAndTwentySevenCharacterLong).computeHashString(), "fecc3ddfd3282caf5a5e1d31deac78cb26999719");
    assertEquals((new SHA1()).addString(hundredAndTwentyEightCharacterLong).computeHashString(), "03a2f36b0deb3d52becacc3a2802752d505c9097");
  }

  @Test
  public void SHA512ProperlyHashesStrings() {
    assertEquals(
        (new SHA512()).addString(empty).computeHashString(),
        "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
    assertEquals(
        (new SHA512()).addString(abc).computeHashString(),
        "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f");
    assertEquals(
        (new SHA512()).addString(allYourBase).computeHashString(),
        "b2f2bd8b2022df9ce4e9c21c5840f31722cf006c340d7d62ad7facb4a3adb7f0a2e54f3e372a9350d7f05779ad56d29a62215243534b794c1bdbedc8577f4ef6");
    assertEquals(
        (new SHA512()).addString(awfullyLongString).computeHashString(),
        "ed99ee87f6f3b4ab1311133c9cf13514e3867bc53458b5053cd0284617a1cf923a13910fdd7ba628b6c1ae3fc5d8d564c1877b98b2fc044849f08b89f3834402");
    assertEquals(
        (new SHA512()).addString(sixtyThreeCharacterLong).computeHashString(),
        "ebf7106c6c9bf7b8c1bfafcc9a051ebcf7809b3f1144b9f1e77f9db8b07e8ccf2ebad546c6c038469681e1bbdfe876a6068c4c89ab6d97a66ab9746b0b09592b");
    assertEquals(
        (new SHA512()).addString(sixtyFourCharacterLong).computeHashString(),
        "95124e0d5847e3e7c26a683da659cbfd94735a15dc7de8b02167bf377645436a5248dd40486ce52efd392739168432877f2322a5713e1e228411ae9a6d33f11c");
    assertEquals(
        (new SHA512()).addString(hundredAndTwentySevenCharacterLong).computeHashString(),
        "acee0d532c5a81fdfaeb4f5eaf82b8f02fb970206db6a11d4ee554e293576971050d74a1494cdacfa8a0964bc27953137de39eb6adb1b7e2c0678ce536431d11");
    assertEquals(
        (new SHA512()).addString(hundredAndTwentyEightCharacterLong).computeHashString(),
        "1c06767d9ea8b4709136f5340f01244f7d58932c1059eb60a55f07ada498a5f6868df090ad112138d84cad102a83ee09a1612ab47addf793f437008148ff9d2a");
  }
}
