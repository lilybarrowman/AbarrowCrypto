package chat;

public class ChatBlock {
  
  private ChatBlockType type;
  private byte[] data;
  
  public ChatBlock(ChatBlockType blockType, byte[] blockData) {
    type = blockType;
    data = blockData;
  }
 
  

}
