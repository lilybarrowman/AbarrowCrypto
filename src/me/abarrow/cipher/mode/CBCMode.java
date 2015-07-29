package me.abarrow.cipher.mode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import me.abarrow.cipher.BlockCipher;
import me.abarrow.cipher.Cipher;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.padding.Padding;
import me.abarrow.stream.StreamRunnable;

public class CBCMode implements Cipher {

  private byte[] iv;
  private BlockCipher core;
  private int blockSize;
  private Padding padding;
  private boolean prpendingIV;
  
  public CBCMode(BlockCipher cipherCore, Padding p) {
    core = cipherCore;
    blockSize = core.getBlockBytes();
    padding = p.setBlockSize(blockSize);
    prpendingIV = true;
  }

  public CBCMode(BlockCipher cipherCore, Padding p, byte[] intializationVector) {
    this(cipherCore, p);
    setIV(intializationVector);
  }
  
  @Override
  public byte[] encrypt(byte[] unpadded) throws CryptoException {
    if (!hasIV()) {
      throw new CryptoException(CryptoException.NO_IV);
    }
    byte[] input = padding.pad(unpadded);
    byte[] output = null;
    int outputOffset = 0;
    if (prpendingIV) {
      output = new byte[input.length + blockSize];
      System.arraycopy(iv, 0, output, 0, blockSize);
      outputOffset = blockSize;
    } else {
      output = new byte[input.length];
    }
    int i;
    byte[] xored = new byte[blockSize];
    System.arraycopy(iv, 0, xored, 0, blockSize);
    for (i = 0; (i + blockSize - 1) < input.length; i += blockSize) {
      CryptoUtils.xorByteArrays(input, i, xored, 0, xored, 0, blockSize);
      core.encryptBlock(xored, xored);
      System.arraycopy(xored, 0, output, i + outputOffset, blockSize);
    }

    CryptoUtils.fillWithZeroes(xored);
    return output;
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    if (!hasIV()) {
      throw new CryptoException(CryptoException.NO_IV);
    }
    int inpLength = input.length;
    int inpOffset = 0;
    if (prpendingIV) {
      setIV(input);
      inpLength = input.length - blockSize;
      inpOffset = blockSize;
    }
    byte[] output = new byte[inpLength];
    byte[] swap;
    int i;
    byte[] xored = new byte[blockSize];
    byte[] chain = new byte[blockSize];
    System.arraycopy(iv, 0, chain, 0, blockSize);
    for (i = 0; (i + blockSize - 1) < inpLength; i += blockSize) {
      System.arraycopy(input, i + inpOffset, xored, 0, blockSize);
      core.decryptBlock(xored, 0, output, i);
      CryptoUtils.xorByteArrays(output, i, chain, 0, output, i, blockSize);
      swap = chain;
      chain = xored;
      xored = swap;
    }

    CryptoUtils.fillWithZeroes(xored);
    CryptoUtils.fillWithZeroes(chain);
    return padding.unpad(output);
  }

  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (!hasIV()) {
          throw new IOException(new CryptoException(CryptoException.NO_IV));
        }
        if (prpendingIV) {
          out.write(iv);
        }
        byte[] block = new byte[blockSize];
        byte[] xored = new byte[blockSize];
        byte[] pointer;
        boolean going = true;
        try {
          System.arraycopy(iv, 0, xored, 0, blockSize);
          while (going) {
            int read = in.read(block);
            if (read == blockSize) {
              pointer = block;
            } else {
              pointer = padding.pad(Arrays.copyOf(block, read < 0 ? 0 : read));
              going = false;
              if (pointer.length == 0) {
                break;
              }
            }
            CryptoUtils.xorByteArrays(pointer, 0, xored, 0, xored, 0, blockSize);
            core.encryptBlock(xored, xored);
            out.write(xored);
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
        if (prpendingIV) {
          in.read(iv);
        }
        byte[] xored = new byte[blockSize];
        byte[] output = new byte[blockSize];
        byte[] chain = new byte[blockSize];
        boolean hasOutput = false;
        byte[] swap;
        try {
          System.arraycopy(iv, 0, chain, 0, blockSize);
          while (true) {
            int read = in.read(xored);
            if (read == blockSize) {
              if (hasOutput) {
                out.write(output);
              }
              core.decryptBlock(xored, output);
              CryptoUtils.xorByteArrays(output, 0, chain, 0, output, 0, blockSize);
              swap = xored;
              xored = chain;
              chain = swap;
              hasOutput = true;
              continue;
            }
            if (read != -1) {
              throw new CryptoException(CryptoException.INVALID_LENGTH);
            }
            break;
          }
          if (hasOutput) {
            out.write(padding.unpad(output));
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
  public Cipher setKey(byte[] key) {
    core.setKey(key);
    return this;
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
