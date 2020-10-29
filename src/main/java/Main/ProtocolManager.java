package Main;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class ProtocolManager {
    Reader in;
    Writer out;

    public ProtocolManager(Reader in, Writer out) {
        this.in = in;
        this.out = out;
    }

    void sendMessage(String message) throws IOException {
        ProtocolMessage packet = new ProtocolMessage(message);
        packet.sendToStream(out);
    }

    void sendMessage(byte[] message) throws IOException {
        ProtocolMessage packet = new ProtocolMessage(Utils.bytesToHex(message));
        packet.sendToStream(out);
    }

    void sendMessage(char[] message) throws IOException {
        ProtocolMessage packet = new ProtocolMessage(new String(message));
        packet.sendToStream(out);
    }

    String getMessageStr() throws IOException {
        ProtocolMessage packet = new ProtocolMessage();
        packet.readFromStream(in);
        return new String(packet.getMessage());
    }

    byte[] getMessageBytes() throws IOException {
        ProtocolMessage packet = new ProtocolMessage();
        packet.readFromStream(in);
        return Utils.stringToBytes(String.valueOf(packet.getMessage()));
    }

    char[] getMessageChar() throws IOException {
        ProtocolMessage packet = new ProtocolMessage();
        packet.readFromStream(in);
        return packet.getMessage();
    }
}
