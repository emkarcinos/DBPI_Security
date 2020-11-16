package Main;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

public class DHInteractive {
    ProtocolManager protocolManager;

    public DHInteractive(Reader in, Writer out) {
        this.protocolManager = new ProtocolManager(in, out);
    }

    void beginSenderInteractive(){
        System.out.println("Starting DH key exchange...");
        DHExchange dhExchange = new DHExchange(2048, true);
        System.out.println("Your public key is: " + Utils.bytesToHex(dhExchange.getThisPublicKeyEncoded()));
        System.out.println("Sending this public key to the other side...");
        try {
            protocolManager.sendMessage(dhExchange.getThisPublicKeyEncoded());
            System.out.println("Key sent. Waiting for other sides public key...");

            byte[] recvPKey = protocolManager.getMessageBytes();
            System.out.println("Got key: " + Utils.bytesToHex(recvPKey));

            System.out.println("Final agreement phase...");
            dhExchange.onRecievePubKey(recvPKey);
            SecretKeySpec secret = dhExchange.getSecretKey();
            System.out.println("Secret is: " + Utils.bytesToHex(secret.getEncoded()));

            System.out.println("Generating IV vector for AES ciphering...");

            byte[] iv = AES.generateIv();

            System.out.println("IV is: " + Utils.bytesToHex(iv));

            // Sending IV as plaintext
            protocolManager.sendMessage(iv);

            System.out.println("Specify a file to send");
            System.out.print("Path: ");

            Scanner scanner = new Scanner(System.in);
            String fPath = scanner.nextLine();

            File file = new File(fPath);
            long fSize = file.length();
            System.out.printf("File: %s, size: %.4fkB\n", file.getName(), (double)fSize / 1000);
            byte[] fBytes = Files.readAllBytes(file.toPath());

            MessageDigest md5digest = MessageDigest.getInstance("MD5");
            md5digest.update(fBytes);
            byte[] checksum = md5digest.digest();
            System.out.println("MD5 checksum: " + Utils.bytesToHex(checksum));

            System.out.println("Encrypting...");

            byte[] encryptedFile = AES.encrypt(fBytes, secret, iv);
            byte[] encryptedFileName = AES.encrypt(file.getName().getBytes(), secret, iv);
            System.out.println("Sending encrypted file...");
            protocolManager.sendMessage(checksum);
            protocolManager.sendMessage(encryptedFileName);
            protocolManager.sendMessage(encryptedFile);

            System.out.println("Done!");
            try {
                Thread.sleep(200000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("File sent.");
        } catch (IOException | NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }

    }

    void beginReceiverInteractive(){
        System.out.println("Starting DH key exchange...");
        DHExchange dhExchange = new DHExchange(2048, false);
        try {
            System.out.println("Waiting for other sides public key...");

            byte[] recvPKey = protocolManager.getMessageBytes();
            System.out.println("Got key: " + Utils.bytesToHex(recvPKey));
            dhExchange.createKeyParFromPublicKey(recvPKey);

            System.out.println("Your public key is: " + Utils.bytesToHex(dhExchange.getThisPublicKeyEncoded()));
            System.out.println("Sending the key to the other side...");
            protocolManager.sendMessage(dhExchange.getThisPublicKeyEncoded());

            SecretKeySpec secret = dhExchange.getSecretKey();
            System.out.println("Secret is: " + Utils.bytesToHex(secret.getEncoded()));

            System.out.println("Waiting for IV...");
            byte[] iv = protocolManager.getMessageBytes();
            System.out.println("IV is: " + Utils.bytesToHex(iv));

            System.out.println("Waiting for encrypted file...");

            byte[] checksum = protocolManager.getMessageBytes();
            byte[] encryptedFileName = protocolManager.getMessageBytes();
            byte[] encryptedFile = protocolManager.getMessageBytes();

            System.out.println("Decrypting...");
            String decryptedFileName = new String(AES.decrypt(encryptedFileName, secret, iv));
            byte[] decryptedFile = AES.decrypt(encryptedFile, secret, iv);

            System.out.println("Filename: " + decryptedFileName);
            File recievedFile = new File(decryptedFileName);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(recievedFile));
            stream.write(decryptedFile);
            stream.flush();

            System.out.println("File saved in " + recievedFile.getAbsolutePath());

            System.out.println("Checking checksums...");
            System.out.println("Expected: " + Utils.bytesToHex(checksum));

            byte[] fBytes = Files.readAllBytes(recievedFile.toPath());

            MessageDigest md5digest = MessageDigest.getInstance("MD5");
            md5digest.update(fBytes);
            byte[] recvChecksum = md5digest.digest();
            System.out.println("MD5 checksum: " + Utils.bytesToHex(recvChecksum));

            if(Arrays.equals(recvChecksum, checksum))
                System.out.println("Checksum match positive.");
            else
                System.out.println("Checksum match negative! File damaged.");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void beginLoopAs(boolean isHost){
        // Host (the one who starts the app first) is Alice - ciphers the message.
        if(isHost)
            beginSenderInteractive();
        else
            beginReceiverInteractive();
    }
}
