// This class Creates a block and checks the validity of previous block

import com.google.gson.*;
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
    nonce = 0;
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
    while (!getHash().substring(0, difficulty).equals(target)) {
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
    return this.getHash().equals(this.calculateHash())
        && previousBlock.getHash() == this.getPreviousHash()
        && this.getHash().substring(0, difficulty).equals(hashTarget);
  }

  // Returns block in Json format
  public String toString() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(this);
  }
}
