package Main;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

public class DSARSAExchangeTest {

    @Test
    void RSAEncryptDecryptTest(){
        KeyPair keys = DSARSAExchange.generateKeyPair("RSA");
        String message = "Test";
        byte[] encrypted = DSARSAExchange.encryptUsingRSA(message, keys.getPublic());
        String decrypted = DSARSAExchange.decryptUsingRSA(encrypted, keys.getPrivate());
        assertEquals(message, decrypted);
    }

    @Test
    void DSASigningVerifyingTest(){
        KeyPair keys = DSARSAExchange.generateKeyPair("DSA");
        String message = "Test";
        byte[] signature = DSARSAExchange.signWithDSA(message, keys.getPrivate());
        assertTrue(DSARSAExchange.verifyDSASignature(message, signature, keys.getPublic()));
    }

}
