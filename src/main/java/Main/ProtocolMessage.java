package Main;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class ProtocolMessage {
    private static final Logger logger = LogManager.getLogger(App.class);

    private int length;
    private char[] message;

    private char[] raw;

    /** Convert an integer into 4-byte array */
    public static byte[] intToByte(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    /** Convert a byte array into 4-byte integer */
    public static int byteToInt(byte[] bytes) {
        return bytes[0] << 24 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[2] & 0xFF) << 8 |
                (bytes[3] & 0xFF);
    }

    /** Convert an integer into 4-byte array */
    public static char[] intToCharArray(int value) {
        return new char[] {
                (char)(value >>> 24),
                (char)(value >>> 16),
                (char)(value >>> 8),
                (char)value};
    }

    /** Convert a byte array into 4-byte integer */
    public static int charArrayToInt(char[] bytes) {
        return bytes[0] << 24 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[2] & 0xFF) << 8 |
                (bytes[3] & 0xFF);
    }

    private void composeRawData(int length, char[] message){
        this.raw = new char[4 + length];
        byte[] integer = intToByte(length);

        this.raw[0] = (char)integer[0];
        this.raw[1] = (char)integer[1];
        this.raw[2] = (char)integer[2];
        this.raw[3] = (char)integer[3];

        System.arraycopy(message, 0, this.raw, 4, message.length);
    }

    public ProtocolMessage() {
    }

    public ProtocolMessage(String message) {
        this.length = message.length();
        this.message = message.toCharArray();

        composeRawData(this.length, this.message);
    }

    public ProtocolMessage(int length, char[] message) {
        this.length = length;
        this.message = message;

        composeRawData(length, message);
    }

    public ProtocolMessage(char[] raw) {
        this.raw = raw;

        deconstructPacket(raw);
    }

    private void deconstructPacket(char[] raw){
        byte[] rawLength = new byte[4];
        rawLength[0] = (byte)raw[0];
        rawLength[1] = (byte)raw[1];
        rawLength[2] = (byte)raw[2];
        rawLength[3] = (byte)raw[3];

        this.length = byteToInt(rawLength);

        this.message = new char[length];
        System.arraycopy(raw, 4, this.message, 0, message.length);

    }

    public void sendToStream(Writer writer) throws IOException {
        writer.write(this.raw);
        writer.flush();
    }

    public void readFromStream(Reader reader) throws IOException {
        int totalBytesRead = 0;
        char[] lenBuffer = new char[4];
        // Read first 4 bytes containing the length of the incoming message
        while(totalBytesRead != 4){
            int bytesRead = reader.read(lenBuffer, totalBytesRead, 4 - totalBytesRead);
            if (bytesRead == -1) {
                logger.log(Level.ERROR, "Invalid packet.");
                return;
            } else
                totalBytesRead += bytesRead;
        }

        int messageLength = charArrayToInt(lenBuffer);

        char[] buffer = new char[4 + messageLength];
        System.arraycopy(lenBuffer, 0, buffer, 0, totalBytesRead);
        while(totalBytesRead != 4 + messageLength){
            int bytesRead = reader.read(buffer, totalBytesRead, messageLength - totalBytesRead + 4);
            if (bytesRead == -1) {
                logger.log(Level.ERROR, "Invalid packet.");
                return;
            } else
                totalBytesRead += bytesRead;
        }
        this.raw = new char[totalBytesRead];
        System.arraycopy(buffer, 0, this.raw, 0, totalBytesRead);
        deconstructPacket(this.raw);

    }

    public int getLength() {
        return length;
    }

    public char[] getMessage() {
        return message;
    }

    public char[] getRaw() {
        return raw;
    }
}
