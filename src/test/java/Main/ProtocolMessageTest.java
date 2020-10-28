package Main;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProtocolMessageTest {

    @Test
    public void protocolBothWaysTest(){
        int length = 10;
        char[] msg = "1234567890".toCharArray();
        ProtocolMessage messageIn = new ProtocolMessage(length, msg);
        char[] data = messageIn.getRaw();
        ProtocolMessage messageOut = new ProtocolMessage(data);
        assertEquals(length, messageOut.getLength(), "Length not right.");
        assertArrayEquals(msg, messageOut.getMessage(), "Message not right.");

    }
}
