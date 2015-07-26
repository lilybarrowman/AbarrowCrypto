package me.abarrow.chat;

public enum ChatBlockType {

  CONNECT(0, 512), MESSAGE(2, 256);
  
  /*
  Both users accumulate random data, primarily through mouse entropy.
  One user initiates a connection by sending the hash of a small part of their stream of random data as well as an HMAC of the hash with their password.
  The other user first validates the HMAC, closing the connection if it fails. If they accept it they'll send an acceptance message.
  Then both users run 4000 or so rounds of PBKDF2 using the password and the random data to create a key for AES.
  That key is then hashed again to create a key for TwoFish and which is in turn hashed to create a key for Serpent.
  When a user wishes to send the other use a message, random data collected at the beginning is  xored with a counter and then hashed to create the IVs.
  CTR mode AES-Twofish-Serpent (in that order) is performed using the above generated keys and IVs. Then HMACs are performed using the AES key.
  The order of sent data would be IV, cipher text and finally HMAC.
  */
  
  private long value;
  private long headerBytes;
  
  ChatBlockType(long val, long headerLength) {
    value = val;
    headerBytes = headerLength;
  }
  
  public long getValue() {
    return value;
  }
  
  public long getHeaderBytes() {
    return headerBytes;
  }
  
}
