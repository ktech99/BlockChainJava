import java.security.*;
import java.security.Security;
import java.util.*;
import org.bouncycastle.jce.provider.*;

public class BlockChain {

  public static ArrayList<Block> blockList = new ArrayList<Block>();
  public static int difficulty = 5;
  public static Map<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
  public static Wallet walletA;
  public static Wallet walletB;
  public static float minimumTransaction = 0.1f;

  public static void main(String[] args)
      throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
    // boolean valid = true;
    // Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    //
    // // creating first block with previous block hash = 0
    // Block genesisBlock = new Block("Yo this the First Block", "0");
    // System.out.println("first Hash :" + genesisBlock.getHash());
    // blockList.add(genesisBlock);
    // blockList.get(0).mineBlock(difficulty);
    // // Creating second Block, storing Hash of previous block
    // Block secondBlock = new Block("Yo im the second block", genesisBlock.getHash());
    // System.out.println("Hash for block 2 : " + secondBlock.getHash());
    // blockList.add(secondBlock);
    // blockList.get(1).mineBlock(difficulty);
    // // checking if blocks are valid
    // for (int i = 1; i < blockList.size(); i++) {
    //   if (!blockList.get(i).isValid(blockList.get(i - 1), difficulty)) {
    //     valid = false;
    //   }
    // }
    // System.out.println("blockChain is valid: " + valid);
    // // converting blockChain to json format
    // System.out.println("The BlockChain:");
    // for (Block b : blockList) {
    //   System.out.println(b.toString());
    // }
    // System.out.println("Is signature verified");
    // System.out.println(Transaction.verifiySignature());
    // Setup Bouncey castle as a Security Provider
    // Setup Bouncey castle as a Security Provider
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    // Create the new wallets
    walletA = new Wallet();
    walletB = new Wallet();
    // Test public and private keys
    System.out.println("Private and public keys:");
    System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
    System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
    // Create a test transaction from WalletA to walletB
    Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
    transaction.generateSignature(walletA.privateKey);
    // Verify the signature works and verify it from the public key
    System.out.println("Is signature verified");
    System.out.println(transaction.verifiySignature());
  }
}
