package me.abarrow.hash.sha;

import java.util.Arrays;

import me.abarrow.core.CryptoUtils;
import me.abarrow.hash.Hasher;

public abstract class SHA32Hash extends Hasher {

  private static final int MIN_PADDING_BYTES = 9;
  protected int[] hash;
  protected int[] W;

  @Override
  public final byte[] computeHash(byte[] remainder, int remainderLength) {
    hashByteCount.plusEquals(remainderLength);
    
    byte[] block = new byte[getBlockBytes()];
    System.arraycopy(remainder, 0, block, 0, remainderLength);

    if (remainderLength == 0) {
      fillPadding(block, 0);
      innerHashBlock(block, 0);
    } else if ((getBlockBytes() - remainderLength) < SHA32Hash.MIN_PADDING_BYTES) {
      block[remainderLength] = CryptoUtils.ONE_AND_SEVEN_ZEROES_BYTE;
      innerHashBlock(block, 0);

      Arrays.fill(block, 0, block.length, (byte) 0);
      appendWithLength(block);
      innerHashBlock(block, 0);
    } else {
      fillPadding(block, remainderLength);
      innerHashBlock(block, 0);
    }

    byte[] result = CryptoUtils.intArrayToByteArray(new byte[getHashByteLength()], 0, hash, isHashLittleEndian());
    reset();
    return result;
  }
  
  @Override
  public final void hashBlock(byte[] data, int srcPos) {
    hashByteCount.plusEquals(getBlockBytes());
    innerHashBlock(data, srcPos);
  }

  
  protected abstract void innerHashBlock(byte[] data, int srcPos);


  private void fillPadding(byte[] padded, int startIndex) {
    padded[startIndex] = CryptoUtils.ONE_AND_SEVEN_ZEROES_BYTE;
    appendWithLength(padded);
  }

  private void appendWithLength(byte[] padded) {
    CryptoUtils.longToBytes(hashByteCount.longValue() * 8, padded, padded.length - 8, isHashLittleEndian());
  }

  @Override
  public void reset() {
    super.reset();
    int[] intialHashes = getInitialHashes();
    if (hash == null) {
      hash = Arrays.copyOf(intialHashes, intialHashes.length);
      W = new int[getWLength()];
    } else {
      System.arraycopy(intialHashes, 0, hash, 0, intialHashes.length);
      CryptoUtils.fillWithZeroes(W);
    }
  }
  
  public boolean isHashLittleEndian() {
    return false;
  }
  
  protected abstract int[] getInitialHashes();
  protected abstract int getWLength();

}
