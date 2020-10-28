package Main;

public class ProtocolMessage {
    int length;
    char[] message;

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
}
