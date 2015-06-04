package cipher.mode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import core.CryptoException;
import core.CryptoUtils;
import cipher.BlockCipher;
import cipher.Cipher;
import stream.StreamRunnable;

public class ECBMode implements Cipher {

  private BlockCipher core;
  private int blockSize;

  public ECBMode(BlockCipher c) {
    core = c;
    blockSize = core.getBlockBytes();
  }

  @Override
  public byte[] encrypt(byte[] input) throws CryptoException {
    if ((input.length % blockSize) != 0) {
      throw new CryptoException(CryptoException.INVALID_LENGTH);
    }
    int blockCount = input.length / blockSize;
    byte[] output = new byte[input.length];
    for (int n = 0, pos = 0; n < blockCount; n++, pos += blockSize) {
      core.encryptBlock(input, pos, output, pos);
    }
    return output;
  }

  @Override
  public byte[] decrypt(byte[] input) throws CryptoException {
    if ((input.length % blockSize) != 0) {
      throw new CryptoException(CryptoException.INVALID_LENGTH);
    }
    int blockCount = input.length / blockSize;
    byte[] output = new byte[input.length];
    for (int n = 0, pos = 0; n < blockCount; n++, pos += blockSize) {
      core.decryptBlock(input, pos, output, pos);
    }
    return output;
  }

  @Override
  public StreamRunnable encrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        byte[] block = new byte[blockSize];
        byte[] output = new byte[blockSize];
        try {
          while (true) {
            int read = in.read(block);
            if (read == blockSize) {
              core.encryptBlock(block, output);
              out.write(output);
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
          CryptoUtils.fillWithZeroes(block);
        }
      }
    };
  }

  @Override
  public StreamRunnable decrypt() {
    return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        byte[] block = new byte[blockSize];
        byte[] output = new byte[blockSize];
        try {
          while (true) {
            int read = in.read(block);
            if (read == blockSize) {
              core.decryptBlock(block, output);
              out.write(output);
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
          CryptoUtils.fillWithZeroes(block);
        }
      }
    };
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
  public boolean setIV(byte[] initVector) { 
    return false;
  }

  @Override
  public boolean hasIV() {
    return false;
  }

  @Override
  public int getBlockBytes() {
    return core.getBlockBytes();
  }

  @Override
  public byte[] getIV() {
    return null;
  }

}
