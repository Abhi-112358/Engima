package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Abhiroop Mathur
 */
public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTS ***** */

    private static final Alphabet AZ = new Alphabet(TestUtils.UPPER_STRING);

    private static final HashMap<String, Rotor> ROTORS = new HashMap<>();

    static {
        HashMap<String, String> nav = TestUtils.NAVALA;
        ROTORS.put("B", new Reflector("B", new Permutation(nav.get("B"), AZ)));
        ROTORS.put("Beta",
                new FixedRotor("Beta",
                        new Permutation(nav.get("Beta"), AZ)));
        ROTORS.put("III",
                new MovingRotor("III",
                        new Permutation(nav.get("III"), AZ), "V"));
        ROTORS.put("IV",
                new MovingRotor("IV", new Permutation(nav.get("IV"), AZ),
                        "J"));
        ROTORS.put("I",
                new MovingRotor("I", new Permutation(nav.get("I"), AZ),
                        "Q"));
    }

    private static final String[] ROTORS1 = { "B", "Beta", "III", "IV", "I" };
    private static final String SETTING1 = "AXLE";

    private Machine mach1() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        mach.setRotors(SETTING1);
        return mach;
    }

    @Test
    public void testInsertRotors() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        assertEquals(5, mach.numRotors());
        assertEquals(3, mach.numPawls());
        assertEquals(AZ, mach.alphabet());
        assertEquals(ROTORS.get("B"), mach.getRotor(0));
        assertEquals(ROTORS.get("Beta"), mach.getRotor(1));
        assertEquals(ROTORS.get("III"), mach.getRotor(2));
        assertEquals(ROTORS.get("IV"), mach.getRotor(3));
        assertEquals(ROTORS.get("I"), mach.getRotor(4));
    }

    @Test
    public void testConvertChar() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(YF) (HZ)", AZ));
        assertEquals(25, mach.convert(24));
    }

    @Test
    public void testConvertMsg() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", AZ));
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                mach.convert("FROMHISSHOULDERHIAWATHA"));
    }

    @Test
    public void advanceRotorsTest() {
        Alphabet test = new Alphabet("ABC");
        ArrayList<Rotor> rotors = new ArrayList<>();
        rotors.add(new FixedRotor("F", new Permutation("(ABD)", test)));
        rotors.add(new MovingRotor(
            "III", new Permutation("(ABC)", test), "C"));
        rotors.add(new MovingRotor("II", new Permutation("(ABC)", test), "C"));
        rotors.add(new MovingRotor("I", new Permutation("(ABC)", test), "C"));
        Machine mach2 = new Machine(test, 4, 3, rotors);
        String[] rotors2 = {"F", "III", "II", "I"};
        mach2.insertRotors(rotors2);

        mach2.advanceRotors();
        assertEquals(0, mach2.getRotor(0).setting());
        assertEquals(0, mach2.getRotor(1).setting());
        assertEquals(0, mach2.getRotor(2).setting());
        assertEquals(1, mach2.getRotor(3).setting());

        for (int i = 0; i < 4; i++) {
            mach2.advanceRotors();
        }

        assertEquals(0, mach2.getRotor(0).setting());
        assertEquals(0, mach2.getRotor(1).setting());
        assertEquals(1, mach2.getRotor(2).setting());
        assertEquals(2, mach2.getRotor(3).setting());

        for (int i = 0; i < 5; i++) {
            mach2.advanceRotors();
        }

        assertEquals(0, mach2.getRotor(0).setting());
        assertEquals(1, mach2.getRotor(1).setting());
        assertEquals(1, mach2.getRotor(2).setting());
        assertEquals(1, mach2.getRotor(3).setting());

        for (int i = 0; i < 5; i++) {
            mach2.advanceRotors();
        }

        assertEquals(0, mach2.getRotor(0).setting());
        assertEquals(2, mach2.getRotor(1).setting());
        assertEquals(1, mach2.getRotor(2).setting());
        assertEquals(0, mach2.getRotor(3).setting());

        for (int i = 0; i < 5; i++) {
            mach2.advanceRotors();
        }

        assertEquals(0, mach2.getRotor(0).setting());
        assertEquals(0, mach2.getRotor(1).setting());
        assertEquals(0, mach2.getRotor(2).setting());
        assertEquals(2, mach2.getRotor(3).setting());

    }

    @Test
    public void loginTest() {
        Alphabet test2 = new Alphabet("abcdefghijklmnopqrstuvwxyz");
        ArrayList<Rotor> rotors = new ArrayList<>();
        rotors.add(new Reflector("F", new Permutation("(az) (by) (cx) (dw) "
            + "(ev) (fu) (gt) (hs) (ir) (jq) (kp) (lo) (mn)", test2)));
        rotors.add(new MovingRotor("III", new Permutation(
            "(quack)(froze)(twins) (glyph)", test2), "m"));
        rotors.add(new MovingRotor("II", new Permutation(
            "(tears) (boing) (lucky)", test2), "b"));
        rotors.add(new MovingRotor("I", new Permutation(
            "(wordle) (is) (fun)", test2), "a"));
        Machine mach2 = new Machine(test2, 4, 3, rotors);
        String[] rotors2 = {"F", "III", "II", "I"};
        mach2.insertRotors(rotors2);
        mach2.setPlugboard(new Permutation("(az) (mn)", test2));
        mach2.setRotors("maa");
        assertEquals("woj", mach2.convert("ajw"));

    }
}
