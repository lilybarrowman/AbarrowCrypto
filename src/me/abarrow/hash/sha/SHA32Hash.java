package me.abarrow.hash.sha;

import java.math.BigInteger;
import java.util.Arrays;

import me.abarrow.core.CryptoUtils;
import me.abarrow.hash.Hasher;

public abstract class SHA32Hash extends Hasher {

  private static final int MIN_PADDING_BYTES = 9;
  protected int[] hash;
  protected int[] W;
  protected byte[] padded;

  @Override
  public byte[] computeHash(byte[] out, int start) {

    int copiedLength = toHashPos;
    if (copiedLength == 0) {
      fillPadding(padded, 0);
      hashBlock(padded, 0);
    } else if ((getBlockBytes() - copiedLength) < SHA32Hash.MIN_PADDING_BYTES) {
      System.arraycopy(toHash, 0, padded, 0, copiedLength);
      padded[copiedLength] = CryptoUtils.ONE_AND_SEVEN_ZEROES_BYTE;
      hashBlock(padded, 0);

      Arrays.fill(padded, 0, padded.length, (byte) 0);
      appendWithLength(padded);
      hashBlock(padded, 0);
    } else {
      System.arraycopy(toHash, 0, padded, 0, copiedLength);
      fillPadding(padded, copiedLength);
      hashBlock(padded, 0);
    }

    CryptoUtils.fillWithZeroes(padded);
    byte[] result = CryptoUtils.intArrayToByteArray(out, start, hash, false);
    reset();
    return result;
  }

  private void fillPadding(byte[] padded, int startIndex) {
    padded[startIndex] = CryptoUtils.ONE_AND_SEVEN_ZEROES_BYTE;
    appendWithLength(padded);
  }

  private void appendWithLength(byte[] padded) {
    CryptoUtils.longToBytes(totalLength.multiply(BigInteger.valueOf(8)).longValue(), padded, padded.length - 8);
  }

  @Override
  protected void reset() {
    super.reset();
    int[] intialHashes = getInitialHashes();
    if (hash == null) {
      hash = Arrays.copyOf(intialHashes, intialHashes.length);
      W = new int[getRounds()];
      padded = new byte[getBlockBytes()];
    } else {
      System.arraycopy(intialHashes, 0, hash, 0, intialHashes.length);
      CryptoUtils.fillWithZeroes(W);
      CryptoUtils.fillWithZeroes(padded);
    }
  }
  
  protected abstract int[] getInitialHashes();
  protected abstract int getRounds();

}
