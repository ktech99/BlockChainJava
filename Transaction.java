import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import org.apache.commons.lang3.*;

public class Transaction {

  public String transactionId; // Hash of the transaction
  public PublicKey sender; // Senders Public Key
  public PublicKey reciever; // Recievers public key
  public float amount; // Amount to send
  public byte[] signature; // Our signatutre

  public ArrayList<TransactionInput> inputs;
  public ArrayList<TransactionOutput> outputs;

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
            + Float.toString(amount)
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
        StringUtil.getStringFromKey(sender)
            + StringUtil.getStringFromKey(reciever)
            + Float.toString(amount);
    signature = StringUtil.applyECDSASig(privateKey, data);
  }
  // Verifies the data we signed hasnt been tampered with
  public boolean verifiySignature() {
    String data =
        StringUtil.getStringFromKey(sender)
            + StringUtil.getStringFromKey(reciever)
            + Float.toString(amount);
    return StringUtil.verifyECDSASig(sender, data, signature);
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

    float leftOver = getInputsValue() - amount;
    transactionId = calulateHash();
    outputs.add(new TransactionOutput(this.reciever, amount, transactionId));
    outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
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
  // TODO : remove redundency
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
}
