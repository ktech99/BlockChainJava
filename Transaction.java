import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class Transaction {

  private String transactionId; // Hash of the transaction
  private PublicKey sender; // Senders Public Key
  private PublicKey reciever; // Recievers public key
  private float amount; // Amount to send
  private byte[] signature; // Our signatutre

  private ArrayList<TransactionInput> inputs;
  private ArrayList<TransactionOutput> outputs;

  private static int number; // approximate number of transactions

  public Transaction(
      PublicKey from, PublicKey to, float amount, ArrayList<TransactionInput> inputs) {
    this.sender = from;
    this.reciever = to;
    this.amount = amount;
    this.inputs = inputs;
    number = 0;
  }

  private String calulateHash() {
    number++; // increase the number to avoid 2 identical transactions having the same hash
    return StringUtil.applySha256(
        StringUtil.getStringFromKey(sender)
            + StringUtil.getStringFromKey(reciever)
            + Float.toString(getAmount())
            + number);
  }

  // Applies ECDSA Signature and returns the result ( as bytes ).
  public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
    Signature dsa;
    byte[] output = new byte[0];
    try {
      dsa = Signature.getInstance("ECDSA", "BC");
      dsa.initSign(privateKey);
      byte[] strByte = input.getBytes();
      dsa.update(strByte);
      byte[] realSig = dsa.sign();
      output = realSig;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return output;
  }

  // Verifies a String signature
  public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
    try {
      Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
      ecdsaVerify.initVerify(publicKey);
      ecdsaVerify.update(data.getBytes());
      return ecdsaVerify.verify(signature);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String getStringFromKey(Key key) {
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  // Signs all the data we dont wish to be tampered with.
  public void generateSignature(PrivateKey privateKey) {
    String data =
        StringUtil.getStringFromKey(getSender())
            + StringUtil.getStringFromKey(getReciever())
            + Float.toString(getAmount());
    signature = StringUtil.applyECDSASig(privateKey, data);
  }
  // Verifies the data we signed hasnt been tampered with
  public boolean verifiySignature() {
    String data =
        StringUtil.getStringFromKey(getSender())
            + StringUtil.getStringFromKey(getReciever())
            + Float.toString(getAmount());
    return StringUtil.verifyECDSASig(getSender(), data, getSignature());
  }

  public boolean processTransaction() {
    if (!verifiySignature()) {
      System.out.println("# Failed to verify transaction signature");
      return false;
    }
    for (TransactionInput i : inputs) {
      i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
    }

    if (getInputsValue() < BlockChain.minimumTransaction) {
      System.out.println("Transaction too small: " + getInputsValue());
      return false;
    }

    float leftOver = getInputsValue() - getAmount();
    transactionId = calulateHash();
    outputs.add(new TransactionOutput(this.getReciever(), getAmount(), getTransactionId()));
    outputs.add(new TransactionOutput(this.getSender(), leftOver, getTransactionId()));
    for (TransactionOutput out : outputs) {
      BlockChain.UTXOs.put(out.id, out);
    }
    for (TransactionInput i : inputs) {
      if (i.UTXO != null) {
        BlockChain.UTXOs.remove(i.UTXO.id);
      }
    }
    return true;
  }

  public float getInputsValue() {
    float total = 0;
    for (TransactionInput i : inputs) {
      if (i.UTXO != null) {
        total += i.UTXO.value;
      }
    }
    return total;
  }

  public float getOutputsValue() {
    float total = 0;
    for (TransactionOutput o : outputs) {
      total += o.value;
    }
    return total;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public PublicKey getSender() {
    return sender;
  }

  public PublicKey getReciever() {
    return reciever;
  }

  public float getAmount() {
    return amount;
  }

  public byte[] getSignature() {
    return signature;
  }
}
