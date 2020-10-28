package Main;

public class ProtocolMessage {
    int length;
    char[] message;

    char[] raw;

    /** Convert an integer into 4-byte array */
    private byte[] intToByte(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    /** Convert a byte array into 4-byte integer */
    private int byteToInt(byte[] bytes) {
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
        System.arraycopy(raw, 0, rawLength, 0, 4);
        System.arraycopy(raw, 4, this.message, 0, message.length);

        this.length = byteToInt(rawLength);
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
