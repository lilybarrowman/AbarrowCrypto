package cipher.mode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import stream.StreamRunnable;
import core.CryptoException;
import core.CryptoUtils;
import counter.BigIntCounter;
import counter.Counter;
import cipher.BlockCipher;
import cipher.Cipher;

public class CTRMode implements Cipher {
  
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
  
  public CTRMode(BlockCipher cipherCore, Counter c) {
    core = cipherCore;
    blockSize = core.getBlockBytes();
    counter = c;
  }
  
  public CTRMode(BlockCipher cipherCore, Counter c, byte[] initVec) {
    this(cipherCore, c);
    setIV(initVec);
  }

  @Override
  public byte[] encrypt(byte[] input) throws CryptoException {
    if (!hasIV()) {
      throw new CryptoException(CryptoException.NO_IV);
    }
    counter.reset();
    int inputLen = input.length;
    int outputOffset = 0;
    byte[] output = new byte[inputLen];
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
    return CryptoUtils.xorByteArrays(input, 0, output, outputOffset, output, outputOffset, inputLen);
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    return encrypt(input);
  }

  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable(){
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (!hasIV()) {
          throw new IOException(new CryptoException(CryptoException.NO_IV));
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
    };
  }

  @Override
  public StreamRunnable decrypt() {
    return encrypt();
  }
  
  @Override
  public boolean setIV(byte[] initVector) {
    iv = Arrays.copyOf(initVector, blockSize);
    return true;
  }

  @Override
  public boolean hasIV() {
    return iv != null;
  }

  @Override
  public void setKey(byte[] key) {
    core.setKey(key);
  }

  @Override
  public boolean hasKey() {
    return core.hasKey();
  }

  @Override
  public void removeKey() {
    core.removeKey();
  }

  @Override
  public int getBlockBytes() {
    return core.getBlockBytes();
  }

  @Override
  public byte[] getIV() {
    return iv;
  }

}
