import java.util.Date;

public class Block {
  private String hash;
  private String previousHash;
  private String data;
  private long timeStamp;
  private int nonce;

  public Block(String data, String previousHash) {
    this.data = data;
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();
    this.hash = calculateHash();
  }

  public String calculateHash() {
    String calculatedHash =
        ToHash.applyHash(previousHash + "" + timeStamp + "" + nonce + "" + data);
    return calculatedHash;
  }

  public void mineBlock(int difficulty) {
    String target =
        new String(new char[difficulty])
            .replace('\0', '0'); // Create a string with difficulty * "0"
    while (!hash.substring(0, difficulty).equals(target)) {
      nonce++;
      hash = calculateHash();
    }
    System.out.println("Block Mined!!! : " + hash);
  }

  public String getHash() {
    return hash;
  }

  public String getPreviousHash() {
    return previousHash;
  }

  public boolean isValid(Block previousBlock, int difficulty) {
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');
    if (this.getHash() != this.calculateHash()) {
      System.out.println("Current not equal");
      return false;
    }
    if (previousBlock.getHash() != this.getPreviousHash()) {
      System.out.println("prev not equal");
      return false;
    }
    if (!this.getHash().substring(0, difficulty).equals(hashTarget)) {
      System.out.println("This block hasn't been mined");
      return false;
    }
    return true;
  }
}
