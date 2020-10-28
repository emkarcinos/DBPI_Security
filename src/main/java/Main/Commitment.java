package Main;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class Commitment {
    private static final Logger logger = LogManager.getLogger(App.class);

    final private int randomSequenceByteCount = 128;

    private String message;
    private byte[] sequenceA;
    private byte[] sequenceB;
    private byte[] hash;

    private byte[] generateRandomSequence(){
        byte[] result = new byte[randomSequenceByteCount];
        Random r = new Random();
        r.nextBytes(result);
        return result;
    }

    public Commitment(String message) {
        logger.log(Level.INFO, "Creating commitment...");
        this.message = message;

        this.sequenceA = generateRandomSequence();
        logger.log(Level.INFO, "Random sequence A is " + Arrays.toString(this.sequenceA));
        this.sequenceB = generateRandomSequence();
        logger.log(Level.INFO, "Random sequence B is " + Arrays.toString(this.sequenceB));

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(this.sequenceA);
            digest.update(this.sequenceB);
            digest.update(this.message.getBytes(StandardCharsets.UTF_8));

            this.hash = digest.digest();
            logger.log(Level.INFO, "Hash is " + Arrays.toString(this.hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Commitment() {
    }

    public boolean checkHash() {
        boolean result = false;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(this.sequenceA);
            digest.update(this.sequenceB);
            digest.update(this.message.getBytes(StandardCharsets.UTF_8));

            result = this.hash == digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getMessage() {
        return message;
    }

    public byte[] getSequenceA() {
        return sequenceA;
    }

    public byte[] getSequenceB() {
        return sequenceB;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSequenceB(byte[] sequenceB) {
        this.sequenceB = sequenceB;
    }

}
