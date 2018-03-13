// This class Creates a block and checks the validity of previous block

import com.google.gson.*;
import java.util.*;
import java.util.Date;

public class Block {
  private String hash; // Storing hash of current block
  private String previousHash; // Storing hash of previous block
  private long timeStamp; // Storing the timeStamp
  private int nonce; // storing the number of nonce
  public String merkleRoot; // Stores the merkleRoot
  public ArrayList<Transaction> transactions =
      new ArrayList<Transaction>(); // our data will be a simple message.

  // Sets previous hash
  // generates the timeStamp
  // calculates the current hash
  public Block(String previousHash) {
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();
    this.hash = calculateHash();
    nonce = 0;
  }

  // returns the calculated Hash
  public String calculateHash() {
    String calculatedHash =
        ToHash.applyHash(previousHash + "" + timeStamp + "" + nonce + "" + merkleRoot);
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
    System.out.println("Block Mined! : " + getHash());
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

  // Add transaction to the block
  public boolean addTransaction(Transaction transaction) {
    // process transaction and check if valid, unless block is genesis block then ignore.
    if (transaction == null) {
      return false;
    }
    if ((previousHash != "0")) {
      if (!(transaction.processTransaction())) {
        System.out.println("Transaction failed to process. Discarded.");
        return false;
      }
    }
    transactions.add(transaction);
    System.out.println("Transaction Successfully added to Block");
    return true;
  }

  // Returns block in Json format
  public String toString() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(this);
  }
}
