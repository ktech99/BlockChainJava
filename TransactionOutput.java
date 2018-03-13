import java.security.*;

public class TransactionOutput {
  public String id;
  public PublicKey reciepient; // also known as the new owner of these coins.
  public float value; // the amount of coins they own
  public String parentTransactionId; // the id of the transaction this output was created in

  // creates the id of transaction
  public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
    this.reciepient = reciepient;
    this.value = value;
    this.parentTransactionId = parentTransactionId;
    this.id =
        ToHash.applyHash(
            Transaction.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
  }

  // Check if coin belongs to you
  public boolean isMine(PublicKey publicKey) {
    return (publicKey == reciepient);
  }
}
