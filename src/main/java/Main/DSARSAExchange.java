package Main;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;

public class DSARSAExchange {
    private static int keySize = 1024;

    public static KeyPair generateKeyPair(String algorithm){
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance(algorithm);
            generator.initialize(keySize);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert generator != null;
        return generator.generateKeyPair();
    }

    public static byte[] signWithDSA(String message, PrivateKey key) {
        try {
            Signature signature = Signature.getInstance("SHA256withDSA");
            signature.initSign(key);
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            return signature.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verifyDSASignature(String message, byte[] signature, PublicKey key){
        try {
            Signature signatureInstance = Signature.getInstance("SHA256withDSA");
            signatureInstance.initVerify(key);
            signatureInstance.update(message.getBytes(StandardCharsets.UTF_8));

            return signatureInstance.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] encryptUsingRSA(String message, PublicKey key){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptUsingRSA(byte[] data, PrivateKey key){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] signUsingRSA(byte[] data, PrivateKey key){
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(key);
            signature.update(data);

            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verifyRSASignature(byte[] data, byte[] signature, PublicKey key){
        try {
        Signature signatureInstance = Signature.getInstance("SHA256withRSA");
        signatureInstance.initVerify(key);
        signatureInstance.update(data);

            return signatureInstance.verify(signature);
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return false;
    }
}
