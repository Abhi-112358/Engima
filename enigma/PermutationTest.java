package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Abhiroop Mathur
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    Alphabet test1 = new Alphabet("AWIUBD");
    Permutation perm1 = new Permutation("(ABD) (WIU)", test1);
    Alphabet test2 = new Alphabet("AWIUBD");
    Permutation perm3 = new Permutation("(ABD) (WIU)", test2);
    @Test
    public void permuteTest() {

        assertEquals('B', perm3.permute('A'));
    }
    Alphabet test3 = new Alphabet("AWIUBDSGH");
    Permutation perm2 = new Permutation("(ABD) (WIU)", test2);
    @Test
    public void constructorTest() {
        assertEquals(3, perm1._cycles.size());
        assertEquals("ABD", perm1._cycles.get(1));
        assertEquals("WIU", perm1._cycles.get(2));
    }

    @Test
    public void addCyclesTest() {
        perm1.addCycle("(SGH)");
        assertEquals(4, perm1._cycles.size());
        assertEquals("SGH", perm1._cycles.get(3));
    }

    @Test
    public void permutemethodsTest() {
        assertEquals('B', perm1.permute('A'));
        assertEquals('I', perm1.permute('W'));
        assertEquals('W', perm1.permute('U'));

        assertEquals(2, perm1.permute(1));
        assertEquals(3, perm1.permute(2));
    }

    @Test
    public void invertmethodsTest() {
        assertEquals('A', perm1.invert('B'));
        assertEquals('W', perm1.invert('I'));
        assertEquals('U', perm1.invert('W'));

        assertEquals(1, perm1.invert(2));
        assertEquals(2, perm1.invert(3));
    }

    @Test
    public void derangementTest() {
        assertTrue(perm1.derangement());
        assertNotEquals(0, perm1.size());
    }

}
