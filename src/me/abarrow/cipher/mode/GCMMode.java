package me.abarrow.cipher.mode;

import me.abarrow.cipher.MACCipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import me.abarrow.cipher.AuthenticatedCipher;
import me.abarrow.cipher.BlockCipher;
import me.abarrow.cipher.Cipher;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.mac.MAC;
import me.abarrow.mac.gcmac.GCMMAC;
import me.abarrow.math.Int128;
import me.abarrow.stream.StreamRunnable;

public class GCMMode implements AuthenticatedCipher {
  
  private byte[] iv;
  private byte[] authData;
  private BlockCipher bc;
  private boolean prpendingIV;

  
  private static final int BLOCK_SIZE = 16;

  public GCMMode(BlockCipher blockCipher) throws CryptoException {
    bc = blockCipher;
    if (bc.getBlockBytes() != BLOCK_SIZE) {
      throw new CryptoException(CryptoException.INCOMPATIBLE_CIPHER);
    }
  }
  
  public static void ghash(Int128 h, byte[] a, byte[] c, Int128 x) {
    Int128 extra = new Int128();
    Int128 spareInt = new Int128();
    byte[] spare = new byte[BLOCK_SIZE];
    
    try {
      x.toZero();
      int n;
      for (n = 0; n < a.length - BLOCK_SIZE; n += BLOCK_SIZE) {
        extra.copyFromLittleEndian(a, n);
        x.xorEquals(extra);
        x.finiteTimesEquals(h, spareInt);
      }
      extra.toZero();
      if (n != a.length) {
        extra.copyFromLittleEndian(a, n);
        x.xorEquals(extra);
        x.finiteTimesEquals(h, spareInt);
      }
      
      for (n = 0; n < c.length - BLOCK_SIZE; n += BLOCK_SIZE) {
        extra.copyFromLittleEndian(c, n);
        x.xorEquals(extra);
        x.finiteTimesEquals(h, spareInt);
      }
      extra.toZero();
      if (n != c.length) {
        extra.copyFromLittleEndian(c, n);
        x.xorEquals(extra);
        x.finiteTimesEquals(h, spareInt);
      }

      CryptoUtils.longToBytes(((long)a.length)*8, spare, 0, false);
      CryptoUtils.longToBytes(((long)c.length)*8, spare, 8, false);
      extra.copyFromLittleBitEndian(spare);
      x.xorEquals(extra);
      x.finiteTimesEquals(h, spareInt);
    } finally {
      spareInt.toZero();
      extra.toZero();
      CryptoUtils.fillWithZeroes(spare);
    }
    
  }

	@Override
	public StreamRunnable encrypt() {
		return new StreamRunnable() {
      @Override
      public void process(InputStream in, OutputStream out) throws IOException {
        if (!hasIV()) {
          throw new IOException(new CryptoException(CryptoException.NO_IV));
        }
        byte[] zeroBlock = new byte[BLOCK_SIZE];
        byte[] hBytes = new byte[BLOCK_SIZE];
        Int128 hint = new Int128();
        
        
        byte[] y = new byte[BLOCK_SIZE];
        try {
          bc.encryptBlock(zeroBlock, hBytes);
          if (iv.length == 12) {
            System.arraycopy(iv, 0, y, 0, 12);
          } else {
            
          }
          

        } catch(CryptoException ce) {
          throw new IOException(ce);
        } finally {
          CryptoUtils.fillWithZeroes(y);
          CryptoUtils.fillWithZeroes(hBytes);
          hint.toZero();
        }
      }
		};
	}

	@Override
	public StreamRunnable decrypt() {
		// TODO Auto-generated method stub
		return null;
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