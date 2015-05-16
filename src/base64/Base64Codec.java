package base64;

import java.util.HashMap;
import java.util.Map;

public class Base64Codec {

  private static Base64Codec standard;
  private static Base64Codec openBSD;

  private final char padChar;
  private final char[] chars;
  private Map<Character, Integer> charMap;

  public Base64Codec(char[] mapping, char padding) {
    padChar = padding;
    chars = mapping;
    charMap = new HashMap<Character, Integer>(64);
    for (int i = 0; i < 64; i++) {
      charMap.put(chars[i], i);
    }
  }

  public static Base64Codec getStandardBase64Codec() {
    if (standard == null) {
      standard = new Base64Codec("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray(), '=');
    }
    return standard;
  }

  public static Base64Codec getOpenBSDBase64Codec() {
    if (openBSD == null) {
      openBSD = new Base64Codec("./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray(), '=');
    }
    return openBSD;
  }
  
  public String encode(byte[] bytes) {
    return encode(bytes, -1);
  }

  public String encode(byte[] bytes, int length) {
    StringBuilder str = new StringBuilder();

    int n = 0;
    int a;
    int b;
    int c;
    
    if (length == -1) {
      length = bytes.length;
    }

    
    while(true) {
      if (n >= length) {
        break;
      }
      
      a = bytes[n];
      n++;
      
      //first 6 digits of a
      int one = (a >> 2) & 0x3f;
      
      str.append(chars[one]);
      
      // last 2 digits of a
      int two = (a & 0x3) << 4;
      
      if (n >= length) {
        str.append(chars[two]);
        str.append(padChar);
        str.append(padChar);
        break;
      }
      
      b = bytes[n];
      n++;
      
      //first 4 digits of b
      two |= (b >> 4) & 0xf;
      
      
      str.append(chars[two]);
      
      //last 4 digits of b
      int three = (b & 0xf) << 2;
      
      if (n >= length) {
        str.append(chars[three]);
        str.append(padChar);
        break;
      }
      
      c = bytes[n];
      n++;
      
      //first 2 digits of c
      three |= (c >> 6) & 0x3;
      
      str.append(chars[three]);
      
      // last 6 digits of c
      int four = c & 0x3f;
      
      str.append(chars[four]); 
    }

    return str.toString();
  }

  public byte[] decode(String string) {
    char[] strChars = string.toCharArray();

    int bytesFewer = 0;
    if (strChars[strChars.length - 2] == padChar) {
      bytesFewer = 2;
    } else if (strChars[strChars.length - 1] == padChar) {
      bytesFewer = 1;
    }

    int numBytes = (int) ((strChars.length) / 4.0 * 3.0 - bytesFewer);
    byte[] bytes = new byte[numBytes];

    int a;
    int b;
    int c;
    int d;
    int i;
    for (i = 0; (i + 2) < bytes.length; i += 3) {
      int j = i / 3 * 4;
      a = charMap.get(strChars[j]);
      b = charMap.get(strChars[j + 1]);
      c = charMap.get(strChars[j + 2]);
      d = charMap.get(strChars[j + 3]);

      // all digits of a and the first 2 digits of b
      bytes[i] = (byte) ((a << 2) + (b >>> 4));
      // last 4 digits of b and the first 4 digits of c
      bytes[i + 1] = (byte) (((b & 0xf) << 4) + (c >>> 2));
      // last 2 digits of c and all the digits of d
      bytes[i + 2] = (byte) (((c & 0x3) << 6) + d);
    }

    if (bytesFewer == 0) {
      bytesFewer = bytes.length - i;

      // this is an odd case that results from a truncated base64 encoded string

      if (i < bytes.length) {
        int j = i / 3 * 4;
        a = charMap.get(strChars[j]);
        b = ((j + 1) < strChars.length) ? charMap.get(strChars[j + 1]) : 0;
        c = ((j + 2) < strChars.length) ? charMap.get(strChars[j + 2]) : 0;
        // all digits of a and the first 2 digits of b
        bytes[i] = (byte) ((a << 2) + (b >>> 4));

        if ((i + 1) < bytes.length) {
          // last 4 digits of b and the first 4 digits of c
          bytes[i + 1] = (byte) (((b & 0xf) << 4) + (c >>> 2));
        }
      }
    } else if (bytesFewer == 1) {
      a = charMap.get(strChars[strChars.length - 4]);
      b = charMap.get(strChars[strChars.length - 3]);
      c = charMap.get(strChars[strChars.length - 2]);
      // all digits of a and the first 2 digits of b
      bytes[bytes.length - 2] = (byte) ((a << 2) + (b >>> 4));
      // last 4 digits of b and the first 4 digits of c
      bytes[bytes.length - 1] = (byte) (((b & 0xf) << 4) + (c >>> 2));
    } else if (bytesFewer == 2) {
      a = charMap.get(strChars[strChars.length - 4]);
      b = charMap.get(strChars[strChars.length - 3]);
      // all digits of a and the first 2 digits of b
      bytes[bytes.length - 1] = (byte) ((a << 2) + (b >>> 4));
    }

    return bytes;
  }

}
