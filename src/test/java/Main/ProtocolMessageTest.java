package Main;

import org.junit.jupiter.api.Test;

import java.io.*;

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

    @Test
    public void byteToIntIntToByteTest(){
        int testedInt = 12512;
        byte[] out = ProtocolMessage.intToByte(testedInt);
        int result = ProtocolMessage.byteToInt(out);
        assertEquals(testedInt, result, "Integers not equal!");

    }

    @Test
    public void charToIntToCharTest(){
        int testedInt = 12512;
        char[] out = ProtocolMessage.intToCharArray(testedInt);
        int result = ProtocolMessage.charArrayToInt(out);
        assertEquals(testedInt, result, "Integers not equal!");
    }

    @Test
    public void inputReadingOutputWritingTest() throws IOException {
        ProtocolMessage message = new ProtocolMessage("This is a text");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(stream);
        message.sendToStream(writer);
        byte[] buffer = stream.toByteArray();

        ByteArrayInputStream istream = new ByteArrayInputStream(buffer);
        ProtocolMessage newMessage = new ProtocolMessage();
        Reader reader = new BufferedReader(new InputStreamReader(istream));
        newMessage.readFromStream(reader);
        assertArrayEquals(message.getRaw(), newMessage.getRaw());
    }
}
