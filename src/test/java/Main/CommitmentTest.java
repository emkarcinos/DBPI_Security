package Main;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommitmentTest {

    @Test
    public void CommitmentHashingTest(){
        Commitment alice = new Commitment("Test");
        Commitment bob = new Commitment(alice.getSequenceA(), alice.getHash());
        bob.setSequenceB(alice.getSequenceB());
        bob.setMessage(alice.getMessage());
        assertTrue(bob.checkHash());
    }
}
