package Main;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class BlindSignature implements Interactive {
    ProtocolManager protocolManager;

    int iterations = 100;

    public BlindSignature(Reader in, Writer out) {
        this.protocolManager = new ProtocolManager(in, out);
    }

    @Override
    /* Alice */
    public void beginSenderInteractive() {
        //KeyPair aliceKeyPair = DSARSAExchange.generateKeyPair("RSA");
        ArrayList<String> messagesPlaintext = new ArrayList<>();
        ArrayList<BigInteger> messageHashes = new ArrayList<>();
        ArrayList<BigInteger> ks = new ArrayList<>();
        ArrayList<BigInteger> yis = new ArrayList<>();

        try {
            System.out.println("Awaiting Bob's public key...");
            byte[] bobKeyData = protocolManager.getMessageBytes();
            RSAPublicKey bobKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bobKeyData));

            BigInteger n = bobKey.getModulus();
            BigInteger e = bobKey.getPublicExponent();

            System.out.printf("Got Bob's key:\n\tn: %s\n\te: %d\n\n", n, e);
            System.out.println();

            System.out.printf("Enrolling %d random values ki...\n", iterations);
            SecureRandom rand = new SecureRandom();
            for(int i = 0; i < iterations; i++) {

                BigInteger k = BigInteger.valueOf(0);
                BigInteger gcd = BigInteger.valueOf(0);

                while (!gcd.equals(BigInteger.valueOf(1))) {
                    k = BigInteger.valueOf(rand.nextInt());
                    if (k.equals(BigInteger.valueOf(0)) || k.equals(BigInteger.valueOf(1)) || k.signum() < 0)
                        continue;
                    gcd = k.gcd(n);
                }

                ks.add(k);
            }

            System.out.println("Done generating random k's.");
            System.out.println();

            System.out.println("First few k's:");
            for(int i=0;i<5;i++)
                System.out.printf("\t%.10s<...>\n", ks.get(i));
            System.out.println();

            System.out.printf("Enrolling %d messages...\n", iterations);
            for(int i = 0; i < iterations; i++) {

                String message = "message" + i;
                messagesPlaintext.add(message);

            }

            System.out.println("Done.");
            System.out.println("First few messages:");
            for(int i=0;i<5;i++)
                System.out.printf("\t%.10s<...>\n", messagesPlaintext.get(i));

            System.out.println();

            System.out.println("Calculating hashes for each message and blinding them...");

            for(int i = 0; i < iterations; i++) {

                byte[] messageHash = MessageDigest.getInstance("SHA-256").digest(messagesPlaintext.get(i).getBytes(StandardCharsets.UTF_8));

                BigInteger h = new BigInteger(1, messageHash);
                messageHashes.add(h);

                BigInteger b = ks.get(i).modPow(e, n);
                BigInteger y = h.multiply(b).mod(n);
                yis.add(y);

            }

            System.out.println("First few hashes:");
            for(int i=0;i<5;i++)
                System.out.printf("\t%.10s<...>\n", messageHashes.get(i));
            System.out.println();

            System.out.println("First few blinded hashes:");
            for(int i=0;i<5;i++)
                System.out.printf("\t%.10s<...>\n", yis.get(i));
            System.out.println();

            System.out.println("Sending all blinded hashed to Bob...");

            for(BigInteger y : yis)
                protocolManager.sendMessage(y.toByteArray());

            System.out.println("Done. Awaiting Bob's random j..");

            BigInteger j = new BigInteger(1, protocolManager.getMessageBytes());
            System.out.printf("Got j=%d.\n", j);

            System.out.printf("Sending %d messages and k's, without message and k number %d...\n", iterations, j);
            for(int i = 0; i < iterations; i++) {
                if(i != j.intValue())
                    protocolManager.sendMessage(messagesPlaintext.get(i));
            }

            for(int i = 0; i < iterations; i++) {
                if(i != j.intValue())
                    protocolManager.sendMessage(ks.get(i).toByteArray());
            }

            System.out.println("Done.");
            System.out.println("Awaiting Bob's signature for hidden hash number " + j);
            byte[] bobSignature = protocolManager.getMessageBytes();
            BigInteger z = new BigInteger(1, bobSignature);

            System.out.println("Got signature: " + z);

            System.out.println("Uncovering the signature...");

            BigInteger s = (z.multiply(ks.get(j.intValue()).modInverse(n))).mod(n);

            System.out.println("Uncovered signature is: " + s);
            System.out.println("Verifying...");

            BigInteger verifyResult = s.modPow(bobKey.getPublicExponent(), bobKey.getModulus());

            System.out.println("Result :" + Arrays.equals(messageHashes.get(j.intValue()).toByteArray(), verifyResult.toByteArray()));

            Thread.sleep(1000);

        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException | InterruptedException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }

    }

    @Override
    /* Bob */
    public void beginRecieverInteractive() {
        ArrayList<String> messagesPlaintext = new ArrayList<>();
        ArrayList<BigInteger> messageHashes = new ArrayList<>();
        ArrayList<BigInteger> ks = new ArrayList<>();
        ArrayList<BigInteger> hiddenHashes = new ArrayList<>();

        System.out.println("Enrolling RSA key pair...");

        KeyPair bobKeyPair = DSARSAExchange.generateKeyPair("RSA");
        RSAPublicKey bobPublic = (RSAPublicKey) bobKeyPair.getPublic();
        BigInteger n = bobPublic.getModulus();
        BigInteger e = bobPublic.getPublicExponent();
        BigInteger d = ((RSAPrivateKey)bobKeyPair.getPrivate()).getPrivateExponent();

        System.out.printf("Key:\n\tn: %s\n\te: %d\n\n", bobPublic.getModulus(), bobPublic.getPublicExponent());

        System.out.println("Sending your public key to Alice...");


        try {
            protocolManager.sendMessage(bobKeyPair.getPublic().getEncoded());
            System.out.println("Done.");

            System.out.printf("Awaiting %d hidden hashes from Alice...\n", iterations);

            for(int i = 0; i < iterations; i++){
                byte[] hiddenHash = protocolManager.getMessageBytes();
                BigInteger h = new BigInteger(1, hiddenHash);
                hiddenHashes.add(h);
            }
            System.out.println("Done.");
            System.out.println("First few blinded hashes:");
            for(int i=0;i<5;i++)
                System.out.printf("\t%.10s<...>\n", hiddenHashes.get(i));
            System.out.println();

            System.out.println("Enrolling random j value...");
            BigInteger j = BigInteger.valueOf(new Random().nextInt(iterations));
            System.out.println("\tj=" + j);

            System.out.println("Sending j to Alice...");
            protocolManager.sendMessage(j.toByteArray());
            System.out.println("Done.");
            System.out.println("Awaiting messages and k's...");

            for(int i = 0; i < iterations; i++){
                if(i!=j.intValue())
                    messagesPlaintext.add(protocolManager.getMessageStr());
                else
                    messagesPlaintext.add("");
            }

            for(int i = 0; i < iterations; i++){
                if(i!=j.intValue())
                    ks.add(new BigInteger(1, protocolManager.getMessageBytes()));
                else
                    ks.add(BigInteger.ZERO);
            }

            System.out.println("Done\n");

            System.out.println("First few messages:");
            for(int i=0;i<5;i++){
                if(i != j.intValue())
                    System.out.printf("\t%.10s<...>\n", messagesPlaintext.get(i));
            }
            System.out.println();

            System.out.println("First k's:");
            for(int i=0;i<5;i++){
                if(i != j.intValue())
                    System.out.printf("\t%.10s<...>\n", ks.get(i));
            }
            System.out.println();

            ArrayList<BigInteger> selfCalculatedHashes = new ArrayList<>();
            System.out.println("Calculating hashes for every message...");
            for(int i = 0; i < iterations; i++){
                if(i != j.intValue())
                    selfCalculatedHashes.add(new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(messagesPlaintext.get(i).getBytes(StandardCharsets.UTF_8))));
            }

            System.out.println("First few hashes:");
            for(int i=0;i<5;i++){
                if(i != j.intValue())
                    System.out.printf("\t%.10s<...>\n", selfCalculatedHashes.get(i));
            }
            System.out.println();

            System.out.println("Calculating hashes using blinded hashes and k's...");
            for(int i = 0; i < iterations; i++){
                if(i != j.intValue())
                    messageHashes.add(hiddenHashes.get(i).multiply((ks.get(i).modPow(e, n)).modInverse(n)).mod(n));
            }

            System.out.println("First few hashes:");
            for(int i=0;i<5;i++){
                if(i != j.intValue())
                    System.out.printf("\t%.10s<...>\n", messageHashes.get(i));
            }

            System.out.println("Checking if calculated hashes match exact message hashes...");

            boolean allCorrect = false;
            for(int i=0;i<5;i++){
                if(i != j.intValue())
                    allCorrect = Arrays.equals(messageHashes.get(i).toByteArray(), selfCalculatedHashes.get(i).toByteArray());
            }

            System.out.println(allCorrect);

            System.out.printf("Blindly signing hidden hash number j=%d...\n", j);



            BigInteger s = hiddenHashes.get(j.intValue()).modPow(d, n);
            System.out.println("Signature is: " + s);

            System.out.println("Sending signature to Alice...");
            protocolManager.sendMessage(s.toByteArray());
            System.out.println("Done!");
            Thread.sleep(1000);
        } catch (IOException | NoSuchAlgorithmException | InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
