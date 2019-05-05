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
import me.abarrow.stream.ByteProcess;
import me.abarrow.stream.DynamicByteQueue;
import me.abarrow.stream.StreamRunnable;
import me.abarrow.stream.StreamUtils;

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

  public HMAC(Hasher hashMaker, byte[] key) throws CryptoException {
    this(hashMaker);
    setKey(key);
  }

  public int getHMACByteLength() {
    return hashByteLength;
  }

  public StreamRunnable tag(final boolean tagOnly) {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        innerStream(tagOnly, true, in, out);
      }
    };
  }
  
  private void innerStream(boolean tagOnly, boolean isTagging, InputStream in, OutputStream out) throws IOException {
    if (!hasKey()) {
      throw new IOException(new CryptoException(CryptoException.NO_KEY));
    }
    
    boolean append = !tagOnly && isTagging;
    boolean store = !isTagging && !tagOnly;
    DynamicByteQueue queue = null;
    byte[] messageMac = null;
    if (store) {
      queue = new DynamicByteQueue();
    }
    
    ByteProcess hashProc = hasher.hash().asByteProcess(true);
    
    
    hashProc.add(iPadKey);
    
    byte[] buffer = new byte[blockBytes];
    byte[] old = new byte[blockBytes];
    byte[] swap;
    boolean hasRead = false;
    while (true) {
      int read = in.read(buffer);
      if (read == blockBytes) {
        if (hasRead) {
          //do something with old
          hashProc.add(old);
          if (append) {
            out.write(old);
          } else if (store) {
            queue.write(old);
          }
        }
        swap = old;
        old = buffer;
        buffer = swap;
      } else if (read == -1) {
        if (hasRead) {
          if (isTagging) {
            //last complete block of plaintext
            hashProc.add(old);
            if (append) {
              out.write(old);
            }
          } else {
            //last block contains the hash
            int macStartIndex = blockBytes - hashByteLength;
            hashProc.add(old, 0, macStartIndex);
            if (store) {
              queue.write(old, 0, macStartIndex);
            }
            messageMac = Arrays.copyOfRange(old, macStartIndex, blockBytes);
          }
        } else {
          if (!isTagging) {
            throw new IOException(new CryptoException(CryptoException.NO_MAC));
          } else {
            //authenticate empty text
            //ok I guess
          }
        }
        break;
      } else {
        byte[] partialBlock = Arrays.copyOf(buffer, read);
        if (isTagging) {
          if (hasRead) {
            hashProc.add(old);
            if (append) {
              out.write(old);
            }
          }
          hashProc.add(partialBlock);
          if (append) {
            out.write(partialBlock);
          }
        } else {
          if (read >= hashByteLength) {
            //none of the old block is part of the hmac
            if (hasRead) {
              hashProc.add(old);
              if (store) {
                out.write(old);
              }
            }
            int macStartIndex = read - hashByteLength;
            //the partial block contains the hmac
            messageMac = Arrays.copyOfRange(partialBlock, macStartIndex, read);
            //the partial block might also contain some data to be hashed
            hashProc.add(partialBlock, 0, macStartIndex);
            if (store) {
              out.write(partialBlock, 0, macStartIndex);
            }
          } else {
            int macStartIndex = hashByteLength - read;
            messageMac = new byte[hashByteLength];
            //all of the partial block is part of the hmac
            System.arraycopy(partialBlock, 0, messageMac, macStartIndex, read);
            //some of the old block is part of the hmac
            int oldMacIndex = blockBytes - macStartIndex;
            System.arraycopy(old, oldMacIndex, messageMac, 0, macStartIndex);
            //but some of the last block is data to be hashed
            hashProc.add(old, 0, oldMacIndex);
            if (store) {
              out.write(old, 0, oldMacIndex);
            }
          }
        }
        CryptoUtils.fillWithZeroes(partialBlock);
        break;
      }
    }
    CryptoUtils.fillWithZeroes(buffer);
    CryptoUtils.fillWithZeroes(old);

    byte[] firstPass = hashProc.finishSync();
    hashProc = hasher.hash().asByteProcess(false).add(oPadKey).add(firstPass);
    CryptoUtils.fillWithZeroes(firstPass);
    byte[] hash = hashProc.finishSync();
    if (!isTagging) {
      if(!CryptoUtils.constantTimeArrayEquals(hash, messageMac)) {
        throw new IOException(new CryptoException(CryptoException.MAC_DOES_NOT_MATCH));
      }
    }
    if (store) {
      queue.doneWriting();
      StreamUtils.copyStream(queue.getInputStream(), out, blockBytes);
    }
    if (isTagging) {
      out.write(hash);
    }
    CryptoUtils.fillWithZeroes(hash);
  }

  @Override
  public byte[] tag(byte[] data, boolean tagOnly) throws CryptoException {
    return innerTag(data, 0, data.length, tagOnly);
  }

  private byte[] innerTag(byte[] data, int start, int length, boolean tagOnly) throws CryptoException {
    if (!hasKey()) {
      throw new CryptoException(CryptoException.NO_KEY);
    }
    try {
      byte[] firstPass = hasher.hash().asByteProcess(false).add(iPadKey).add(data, start, length).finishSync();
      ByteProcess hashProc = hasher.hash().asByteProcess(false).add(oPadKey).add(firstPass);
      CryptoUtils.fillWithZeroes(firstPass);
      if (tagOnly) {
        return hashProc.finishSync();
      } else {
        byte[] out = new byte[length + hashByteLength];
        System.arraycopy(data, start, out, 0, length);
        return hashProc.finishSync(out, length);
      }
    } catch(IOException e) {
      throw new CryptoException(e);
    }
  }
  
  @Override
  public StreamRunnable checkTag(final boolean checkTagOnly) {
    return new StreamRunnable(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        innerStream(checkTagOnly, false, in, out);
      }
    };
  }

  @Override
  public byte[] checkTag(byte[] data, boolean checkTagOnly) throws CryptoException {
    int len = data.length;
    int unTaggedLen = len - hashByteLength;
    byte[] tag = Arrays.copyOfRange(data, unTaggedLen, len);
    
    byte[] computedTag = innerTag(data, 0, unTaggedLen, true);
    
    if (CryptoUtils.constantTimeArrayEquals(computedTag, tag)) {
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
  public MAC setKey(byte[] key) throws CryptoException {
    if (hasKey()) {
      removeKey();
    }
    byte[] padded = new byte[blockBytes];

    if (key.length > blockBytes) {
      try {
        key = hasher.hash().startSync(key);
      } catch (IOException e) {
        throw new CryptoException(e);
      }
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
    return this;
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
  public MAC removeKey() {
    CryptoUtils.fillWithZeroes(iPadKey);
    CryptoUtils.fillWithZeroes(oPadKey);
    iPadKey = null;
    oPadKey = null;
    return this;
  }
}
