import java.util.Date;

public class Block {
  private String hash; // Storing hash of current block
  private String previousHash; // Storing hash of previous block
  private String data; // Storing data of block
  private long timeStamp; // Storing the timeStamp
  private int nonce; // storing the number of nonce

  // Sets data
  // Sets previous hash
  // generates the timeStamp
  // calculates the current hash
  public Block(String data, String previousHash) {
    this.data = data;
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();
    this.hash = calculateHash();
  }

  // returns the calculated Hash
  public String calculateHash() {
    String calculatedHash =
        ToHash.applyHash(previousHash + "" + timeStamp + "" + nonce + "" + data);
    return calculatedHash;
  }

  // Defines the difficulty of mining the hash
  public void mineBlock(int difficulty) {
    String target =
        new String(new char[difficulty])
            .replace('\0', '0'); // Create a string with difficulty * "0"
    // Increases the nonce value till the hash matches the required target
    while (!hash.substring(0, difficulty).equals(target)) {
      nonce++;
      hash = calculateHash();
    }
    System.out.println("Block Mined!!! : " + hash);
  }

  // Returns the hash value
  public String getHash() {
    return hash;
  }

  // Returns the previous hash value
  public String getPreviousHash() {
    return previousHash;
  }

  // Checks if the hash is valid
  public boolean isValid(Block previousBlock, int difficulty) {
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');
    // checking if current hash is changed
    if (this.getHash() != this.calculateHash()) {
      System.out.println("Current not equal");
      return false;
    }
    // checking if previous hash is changed
    if (previousBlock.getHash() != this.getPreviousHash()) {
      System.out.println("prev not equal");
      return false;
    }
    // checking if the block has been mined
    if (!this.getHash().substring(0, difficulty).equals(hashTarget)) {
      System.out.println("This block hasn't been mined");
      return false;
    }
    return true;
  }
}
