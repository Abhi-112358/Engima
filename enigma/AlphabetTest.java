package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

public class AlphabetTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    Alphabet test = new Alphabet("ABCD");
    Alphabet test1 = new Alphabet("aABCd");
    @Test
    public void sizeTest() {
        int result = test.size();

        assertEquals(4, result);
    }

    @Test
    public void containsTest() {
        boolean result1 = test.contains('A');
        boolean result2 = test.contains('a');
        boolean result3 = test1.contains('A');
        boolean result4 = test1.contains('a');
        boolean result5 = test.contains('.');


        assertTrue(result1);
        assertFalse(result2);
        assertTrue(result3);
        assertTrue(result4);
        assertFalse(result5);
    }


    String testString = ".-o";
    Alphabet test2 = new Alphabet(testString);
    @Test
    public void toCharTest() {
        for (int i = 0; i < test2.size(); i++) {
            char curr = testString.charAt(i);
            assertEquals(curr, test2.toChar(i));

        }
    }

    @Test
    public void toIntTest() {
        for (int i = 0; i < test2.size(); i++) {
            assertEquals(i, test2.toInt(test2.toChar(i)));
        }
    }
}
