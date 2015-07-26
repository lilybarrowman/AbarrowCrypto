package me.abarrow.mac.hmac;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.hash.Hasher;
import me.abarrow.mac.MAC;
import me.abarrow.stream.StreamRunnable;

public class HMAC implements MAC {

  private static final byte O_PAD_BYTE = 0x5c;
  private static final byte I_PAD_BYTE = 0x36;

  private int blockBytes;
  private int hashByteLength;

  private Hasher hasher;

  private byte[] iPadKey;
  private byte[] oPadKey;

  public HMAC(Hasher hashMaker) {
    hasher = hashMaker;
    blockBytes = hasher.getBlockBytes();
    hashByteLength = hasher.getHashByteLength();
  }

  public HMAC(Hasher hashMaker, byte[] key) {
    this(hashMaker);
    setKey(key);
  }

  public byte[] computeHash(byte[] key, byte[] message) {
    return computeHash(key, message, new byte[hashByteLength], 0);
  }

  public byte[] computeHash(byte[] key, byte[] message, byte[] out, int start) {
    byte[] padded = new byte[blockBytes];
    byte[] padKey = new byte[blockBytes];
    byte[] padKeyHash = new byte[hashByteLength];

    if (key.length > blockBytes) {
      key = hasher.addBytes(key).computeHash();
    }

    if (key.length < blockBytes) {
      // right pad with zereos
      System.arraycopy(key, 0, padded, 0, key.length);
      key = padded;
    }

    Arrays.fill(padKey, I_PAD_BYTE);
    CryptoUtils.xorByteArrays(padKey, key, padKey);

    hasher.addBytes(padKey).addBytes(message).computeHash(padKeyHash, 0);

    Arrays.fill(padKey, O_PAD_BYTE);
    CryptoUtils.xorByteArrays(padKey, key, padKey);

    hasher.addBytes(padKey).addBytes(padKeyHash).computeHash(out, start);

    CryptoUtils.fillWithZeroes(padded);
    CryptoUtils.fillWithZeroes(padKey);
    return out;
  }

  public boolean checkHash(byte[] key, byte[] message, byte[] hmac) {
    return CryptoUtils.arrayEquals(computeHash(key, message), hmac);
  }

  public String computeHashString(byte[] key, byte[] message) {
    return CryptoUtils.byteArrayToHexString(computeHash(key, message));
  }

  public String computeHashString(String key, String message) {
    return CryptoUtils.byteArrayToHexString(computeHash(key.getBytes(), message.getBytes()));
  }

  public int getHMACByteLength() {
    return hashByteLength;
  }

  public StreamRunnable tag(final boolean tagOnly) {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (!hasKey()) {
          throw new IOException(new CryptoException(CryptoException.NO_KEY));
        }
        boolean append = !tagOnly;
        hasher.addBytes(iPadKey);
        byte[] buffer = new byte[blockBytes];
        while (true) {
          int read = in.read(buffer);
          if (read == blockBytes) {
            hasher.addBytes(buffer);
            if (append) {
              out.write(buffer);
            }
          } else if (read == -1) {
            break;
          } else {
            byte[] partialBlock = Arrays.copyOf(buffer, read);
            hasher.addBytes(partialBlock);
            if (append) {
              out.write(partialBlock);
            }
            CryptoUtils.fillWithZeroes(partialBlock);
          }
        }
        CryptoUtils.fillWithZeroes(buffer);
        byte[] firstPass = hasher.computeHash();
        hasher.addBytes(oPadKey).addBytes(firstPass);
        CryptoUtils.fillWithZeroes(firstPass);
        out.write(hasher.computeHash());
      }
    };
  }

  @Override
  public byte[] tag(byte[] data, boolean tagOnly) throws CryptoException {
    return innerTag(data, 0, data.length, tagOnly);
  }

  private byte[] innerTag(byte[] data, int start, int length, boolean tagOnly) throws CryptoException {
    if (!hasKey()) {
      throw new CryptoException(CryptoException.NO_KEY);
    }
    byte[] firstPass = hasher.addBytes(iPadKey).addBytes(data, start, length).computeHash();
    hasher.addBytes(oPadKey).addBytes(firstPass);
    CryptoUtils.fillWithZeroes(firstPass);
    if (tagOnly) {
      return hasher.computeHash();
    } else {
      byte[] out = new byte[length + hashByteLength];
      hasher.addBytes(data, start, length);
      return hasher.computeHash(out, length);
    }
  }
  
  @Override
  public StreamRunnable checkTag(boolean checkTagOnly) {
    return null;/*
    return new StreamRunnable(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (!hasKey()) {
          throw new IOException(new CryptoException(CryptoException.NO_KEY));
        }
        hasher.addBytes(iPadKey);
        byte[] buffer = new byte[hashByteLength];
        byte[] oldBuffer = new byte[hashByteLength];
        byte[] swap;
        byte[] tag;
        boolean hasRead = true;
        while (true) {
          int read = in.read(buffer);
          if (read == hashByteLength) {
            if (hasRead) {
              hasher.addBytes(oldBuffer);
            }
            swap = oldBuffer;
            oldBuffer = buffer;
            buffer = swap;
            hasRead = true;
          } else if (read == -1) {
            if (!hasRead) {
              throw new IOException(new CryptoException(CryptoException.NO_MAC));
            }
            tag = oldBuffer;
            break;
          } else {
            if (!hasRead) {
              throw new IOException(new CryptoException(CryptoException.NO_MAC));
            }            
            int oldMacStart = hashByteLength - read;
            tag = Arrays.copyOfRange(oldBuffer, oldMacStart, hashByteLength + oldMacStart);
            System.arraycopy(buffer, 0, tag, oldMacStart, read);
            hasher.addBytes(oldBuffer, 0, oldMacStart);
            break;
          }
        }
        byte[] firstPass = hasher.computeHash();
        byte[] computedTag = hasher.addBytes(oPadKey).addBytes(firstPass).computeHash();
       
        
        CryptoUtils.fillWithZeroes(buffer);
        
        CryptoUtils.fillWithZeroes(firstPass);
        out.write(hasher);
      }
    };*/
  }

  @Override
  public byte[] checkTag(byte[] data, boolean checkTagOnly) throws CryptoException {
    int len = data.length;
    int unTaggedLen = len - hashByteLength;
    byte[] tag = Arrays.copyOfRange(data, unTaggedLen, len);
    
    byte[] computedTag = innerTag(data, 0, unTaggedLen, true);
    
    if (CryptoUtils.arrayEquals(computedTag, tag)) {
      if (checkTagOnly) {
        return new byte[0];
      } else {
        return Arrays.copyOf(data, unTaggedLen);
      }
    } else {
      throw new CryptoException(CryptoException.MAC_DOES_NOT_MATCH);
    }
  }

  @Override
  public void setKey(byte[] key) {
    byte[] padded = new byte[blockBytes];

    if (key.length > blockBytes) {
      key = hasher.addBytes(key).computeHash();
    }

    if (key.length < blockBytes) {
      // right pad with zereos
      System.arraycopy(key, 0, padded, 0, key.length);
      key = padded;
    }

    iPadKey = new byte[blockBytes];
    Arrays.fill(iPadKey, I_PAD_BYTE);
    CryptoUtils.xorByteArrays(iPadKey, key, iPadKey);

    oPadKey = new byte[blockBytes];
    Arrays.fill(oPadKey, O_PAD_BYTE);
    CryptoUtils.xorByteArrays(oPadKey, key, oPadKey);
  }

  @Override
  public int getTagLength() {
    return hashByteLength;
  }

  @Override
  public boolean hasKey() {
    return iPadKey != null;
  }

  @Override
  public void removeKey() {
    CryptoUtils.fillWithZeroes(iPadKey);
    CryptoUtils.fillWithZeroes(oPadKey);
    iPadKey = null;
    oPadKey = null;
  }
}
