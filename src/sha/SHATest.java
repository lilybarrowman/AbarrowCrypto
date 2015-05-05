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
  public void SHA2_512ProperlyHashesStrings() {
    assertEquals(
        (new SHA2_512()).addString(empty).computeHashString(),
        "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
    assertEquals(
        (new SHA2_512()).addString(abc).computeHashString(),
        "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f");
    assertEquals(
        (new SHA2_512()).addString(allYourBase).computeHashString(),
        "b2f2bd8b2022df9ce4e9c21c5840f31722cf006c340d7d62ad7facb4a3adb7f0a2e54f3e372a9350d7f05779ad56d29a62215243534b794c1bdbedc8577f4ef6");
    assertEquals(
        (new SHA2_512()).addString(awfullyLongString).computeHashString(),
        "ed99ee87f6f3b4ab1311133c9cf13514e3867bc53458b5053cd0284617a1cf923a13910fdd7ba628b6c1ae3fc5d8d564c1877b98b2fc044849f08b89f3834402");
    assertEquals(
        (new SHA2_512()).addString(sixtyThreeCharacterLong).computeHashString(),
        "ebf7106c6c9bf7b8c1bfafcc9a051ebcf7809b3f1144b9f1e77f9db8b07e8ccf2ebad546c6c038469681e1bbdfe876a6068c4c89ab6d97a66ab9746b0b09592b");
    assertEquals(
        (new SHA2_512()).addString(sixtyFourCharacterLong).computeHashString(),
        "95124e0d5847e3e7c26a683da659cbfd94735a15dc7de8b02167bf377645436a5248dd40486ce52efd392739168432877f2322a5713e1e228411ae9a6d33f11c");
    assertEquals(
        (new SHA2_512()).addString(hundredAndTwentySevenCharacterLong).computeHashString(),
        "acee0d532c5a81fdfaeb4f5eaf82b8f02fb970206db6a11d4ee554e293576971050d74a1494cdacfa8a0964bc27953137de39eb6adb1b7e2c0678ce536431d11");
    assertEquals(
        (new SHA2_512()).addString(hundredAndTwentyEightCharacterLong).computeHashString(),
        "1c06767d9ea8b4709136f5340f01244f7d58932c1059eb60a55f07ada498a5f6868df090ad112138d84cad102a83ee09a1612ab47addf793f437008148ff9d2a");
  }
  
  @Test
  public void SHA3ProperlyHashesStrings() {
    assertEquals("6b4e03423667dbb73b6e15454f0eb1abd4597f9a1b078e3f5b5a6bc7", new SHA3(SHA3Mode.SHA3, 28).addString(empty).computeHashString());
    assertEquals("a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
        new SHA3(SHA3Mode.SHA3, 64).addString(empty).computeHashString());
    
    assertEquals("f71837502ba8e10837bdd8d365adb85591895602fc552b48b7390abd", new SHA3(SHA3Mode.KECCAK, 28).addString(empty).computeHashString());
    assertEquals("0eab42de4c3ceb9235fc91acffe746b29c29a8c366b7c60e4e67c466f36a4304c00fa9caf9d87976ba469bcbe06713b435f091ef2769fb160cdab33d3670680e", 
        new SHA3(SHA3Mode.KECCAK, 64).addString(empty).computeHashString());
    
    assertEquals("e642824c3f8cf24ad09234ee7d3c766fc9a3a5168d0c94ad73b46fdf", new SHA3(SHA3Mode.SHA3, 28).addString(abc).computeHashString());
    assertEquals("b751850b1a57168a5693cd924b6b096e08f621827444f70d884f5d0240d2712e10e116e9192af3c91a7ec57647e3934057340b4cf408d5a56592f8274eec53f0",
        new SHA3(SHA3Mode.SHA3, 64).addString(abc).computeHashString());
    
    
    
    assertEquals("9c8012d443e2b33a99c4abddf693bda166f6a55edd5b0c50a10bf1c6", new SHA3(SHA3Mode.SHA3, 28).addString(sixtyThreeCharacterLong).computeHashString());
    assertEquals("cc5d9440ee2dd38886cdc034a77a2c627dd3cf625fb03f6fe9bd20c5c95a9c6f266aaee0114263f06d956241efe6f7ea8b7e624fd498f337fdccdf0e397f54a9",
        new SHA3(SHA3Mode.SHA3, 64).addString(sixtyThreeCharacterLong).computeHashString());
    assertEquals("b6baf23f1b628e20ad0b145365adac772c4330c2adbd857ac34648d8", new SHA3(SHA3Mode.KECCAK, 28).addString(sixtyThreeCharacterLong).computeHashString());
    assertEquals("40c6cfc169a0ddec978abd795d16a3d6d750901a330016953117260e47576cf6b4e86e801178691eb5b266272ff91cc842d0a96ca37a887668855ec44f449a4f", 
        new SHA3(SHA3Mode.KECCAK, 64).addString(sixtyThreeCharacterLong).computeHashString());

    
    assertEquals("eebe57da1589730b546559d338bde718bc9b786c3efd7cd9652f52a8", new SHA3(SHA3Mode.SHA3, 28).addString(awfullyLongString).computeHashString());
    assertEquals("d04305846f59becbb6a7156573c6eb19d88e69814702ea6b369a09c38f444666", new SHA3(SHA3Mode.SHA3, 32).addString(awfullyLongString).computeHashString());
    assertEquals("212a106faa5e239c481a2369bf4a42fc6adb75eb7c234787b8897a49f652d8d2cf36c1b73c30f590a093cebf43afd484",
        new SHA3(SHA3Mode.SHA3, 48).addString(awfullyLongString).computeHashString());
    assertEquals("d8a97cb4770118c8ea7587a0e40294109203688536089b55e8a81163dc11913d038a35ed4e67f06822c7eb8caf70d920007180853df33d7d6f5f044b4d4a384b",
        new SHA3(SHA3Mode.SHA3, 64).addString(awfullyLongString).computeHashString());
    
    assertEquals("07fd915e459b5ebafea2fe8f547465eea4b01c05b3b28dfad1878a28", new SHA3(SHA3Mode.KECCAK, 28).addString(awfullyLongString).computeHashString());
    assertEquals("c948006454e90bf8aded4577ed6c5f4b24ee81d6f3e48c340c978cda5b4e9d00", new SHA3(SHA3Mode.KECCAK, 32).addString(awfullyLongString).computeHashString());
    assertEquals("1575aff9305ae0c96259e859f82b3a513a1aa400eba863e36fb18d29eed974e0e8fe579d4720ad1ea9efaebead2c1062",
        new SHA3(SHA3Mode.KECCAK, 48).addString(awfullyLongString).computeHashString());
    assertEquals("9ecdcd89a40bfacf76b55c96de937fc0ab6451bb28769a5fe695f3aaa39acd94ef19593cde4ed58a68eade3c8771cce17adcbf5cd8e96aca611fabdf950bb649", 
        new SHA3(SHA3Mode.KECCAK, 64).addString(awfullyLongString).computeHashString());
    
  }
}
