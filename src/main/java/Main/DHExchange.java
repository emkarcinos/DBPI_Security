package Main;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class DHExchange {
    private static final Logger logger = LogManager.getLogger(App.class);
    private KeyPair keyPair = null;
    private KeyAgreement agreement = null;
    private PublicKey thisPublicKey;
    private PublicKey otherPublicKey;
    private final int keySize;
    private SecretKeySpec secretKey;


    public DHExchange(int keySize, boolean isHost) {
        this.keySize = keySize;
        if (isHost)
            createKeyPar();
    }

    void createKeyPar() {
        logger.log(Level.INFO, "Generating " + keySize + "-bit key pair...");

        try {
            KeyPairGenerator kPairGen = KeyPairGenerator.getInstance("DH");
            kPairGen.initialize(keySize);
            keyPair = kPairGen.generateKeyPair();


            agreement = KeyAgreement.getInstance("DH");
            agreement.init(keyPair.getPrivate());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        thisPublicKey = keyPair.getPublic();
    }

    void createKeyParFromPublicKey(byte[] publicKeyBytes) throws Exception {
        if (keyPair != null)
            throw new Exception("Key pair alredy exists! Perhaps is was generated before?");
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);

            otherPublicKey = keyFactory.generatePublic(x509KeySpec);

            DHParameterSpec dhParam = ((DHPublicKey) otherPublicKey).getParams();

            logger.log(Level.INFO, "Generating " + keySize + "-bit key pair...");
            KeyPairGenerator kPairGen = KeyPairGenerator.getInstance("DH");
            kPairGen.initialize(dhParam);
            keyPair = kPairGen.generateKeyPair();

            agreement = KeyAgreement.getInstance("DH");
            agreement.init(keyPair.getPrivate());

            thisPublicKey = keyPair.getPublic();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    void onRecievePubKey(byte[] publicKeyBytes) {
        try {
            KeyFactory keyFac = KeyFactory.getInstance("DH");

            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
            otherPublicKey = keyFac.generatePublic(x509KeySpec);
            logger.log(Level.INFO, "Agreement phase...");
            agreement.doPhase(otherPublicKey, true);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    byte[] getThisPublicKeyEncoded() {
        return thisPublicKey.getEncoded();
    }

    byte[] getOtherPublicKeyEncoded() {
        return otherPublicKey.getEncoded();
    }

    void generateSecret() {
        byte[] secret = agreement.generateSecret();
        secretKey = new SecretKeySpec(secret, 0, 16, "AES");
    }

    public SecretKeySpec getSecretKey() {
        return secretKey;
    }
}

