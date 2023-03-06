package enigma;

import java.util.ArrayList;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Abhiroop Mathur
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;

        _cycles = new ArrayList<>();

        cycles = cycles.replace("(", " ").replace(")", " ");

        String[] tempcycle = cycles.split("\\s+");

        for (String i: tempcycle) {

            _cycles.add(i);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    public void addCycle(String cycle) {
        cycle = cycle.replace("(", "").replace(")", "");

        _cycles.add(cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);

        char charat = _alphabet.toChar(p);

        return _alphabet.toInt(permute(charat));

    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);

        char charat = _alphabet.toChar(c);

        return _alphabet.toInt(invert(charat));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        for (String i : _cycles) {
            if (i.contains((Character.toString(p)))) {
                if (i.indexOf(p) + 1 == i.length()) {

                    return i.charAt(0);

                }

                return i.charAt(i.indexOf(Character.toString(p)) + 1);
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        for (String i : _cycles) {
            if (i.contains((Character.toString(c)))) {
                if (i.indexOf(c) == 0) {
                    return i.charAt(i.length() - 1);
                }
                return i.charAt(i.indexOf(Character.toString(c)) - 1);
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (String i : _cycles) {
            if (i.length() == 1) {
                return false;
            }

        }

        ArrayList<String> copy = (ArrayList<String>) _alphabet._chars.clone();
        for (String j : _cycles) {
            for (int k = 0; k < j.length(); k++) {
                if (copy.contains(Character.toString(j.charAt(k)))) {
                    copy.remove(Character.toString(j.charAt(k)));
                }
            }
        }

        System.out.println(copy.size());
        System.out.println(_alphabet.size());
        if (copy.size() == 0) {
            return true;
        }
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Collection of cycles that determine the permutation. */
    protected ArrayList<String> _cycles;
}
