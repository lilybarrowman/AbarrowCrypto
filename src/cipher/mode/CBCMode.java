package cipher.mode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import stream.StreamRunnable;
import cipher.BlockCipher;
import cipher.Cipher;
import core.CryptoException;
import core.CryptoUtils;

public class CBCMode implements Cipher {

  private byte[] iv;
  private BlockCipher core;
  private int blockSize;
  
  public CBCMode(BlockCipher cipherCore) {
    core = cipherCore;
    blockSize = core.getBlockBytes();
  }

  public CBCMode(BlockCipher cipherCore, byte[] intializationVector) {
    this(cipherCore);
    setIV(intializationVector);
  }
  
  @Override
  public byte[] encrypt(byte[] input) throws CryptoException {
    if (!hasIV()) {
      throw new CryptoException(CryptoException.NO_IV);
    }
    if ((input.length % blockSize) != 0) {
      throw new CryptoException(CryptoException.INVALID_LENGTH);
    }
    int outputOffset = 0;
    byte[] output = new byte[input.length];
    int i;
    byte[] xored = new byte[blockSize];
    System.arraycopy(iv, 0, xored, 0, blockSize);
    for (i = 0; (i + blockSize - 1) < input.length; i += blockSize) {
      CryptoUtils.xorByteArrays(input, i, xored, 0, xored, 0, blockSize);
      core.encryptBlock(xored, xored);
      System.arraycopy(xored, 0, output, i, blockSize);
    }

    CryptoUtils.fillWithZeroes(xored);
    return output;
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    if (!hasIV()) {
      throw new CryptoException(CryptoException.NO_IV);
    }
    if ((input.length % blockSize) != 0) {
      throw new CryptoException(CryptoException.INVALID_LENGTH);
    }
    byte[] output = new byte[input.length];
    byte[] swap;
    int i;
    byte[] xored = new byte[blockSize];
    byte[] chain = new byte[blockSize];
    System.arraycopy(iv, 0, chain, 0, blockSize);
    for (i = 0; (i + blockSize - 1) < input.length; i += blockSize) {
      System.arraycopy(input, i, xored, 0, blockSize);
      core.decryptBlock(xored, 0, output, i);
      CryptoUtils.xorByteArrays(output, i, chain, 0, output, i, blockSize);
      swap = chain;
      chain = xored;
      xored = swap;
    }

    CryptoUtils.fillWithZeroes(xored);
    CryptoUtils.fillWithZeroes(chain);
    return output;
  }

  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (!hasIV()) {
          throw new IOException(new CryptoException(CryptoException.NO_IV));
        }
        byte[] block = new byte[blockSize];
        byte[] xored = new byte[blockSize];
        try {
          System.arraycopy(iv, 0, xored, 0, blockSize);
          while (true) {
            int read = in.read(block);
            if (read == blockSize) {
              CryptoUtils.xorByteArrays(block, 0, xored, 0, xored, 0, blockSize);
              core.encryptBlock(xored, xored);
              out.write(xored);
              continue;
            }
            if (read != -1) {
              throw new CryptoException(CryptoException.INVALID_LENGTH);
            }
            break;
          }
        } catch (CryptoException e) {
          throw new IOException(e);
        } finally {
          CryptoUtils.fillWithZeroes(block);
          CryptoUtils.fillWithZeroes(xored);
        }
      }
    };
  }

  @Override
  public StreamRunnable decrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (!hasIV()) {
          throw new IOException(new CryptoException(CryptoException.NO_IV));
        }
        byte[] output = new byte[blockSize];
        byte[] chain = new byte[blockSize];
        byte[] xored = new byte[blockSize];
        byte[] swap;
        try {
          System.arraycopy(iv, 0, chain, 0, blockSize);
          while (true) {
            int read = in.read(xored);
            if (read == blockSize) {
              core.decryptBlock(xored, output);
              CryptoUtils.xorByteArrays(output, 0, chain, 0, output, 0, blockSize);
              out.write(output);
              swap = xored;
              xored = chain;
              chain = swap;
              continue;
            }
            if (read != -1) {
              throw new CryptoException(CryptoException.INVALID_LENGTH);
            }
            break;
          }
        } catch (CryptoException e) {
          throw new IOException(e);
        } finally {
          CryptoUtils.fillWithZeroes(output);
          CryptoUtils.fillWithZeroes(xored);
          CryptoUtils.fillWithZeroes(xored);
        }
      }
    };
  }

  @Override
  public void setKey(byte[] key) {
    core.setKey(key);
  }
  
  @Override
  public void setIV(byte[] initVector) {
    iv = Arrays.copyOf(initVector, blockSize);
  }
  
  @Override
  public boolean hasIV() {
    return iv != null;
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
}
