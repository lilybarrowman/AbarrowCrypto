package me.abarrow.cipher.mode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import me.abarrow.cipher.AuthenticatedCipher;
import me.abarrow.cipher.BlockCipher;
import me.abarrow.cipher.Cipher;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.math.Int128;
import me.abarrow.stream.StreamProcess;
import me.abarrow.stream.SuffixStream;
import me.abarrow.stream.DirectByteOutputStream;


public class GCMMode implements AuthenticatedCipher {
  
  private byte[] iv;
  private byte[] authData = null;
  private BlockCipher bc;
  private boolean prpendingIV = false;

  
  private static final int BLOCK_SIZE = 16;

  public GCMMode(BlockCipher blockCipher) throws CryptoException {
    bc = blockCipher;
    if (bc.getBlockBytes() != BLOCK_SIZE) {
      throw new CryptoException(CryptoException.INCOMPATIBLE_CIPHER);
    }
  }
  
  public static void ghash(Int128 h, byte[] a, byte[] c, Int128 hash, Int128 spare) {
    try {
      hash.toZero();
      ghash_array(hash, h, a, spare);
      ghash_array(hash, h, c, spare);
      finish_ghash(hash, h, ((long)a.length) * 8, ((long)c.length) * 8, spare);
      
    } finally {
      spare.toZero();
    }
  }
  
  public static void ghash(Int128 h, byte[] a, byte[] c, Int128 hash) {
    ghash(h, a, c, hash, new Int128());
  }
  
  public static void finish_ghash(Int128 hash, Int128 h, long aLen, long cLen, Int128 spare) {
    int lowest = CryptoUtils.reverseIntBitOrder((int)(aLen >>> 32));
    int low = CryptoUtils.reverseIntBitOrder((int)(aLen & 0xffffffff));
    int high = CryptoUtils.reverseIntBitOrder((int)(cLen >>> 32));
    int highest = CryptoUtils.reverseIntBitOrder((int)(cLen & 0xffffffff));
    spare.setWords(lowest, low, high, highest);
    
    hash.xorEquals(spare);
    hash.finiteTimesEquals(h, spare);
  }
  
  public static void ghash_array(Int128 hash, Int128 h, byte[] bytes, Int128 spare) {
    int n;
    for (n = 0; n < bytes.length - BLOCK_SIZE; n += BLOCK_SIZE) {
      ghash_block(hash, h, bytes, n, spare);
    }
    spare.toZero();
    if (n != bytes.length) {
      ghash_block(hash, h, bytes, n, spare);
    }
  }
  
  public static void ghash_block(Int128 hash, Int128 h, byte[] bytes, int start, Int128 spare) {
    spare.copyFromLittleBitEndian(bytes, start);
    hash.xorEquals(spare);
    hash.finiteTimesEquals(h, spare);
  }
  
  private static void weirdIncrement(Int128 x) {
    x.setWord(3, CryptoUtils.reverseIntBitOrder(CryptoUtils.reverseIntBitOrder(x.getWord(3))+1));
  }

	@Override
	public StreamProcess encrypt() {
		return new StreamProcess() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (!hasIV()) {
          throw new IOException(new CryptoException(CryptoException.NO_IV));
        }
        
        byte[] block = new byte[BLOCK_SIZE];
        byte[] encryptedIV = new byte[BLOCK_SIZE];
        byte[] counterBlock = new byte[BLOCK_SIZE];

        Int128 hInt = new Int128();
        Int128 counter = new Int128();
        Int128 hash = new Int128();
        Int128 spare = new Int128();
        
        long aLen = 0;
        
        try {
          // prepare the finite field multiplier hInt and encrypt the IV
          bc.encryptBlock(block, block);
          hInt.copyFromLittleBitEndian(block);
          CryptoUtils.fillWithZeroes(block);
          if (iv.length == 12) {
            if (prpendingIV) {
              out.write(iv);
            }
            System.arraycopy(iv, 0, block, 0, 12);
            counter.copyFromLittleBitEndian(block);
            weirdIncrement(counter);
          } else {
            if (prpendingIV) {
              throw new IOException(new CryptoException(CryptoException.CANNOT_PREPEND__IV));
            }
            ghash(hInt, new byte[0], iv, counter, spare);
          }
          counter.toLittleBitEndianBytes(encryptedIV);
          bc.encryptBlock(encryptedIV, encryptedIV);
          // prepare hash
          if (authData != null) {
            ghash_array(hash, hInt, authData, spare);
            aLen = authData.length;
          }
          
          // encrypt message
          long cLen = 0;
          while(true) {
            weirdIncrement(counter);
            counter.toLittleBitEndianBytes(counterBlock);
            bc.encryptBlock(counterBlock, counterBlock);
            
            int read = in.read(block);
            if (read == -1) {
              break;
            }
            cLen += read;
            if (read == BLOCK_SIZE) {
              CryptoUtils.xorByteArrays(block, counterBlock, block);
              ghash_block(hash, hInt, block, 0, spare);
              out.write(block);
            } else {
              // Fill the remainder of the block with 0s
              for (int n = read; n < BLOCK_SIZE; n++) {
                block[n] = 0;
                counterBlock[n] = 0;
              }
              CryptoUtils.xorByteArrays(block, counterBlock, block);
              ghash_block(hash, hInt, block, 0, spare);
              out.write(block, 0, read);
              break;
            }
          }
          in.close();
          
          //finish hash
          finish_ghash(hash, hInt, aLen * 8, cLen * 8, spare);
          hash.toLittleBitEndianBytes(block);
          CryptoUtils.xorByteArrays(block, encryptedIV, encryptedIV);
          
          out.write(encryptedIV);
        } catch(CryptoException ce) {
          throw new IOException(ce);
        } finally {
          CryptoUtils.fillWithZeroes(block);
          CryptoUtils.fillWithZeroes(encryptedIV);
          CryptoUtils.fillWithZeroes(counterBlock);
          hInt.toZero();
          counter.toZero();
        }
      }
		};
	}

	@Override
	public StreamProcess decrypt() {
		return new StreamProcess() {

      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (prpendingIV) {
          iv = new byte[12];
          int read = in.read(iv);
          if (read != iv.length) {
            throw new IOException(new CryptoException(CryptoException.NO_IV));
          }
        }
        if (!hasIV()) {
          throw new IOException(new CryptoException(CryptoException.NO_IV));
        }
        
        byte[] block = new byte[BLOCK_SIZE];
        byte[] encryptedIV = new byte[BLOCK_SIZE];
        byte[] counterBlock = new byte[BLOCK_SIZE];
        byte[] includedTag = null;
        
        DirectByteOutputStream buffOut = new DirectByteOutputStream();

        Int128 hInt = new Int128();
        Int128 counter = new Int128();
        Int128 hash = new Int128();
        Int128 spare = new Int128();
        
        long aLen = 0;
        
        try {
          // prepare the finite field multiplier hInt and encrypt the IV
          bc.encryptBlock(block, block);
          hInt.copyFromLittleBitEndian(block);
          CryptoUtils.fillWithZeroes(block);
          if (iv.length == 12) {
            System.arraycopy(iv, 0, block, 0, 12);
            counter.copyFromLittleBitEndian(block);
            weirdIncrement(counter);
          } else {
            ghash(hInt, new byte[0], iv, counter, spare);
          }
          counter.toLittleBitEndianBytes(encryptedIV);
          bc.encryptBlock(encryptedIV, encryptedIV);
          
          // prepare hash
          if (authData != null) {
            ghash_array(hash, hInt, authData, spare);
            aLen = authData.length;
          }
          
          SuffixStream sufIn = new SuffixStream(in, BLOCK_SIZE);
          if (!sufIn.hasFullSuffix()) {
            sufIn.close();
            throw new IOException(new CryptoException(CryptoException.NO_MAC));
          }
          includedTag = sufIn.getSuffix();
          
          // decrypt the message
          long cLen = 0;
          while(true) {
            weirdIncrement(counter);
            counter.toLittleBitEndianBytes(counterBlock);
            bc.encryptBlock(counterBlock, counterBlock);
            
            int read = sufIn.read(block);
            if (read == -1) {
              break;
            }
            cLen += read;
            if (read == BLOCK_SIZE) {
              ghash_block(hash, hInt, block, 0, spare);
              CryptoUtils.xorByteArrays(block, counterBlock, block);
              buffOut.write(block);
            } else {
              // Fill the remainder of the block with 0s
              for (int n = read; n < BLOCK_SIZE; n++) {
                block[n] = 0;
                counterBlock[n] = 0;
              }
              ghash_block(hash, hInt, block, 0, spare);
              CryptoUtils.xorByteArrays(block, counterBlock, block);
              buffOut.write(block, 0, read);
              break;
            }
          }
          sufIn.close();

          finish_ghash(hash, hInt, aLen * 8, cLen * 8, spare);
          hash.toLittleBitEndianBytes(block);
          CryptoUtils.xorByteArrays(block, encryptedIV, encryptedIV);
          
          if(!CryptoUtils.constantTimeArrayEquals(includedTag, encryptedIV)) {
            throw new IOException(new CryptoException(CryptoException.MAC_DOES_NOT_MATCH));
          }
          out.write(buffOut.getBuffer(), 0, buffOut.getCount());
        } catch(CryptoException ce) {
          throw new IOException(ce);
        } finally {
          CryptoUtils.fillWithZeroes(block);
          CryptoUtils.fillWithZeroes(encryptedIV);
          CryptoUtils.fillWithZeroes(counterBlock);
          CryptoUtils.fillWithZeroes(includedTag);
          hInt.toZero();
          counter.toZero();
          CryptoUtils.fillWithZeroes(buffOut.getBuffer());
        }
      }
		  
		};
	}
	
	public GCMMode setAuthData(byte[] data) {
	  if (authData != null) {
	    removeAuthData();
	  }
	  if (data != null) {
	    authData = Arrays.copyOf(data, data.length);
	  }
	  return this;
	}
	
	public GCMMode removeAuthData() {
	  CryptoUtils.fillWithZeroes(authData);
	  authData = null;
	  return this;
	}

	@Override
	public Cipher setKey(byte[] key) throws CryptoException {
	  bc.setKey(key);
		return this;
	}

	@Override
	public boolean hasKey() {
		return bc.hasKey();
	}

	@Override
	public Cipher removeKey() {
		bc.removeKey();
		return this;
	}

	@Override
	public Cipher setIV(byte[] initVector) {
	  iv = Arrays.copyOf(initVector, initVector.length);
		return this;
	}

	@Override
	public byte[] getIV() {
		return iv;
	}

	@Override
	public boolean hasIV() {
		return iv != null;
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