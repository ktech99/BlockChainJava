import java.util.*;

public class BlockChain {

  private static ArrayList<Block> blockList = new ArrayList<Block>();
  public static final int difficulty = 5;

  // TODO Convert blocklist to JSON format
  public static void main(String[] args) {
    boolean valid = true;
    // creating first block with previous block hash = 0
    Block genesisBlock = new Block("Yo this the First Block", "0");
    System.out.println("first Hash :" + genesisBlock.getHash());
    // Creating second Block, storing Hash of previous block
    Block secondBlock = new Block("Yo im the second block", genesisBlock.getHash());
    System.out.println("Hash for block 2 : " + secondBlock.getHash());
    // checking if blocks are valid
    for (int i = 1; i < blockList.size(); i++) {
      if (blockList.get(i).isValid(blockList.get(i - 1), difficulty)) {
        valid = false;
      }
    }
    System.out.println("blockChain is valid: " + valid);
  }
}
