package me.abarrow.cipher.mode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import me.abarrow.cipher.BlockCipher;
import me.abarrow.cipher.Cipher;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.counter.BigIntCounter;
import me.abarrow.counter.Counter;
import me.abarrow.stream.StreamRunnable;

public class CTRMode implements Cipher {
  
  private boolean prpendingIV;
  private byte[] iv;
  private BlockCipher core;
  private int blockSize;
  private Counter counter;
  
  public CTRMode(BlockCipher cipherCore) {
    this(cipherCore, new BigIntCounter());
  }
  
  public CTRMode(BlockCipher cipherCore, byte[] initVec) {
    this(cipherCore, new BigIntCounter(), initVec);
  }
  
  public CTRMode(BlockCipher cipherCore, Counter c, byte[] initVec) {
    this(cipherCore, c);
    setIV(initVec);
  }
  
  public CTRMode(BlockCipher cipherCore, Counter c) {
    core = cipherCore;
    blockSize = core.getBlockBytes();
    counter = c;
    prpendingIV = true;
  }

  @Override
  public byte[] encrypt(byte[] input) throws CryptoException {
    return encryptOrDecrypt(input, true);
  }

  private byte[] encryptOrDecrypt(byte[] input, boolean encrypting) throws CryptoException {
    if (!hasIV()) {
      throw new CryptoException(CryptoException.NO_IV);
    }
    counter.reset();
    int inputLen = input.length;
    byte[] output;
    int outputOffset;
    int inputOffset;
    if (prpendingIV) {
      if (encrypting) {
        inputOffset = 0;
        outputOffset = blockSize;
        output = new byte[inputLen + blockSize];
        System.arraycopy(iv, 0, output, 0, blockSize);
      } else {
        outputOffset = 0;
        inputLen -= blockSize;
        inputOffset = blockSize;
        output = new byte[inputLen];
        setIV(input);
      }
    } else {
      outputOffset = 0;
      output = new byte[inputLen];
      inputOffset = 0;
    }
    byte[] counterVal = new byte[blockSize];
    byte[] block = new byte[blockSize];
    int n;
    int end = inputLen / blockSize * blockSize;
    for (n = 0; n < end; n+= blockSize) {
      //easy enough to make parallel later
      CryptoUtils.fillLastBytes(counter.increment(), counterVal, blockSize);
      CryptoUtils.xorByteArrays(counterVal, iv, block);
      core.encryptBlock(block, 0, output, outputOffset + n);
    }
    if (n != inputLen) {
      CryptoUtils.fillLastBytes(counter.increment(), counterVal, blockSize);
      CryptoUtils.xorByteArrays(counterVal, iv, block);
      core.encryptBlock(block, block);
      System.arraycopy(block, 0, output, outputOffset + n, inputLen - n);
    }   
    CryptoUtils.fillWithZeroes(block);
    CryptoUtils.fillWithZeroes(counterVal);
    return CryptoUtils.xorByteArrays(input, inputOffset, output, outputOffset, output, outputOffset, inputLen);
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    return encryptOrDecrypt(input, false);
  }

  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        encryptOrDecrypt(in, out, true);
      }
    };
  }
  
  private void encryptOrDecrypt(InputStream in, OutputStream out, boolean encrypting) throws IOException {
    if (!hasIV()) {
      throw new IOException(new CryptoException(CryptoException.NO_IV));
    }
    if (prpendingIV) {
      if (encrypting) {
        out.write(iv);
      } else {
        in.read(iv);
      }
    }
    byte[] block = new byte[blockSize];
    byte[] counterVal = new byte[blockSize];
    byte[] input = new byte[blockSize];
    try {
      while (true) {
        int read = in.read(input);
        if (read == -1) {
          break;
        }
        //easy enough to make parallel later
        CryptoUtils.fillLastBytes(counter.increment(), counterVal, blockSize);
        CryptoUtils.xorByteArrays(counterVal, iv, block);
        core.encryptBlock(block, block);
        CryptoUtils.xorByteArrays(block, 0, input, 0, input, 0, read);
        out.write(input, 0, read);
      }
    } catch (CryptoException e) {
      throw new IOException(e);
    } finally {
      CryptoUtils.fillWithZeroes(block);
      CryptoUtils.fillWithZeroes(input);
      CryptoUtils.fillWithZeroes(counterVal);
    }
  }

  @Override
  public StreamRunnable decrypt() {
    return new StreamRunnable(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        encryptOrDecrypt(in, out, false);
      }
    };
  }
  
  @Override
  public Cipher setIV(byte[] initVector) {
    iv = Arrays.copyOf(initVector, blockSize);
    return this;
  }

  @Override
  public boolean hasIV() {
    return iv != null;
  }

  @Override
  public Cipher setKey(byte[] key) {
    core.setKey(key);
    return this;
  }

  @Override
  public boolean hasKey() {
    return core.hasKey();
  }

  @Override
  public Cipher removeKey() {
    core.removeKey();
    return this;
  }

  @Override
  public byte[] getIV() {
    return iv;
  }

  @Override
  public boolean isIVPrepending() {
    return prpendingIV;
  }

  @Override
  public Cipher setIVPrepending(boolean ivPrepending) {
    prpendingIV = ivPrepending;
    return this;
  }

}
