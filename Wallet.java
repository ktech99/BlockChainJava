// The following code is from
// https://gist.github.com/CryptoKass/6f540a2cd67ebad81a057ea3a50e7f09#file-wallet-java

import java.security.*;
import java.security.spec.*;

public class Wallet {
  public PrivateKey privateKey;
  public PublicKey publicKey;

  public Wallet()
      throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
    generateKeyPair();
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
}
