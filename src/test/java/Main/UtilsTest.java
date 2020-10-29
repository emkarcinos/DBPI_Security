package Main;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    void stringToHexHexToStringTest(){
        String text = "6f1a";
        byte[] bytes = Utils.stringToBytes(text);
        String result = Utils.bytesToHex(bytes);
        assertEquals(text, result);
    }
}
