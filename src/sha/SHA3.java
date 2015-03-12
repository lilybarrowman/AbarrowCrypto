package sha;

import core.CryptoUtils;
import core.Hasher;

public class SHA3 extends Hasher {

  /**
   * A 3 dimensional array of bits
   * <ul>
   * <li>First dimension is the column</li>
   * <li>Second dimension is the row</li>
   * <li>Third dimension is the depth</li>
   * </ul>
   * col 0 row 0 depth 0 -> [0][0][0] -> 0 <br>
   * col 0 row 0 depth 1 -> [0][0][1] -> 1 <br>
   * col 1 row 0 depth 0 -> [1][0][0] -> 64 <br>
   * col 0 row 1 depth 0 -> [0][1][0] -> 320 <br>
   * col 4 row 4 depth 1599 -> [4][4][63] -> 1599
   */
  private long[] state;
  
  private long[] stateCopy;
  private long[] C;

  private static final long[] RC = new long[] { 0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
      0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L, 0x8000000080008081L, 0x8000000000008009L,
      0x000000000000008aL, 0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL, 0x000000008000808bL,
      0x800000000000008bL, 0x8000000000008089L, 0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
      0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L, 0x8000000000008080L, 0x0000000080000001L,
      0x8000000080008008L };

  private static final int[] BIT_SHIFTS = new int[] { 0, 1, 190, 28, 91, 36, 300, 6, 55, 276, 3, 10, 171, 153, 231,
      105, 45, 15, 21, 136, 210, 66, 253, 120, 78 };

  private int outputBytes;
  private int blockBytes;
  
  private SHA3Mode mode;

  public SHA3(SHA3Mode type, int outputSize) {
    mode = type;
    reset();
    outputBytes = outputSize;
    if (outputBytes == 28) {
      blockBytes = 144;
    } else if (outputBytes == 32) {
      blockBytes = 136;
    } else if (outputBytes == 48) {
      blockBytes = 104;
    } else if (outputBytes == 64) {
      blockBytes = 72;
    } else {
      throw new IllegalArgumentException("SHA3 cannot have an ouput size of " + outputSize + " bytes.");
    }

  }

  @Override
  public byte[] computeHash() {
    byte[] padded = new byte[blockBytes];
    int copiedLength = toHash.length;

    if (copiedLength == 0) {
      if (mode == SHA3Mode.KECCAK) {
        padded[0] = (byte) 0x1; //Keccak  0000 0001
      } else {
        padded[0] = (byte) 0x6; // SHA3 0000 0110
      }
      padded[blockBytes - 1] = (byte) 0x80; //1000 0000
      hashBlock(padded, 0);

    } else if (copiedLength == (blockBytes - 1)) {
      System.arraycopy(toHash, 0, padded, 0, copiedLength);
      if (mode == SHA3Mode.KECCAK) {
        padded[copiedLength] = (byte) 0x81; // Keccak 1000 0110
      } else {
        padded[copiedLength] = (byte) 0x86; // SHA3 1000 0110
      }
      hashBlock(padded, 0);

    } else {
      System.arraycopy(toHash, 0, padded, 0, copiedLength);
      if (mode == SHA3Mode.KECCAK) {
        padded[copiedLength] = (byte) 0x1; //Keccak  0000 0001
      } else {
        padded[copiedLength] = (byte) 0x6; // SHA3 0000 0110
      }
      padded[blockBytes - 1] = (byte) 0x80; // 1000 0000
      hashBlock(padded, 0);
    }
    
    return CryptoUtils.safeLongArrayToByteArray(new byte[outputBytes], 0, state, (outputBytes + 7) / 8 , true);
  }

  @Override
  public Hasher reset() {
    super.reset();
    if (state != null) {
      CryptoUtils.fillWithZeroes(state);
      CryptoUtils.fillWithZeroes(stateCopy);
      CryptoUtils.fillWithZeroes(C); 
    } else {
      state = new long[25];
      stateCopy = new long[25];
      C = new long[5];
      
    }
    return this;
  }

  @Override
  public int getBlockBytes() {
    return blockBytes;
  }

  @Override
  public int getHashByteLength() {
    return outputBytes;
  }

  private int getNthBit(long i, int n) {
    //return (int) ((i >>> (63 - n)) & 1);
    return (int) ((i >>> n) & 1);
  }
  
  
  private int getXYZBit(long[] curState, int x, int y, int z) {
    return getNthBit(curState[y * 5 + x], z);
  }
  
  private void outputState(long[] curState) {
    for (int index = 0; index < 1600; index++) {
      
      int row = index / 320;
      int column = (index / 64) % 5;
      int depth = index % 64;
      
      System.out.println(index + " (" + column + ", " + row + ", " + depth + ") " + getXYZBit(curState, column, row, depth));
    }
  }
  
  private void outputStateBitString(long[] curState) {
    String string = "";
    for (int index = 0; index < 1600; index++) {
      
      int row = index / 320;
      int column = (index / 64) % 5;
      int depth = index % 64;
      string += getXYZBit(curState, column, row, depth);
      if ((index % 8) == 7) {
        string += " ";
      }
    }
    System.out.println(string);

  }
  
  private static final int[] ROW_OUTPUT_ORDER = new int[]{2, 1, 0, 4, 3};
  private static final int[] COLUMN_OUTPUT_ORDER = new int[]{3, 4, 0, 1, 2};
  
  private void outputSlice(long[] curState, int depth) {
    for (int row = 0; row < 5; row++) {
      outputRow(curState, row, depth);
    }
  }
  
  private void outputRow(long[] curState, int row, int depth) {
    String rowString = "";
    for (int column = 0; column < 5; column++) {
      rowString += getXYZBit(curState, COLUMN_OUTPUT_ORDER[column], ROW_OUTPUT_ORDER[row], depth);
    }
    System.out.println(rowString);
  }
  
  private void outputSheet(long[] curState, int column) {
    for (int row = 0; row < 5; row++) {
      String rowString = "";
      for (int depth = 0; depth < 64; depth++) {
        rowString += getXYZBit(curState, column, ROW_OUTPUT_ORDER[row], depth);
        if (depth%8 ==7){
          rowString += " ";
        }
      }
      System.out.println(rowString);
    }
  }
  
  private void outputSheets(long[] curState) {
    for (int column = 0; column < 5; column++) {
      outputSheet(curState, COLUMN_OUTPUT_ORDER[column]);
      System.out.println("------");
    }
  }
  

  @Override
  protected void hashBlock(byte[] data, int startIndex) {    
    CryptoUtils.xorLongArrayFromBytes(state, 0, data, startIndex, blockBytes / 8, true);
    
        
    // perform a block permutation
    for (int n = 0; n < 24; n++) {
      
      // theta is working
      for (int x = 0; x < 5; x++) {
        C[x] = state[x] ^ state[5 + x] ^ state[10 + x] ^ state[15 + x] ^ state[20 + x];
      }
      
      int [] stateInts = CryptoUtils.intArrayFromBytes(CryptoUtils.longArrayToByteArray(state), 0, 200);
      
      
      for (int x = 0; x < 5; x++) {
        int sx =(x + 4) % 5;
        int gx = (x + 1) % 5;
        
        long sum = 0;
        
        for (int z = 0; z < 64; z ++) {
          int sz = (z + 63) % 64;
          
          sum ^= (((C[sx] >>> z) & 0x1L) ^ ((C[gx] >>> sz) & 0x1L)) << z;
        }
        for (int y = 0; y < 5; y++) {
          state[x + 5 * y] ^= sum;
        }
      }
      // rho is working
      for (int i = 1; i < 25; i++) {
        state[i] = CryptoUtils.rotateLongLeft(state[i], SHA3.BIT_SHIFTS[i] % 64);
      }
      

      // pi is working
      System.arraycopy(state, 0, stateCopy, 0, state.length);
      for (int i = 0; i < 25; i++) {
        int x = i % 5;
        int y = i / 5;
        state[i] = stateCopy[(x + 3 * y) % 5 + 5 * x];
      }
      
      
      // chi is working
      System.arraycopy(state, 0, stateCopy, 0, state.length);
      for (int i = 0; i < 25; i++) {

        int x = i % 5;
        int y = i / 5;

        state[i] = stateCopy[i] ^ ((~stateCopy[(x + 1) % 5 + 5 * y]) & stateCopy[(x + 2) % 5 + 5 * y]);
      }

      // iota
      state[0] ^= RC[n];
    }
    
    CryptoUtils.fillWithZeroes(stateCopy);
    CryptoUtils.fillWithZeroes(C); 
  }

}
