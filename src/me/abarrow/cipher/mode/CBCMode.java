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
