// The following code is from
// https://gist.github.com/CryptoKass/6f540a2cd67ebad81a057ea3a50e7f09#file-wallet-java
// This class stores the public and private key pair that will be used for transactions

import java.security.*;
import java.security.spec.*;
import java.util.*;

public class Wallet {
  public PrivateKey privateKey;
  public PublicKey publicKey;
  public Map<String, TransactionOutput> UTXOs;

  public Wallet()
      throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
    generateKeyPair();
    UTXOs = new HashMap<String, TransactionOutput>();
  }

  // Generating the keypairs using Elliptical curve Cryptography
  public void generateKeyPair()
      throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
    ECGenParameterSpec ecsp = new ECGenParameterSpec("prime192v1");
    keyGen.initialize(ecsp, random);
    KeyPair keyPair = keyGen.generateKeyPair();
    privateKey = keyPair.getPrivate();
    publicKey = keyPair.getPublic();
  }

  // Returns balance of the wallet by adding up all unspent transactions
  public float getBalance() {
    float total = 0;
    for (String item : BlockChain.UTXOs.keySet()) {
      TransactionOutput UTXO = BlockChain.UTXOs.get(item);
      if (UTXO.isMine(publicKey)) {
        UTXOs.put(UTXO.id, UTXO);
        total += UTXO.value;
      }
    }
    return total;
  }

  public Transaction sendFunds(PublicKey recieverKey, float value) {
    if (getBalance() < value) {
      System.out.println("Not enough funds. You have :" + getBalance());
      return null;
    }
    ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    float total = 0;
    for (String item : UTXOs.keySet()) {
      TransactionOutput UTXO = UTXOs.get(item);
      total += UTXO.value;
      inputs.add(new TransactionInput(UTXO.id));
      if (total > value) {
        break;
      }
    }

    Transaction newTransaction = new Transaction(publicKey, recieverKey, value, inputs);
    newTransaction.generateSignature(privateKey);
    for (TransactionInput input : inputs) {
      UTXOs.remove(input.transactionOutputId);
    }
    return newTransaction;
  }
}
