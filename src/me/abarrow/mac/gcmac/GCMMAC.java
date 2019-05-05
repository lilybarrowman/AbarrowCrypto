package me.abarrow.mac.gcmac;

import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.mac.MAC;
import me.abarrow.stream.StreamProcess;

public class GCMMAC implements MAC {
  
  public GCMMAC() {
  }

  @Override
  public StreamProcess tag(boolean tagOnly) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public byte[] tag(byte[] data, boolean tagOnly) throws CryptoException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StreamProcess checkTag(boolean checkOnly) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public byte[] checkTag(byte[] data, boolean checkOnly) throws CryptoException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MAC setKey(byte[] key) throws CryptoException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasKey() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public MAC removeKey() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getTagLength() {
    // TODO Auto-generated method stub
    return 0;
  }
}
