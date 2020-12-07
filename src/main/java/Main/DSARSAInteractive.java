package Main;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

public class DSARSAInteractive implements Interactive{
    ProtocolManager protocolManager;
    public DSARSAInteractive(Reader in, Writer out) { this.protocolManager = new ProtocolManager(in, out); }

    @Override
    public void beginSenderInteractive() {
        System.out.println("Generating RSA keys...");
        KeyPair rsaKeyPair = DSARSAExchange.generateKeyPair("RSA");
        System.out.println("Your RSA keys:");
        System.out.printf("\tPublic: %.8s...\n", Utils.bytesToHex(rsaKeyPair.getPublic().getEncoded()));
        System.out.printf("\tPrivate: %.8s...\n", Utils.bytesToHex(rsaKeyPair.getPrivate().getEncoded()));

        System.out.println("Generating DSA keys for message signing...");
        KeyPair dsaKeyPair = DSARSAExchange.generateKeyPair("DSA");
        System.out.println("Your DSA keys:");
        System.out.printf("\tPublic: %.8s...\n", Utils.bytesToHex(dsaKeyPair.getPublic().getEncoded()));
        System.out.printf("\tPrivate: %.8s...\n", Utils.bytesToHex(dsaKeyPair.getPrivate().getEncoded()));

        System.out.println("Signing your public RSA key using private DSA key...");
        byte[] signature = DSARSAExchange.signWithDSA(Utils.bytesToHex(rsaKeyPair.getPublic().getEncoded()), dsaKeyPair.getPrivate());
        assert signature != null;
        System.out.printf("\tSignature: %.8s...\n", Utils.bytesToHex(signature));

        System.out.println("Sending RSA, DSA public keys and signature...");
        try {
            protocolManager.sendMessage(dsaKeyPair.getPublic().getEncoded());
            protocolManager.sendMessage(rsaKeyPair.getPublic().getEncoded());
            protocolManager.sendMessage(signature);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done! Awaiting message...\n");

        try {
            byte[] incomingData = protocolManager.getMessageBytes();
            System.out.printf("Got data: %.8s...\n", Utils.bytesToHex(incomingData));
            System.out.println("Decrypting...");
            String message = DSARSAExchange.decryptUsingRSA(incomingData, rsaKeyPair.getPrivate());
            System.out.println("Got message: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void beginRecieverInteractive() {
        System.out.println("Awaiting keys and signature...");
        try {
            byte[] publicDsaKey = protocolManager.getMessageBytes();
            System.out.printf("Got DSA public key: %.8s...\n", Utils.bytesToHex(publicDsaKey));
            byte[] publicRsaKey = protocolManager.getMessageBytes();
            System.out.printf("Got RSA public key: %.8s...\n", Utils.bytesToHex(publicRsaKey));
            byte[] signature = protocolManager.getMessageBytes();
            System.out.printf("Got signature: %.8s...\n", Utils.bytesToHex(signature));

            System.out.println("Verifying RSA key...");
            boolean verificationResult = DSARSAExchange.verifyDSASignature(Utils.bytesToHex(publicRsaKey), signature, KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(publicDsaKey)));
            if(verificationResult)
                System.out.println("OK!");
            else{
                System.out.println("Key verification failed!");
                return;
            }

            System.out.print("Type a message: ");
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();

            System.out.println("Encrypting your message using public RSA key...");
            byte[] ciphertext = DSARSAExchange.encryptUsingRSA(message, KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicRsaKey)));

            System.out.println("Sending message...");
            protocolManager.sendMessage(ciphertext);
            System.out.println("Done!");
            Thread.sleep(1000);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
