package Main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DHExchangeTest {

    @Test
    void KeyMatchAfterExchangeTest(){
        DHExchange alice = new DHExchange(2048, true);
        DHExchange bob = new DHExchange(2048, false);
        try {
            bob.createKeyParFromPublicKey(alice.getThisPublicKeyEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        alice.onRecievePubKey(bob.getThisPublicKeyEncoded());

        assertEquals(bob.getSecretKey(), alice.getSecretKey());
    }
}
