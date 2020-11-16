package Main;

import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

public class AESTest {
    @Test
    void AESBothSidesTest(){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // for example
            SecretKey secretKey = keyGen.generateKey();
            byte[] iv = new byte[cipher.getBlockSize()];
            SecureRandom randomSecureRandom = new SecureRandom();
            randomSecureRandom.nextBytes(iv);
            byte[] data = Utils.stringToBytes("ASDASDASDASD12esadasd ");
            byte[] encoded = AES.encrypt(data, secretKey, iv);
            byte[] decoded = AES.decrypt(encoded, secretKey, iv);
            assertArrayEquals(data, decoded);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }
}
