package Main;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    void stringToHexHexToStringTest(){
        String text = "db6082d48965420538a75b1534f47194be418dff454e1112588c98857cf7b1412e66c49e18964eaf8f4a3cc9c88dbd32a8d2660d2fce2b27ad0b6eca7d9bd8e75b59e418fa0e1d6df462fa1170f690172f59fb24dae4d0c7b3cd7c32bd832d6809e9444c18d46d76331a0ea5d6de90a84df3419919a88f97d7d837d4deddb6b6";
        byte[] bytes = Utils.stringToBytes(text);
        String result = Utils.bytesToHex(bytes);
        assertEquals(text, result);
    }
}
