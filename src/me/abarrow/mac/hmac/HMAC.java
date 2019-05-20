package me.abarrow.mac.hmac;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.hash.Hasher;
import me.abarrow.mac.MAC;
import me.abarrow.stream.ByteProcess;
import me.abarrow.stream.StreamProcess;
import me.abarrow.stream.SuffixStream;

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

  public StreamProcess tag(final boolean tagOnly) {
    return new StreamProcess() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        streamTag(tagOnly, in, out);
      }
    };
  }
  
  private void streamTag(boolean tagOnly, InputStream in, OutputStream out) throws IOException {
    byte[] computedMac = innerHMAC(tagOnly, out, in);
    out.write(computedMac);
  }
  
  private void streamCheckTag(boolean tagOnly, InputStream in, OutputStream out) throws IOException {
    if (!hasKey()) {
      throw new IOException(new CryptoException(CryptoException.NO_KEY));
    }
    SuffixStream taglessIn = new SuffixStream(in, hasher.getHashByteLength());
    if (!taglessIn.hasFullSuffix()) {
      taglessIn.close();
      throw new IOException(new CryptoException(CryptoException.NO_MAC));
    }
    
    byte[] includedMac = taglessIn.getSuffix();
    byte[] computedMac = innerHMAC(tagOnly, out, taglessIn);
    
    if(!CryptoUtils.constantTimeArrayEquals(includedMac, computedMac)) {
      throw new IOException(new CryptoException(CryptoException.MAC_DOES_NOT_MATCH));
    }
    
    CryptoUtils.fillWithZeroes(includedMac);
    CryptoUtils.fillWithZeroes(computedMac);
  }

  private byte[] innerHMAC(boolean tagOnly, OutputStream out, InputStream in) throws IOException {
    //ByteProcess hashProc = hasher.hash().createAsyncByteProcess();
    //hashProc.add(iPadKey);
    
    
    
    hasher.reset();
    hasher.hashBlock(iPadKey, 0);
    
    byte[] buffer = new byte[blockBytes];
    byte[] firstPass = null;
    while (true) {
      int read = in.read(buffer);
      if (read == -1) {
        break;
      } else if (read == blockBytes) {
        hasher.hashBlock(buffer, 0);
        if (!tagOnly) {
          out.write(buffer, 0, read);
        }
      } else {
        firstPass = hasher.computeHash(buffer, read);
        if (!tagOnly) {
          out.write(buffer, 0, read);
        }
      }
    }
    in.close();
    
    CryptoUtils.fillWithZeroes(buffer);
    
    if (firstPass == null) {
      firstPass = hasher.computeHash(buffer, 0);
    }
        
    hasher.hashBlock(oPadKey, 0);
    byte[] hash = hasher.computeHash(firstPass, hasher.getHashByteLength());
    
    CryptoUtils.fillWithZeroes(firstPass);
    return hash;
  }

  @Override
  public byte[] tag(byte[] data, boolean tagOnly) throws CryptoException {
    try {
      return tag(tagOnly).runSync(data);
    } catch (IOException e) {
      throw new CryptoException(e);
    }
  }
  
  @Override
  public StreamProcess checkTag(final boolean checkTagOnly) {
    return new StreamProcess(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        streamCheckTag(checkTagOnly, in, out);
      }
    };
  }

  @Override
  public byte[] checkTag(byte[] data, boolean checkTagOnly) throws CryptoException {
    try {
      return checkTag(checkTagOnly).runSync(data);
    } catch (IOException e) {
      throw new CryptoException(e);
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
        key = hasher.hash().runSync(key);
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
