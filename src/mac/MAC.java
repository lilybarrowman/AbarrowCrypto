package mac;

import stream.StreamRunnable;

public interface MAC {
  //Calculates the authentication tag and either appends it to the to the end of the input or just outputs the tag.
  public StreamRunnable tag(boolean tagOnly);
  public byte[] tag(byte[] data, boolean tagOnly);
  
  //Buffer the input, calculate and compare authentication tags if they match output the input with
  // the tag removed and throwing if they don't match.
  public StreamRunnable unTag(boolean unTagOnly);
  public byte[] unTag(byte[] data, boolean unTagOnly);
  
  public void setKey(byte[] key);

  public int getTagLength();
}