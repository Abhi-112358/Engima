package enigma;

import java.util.ArrayList;

import java.util.Collection;

/** Class that represents a complete enigma machine.
 *  @author Abhiroop Mathur
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors.toArray();
        _theseRotors = new ArrayList<>();
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _theseRotors.get(k);
    }

    /** Returns Alphabet. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _theseRotors.clear();
        for (String i: rotors) {
            for (Object j: _allRotors) {
                if (((Rotor) j).name().equals(i)) {
                    _theseRotors.add((Rotor) j);
                }

            }

        }
        if (_theseRotors.size() != rotors.length) {
            throw new EnigmaException("Rotors not found or misnamed!");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _theseRotors.size() - 1) {
            throw new EnigmaException("Settings string wrong length!");
        }

        int j = 0;
        for (int i = 1; i < _theseRotors.size(); i++) {
            _theseRotors.get(i).set(setting.charAt(j));
            j++;
        }


    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    public void advanceRotors() {
        for (int i = numRotors() - numPawls();
             i < _theseRotors.size() - 1; i++) {
            if (i > numRotors() - numPawls()) {
                if (_theseRotors.get(i).atNotch()) {
                    _theseRotors.get(i).advance();
                } else {
                    if (_theseRotors.get(i + 1).atNotch()) {
                        _theseRotors.get(i).advance();
                    }
                }
            } else {
                if (_theseRotors.get(i + 1).atNotch()) {
                    _theseRotors.get(i).advance();
                }
            }
        }
        _theseRotors.get(_theseRotors.size() - 1).advance();
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        for (int i = _theseRotors.size() - 1; i > 0; i--) {
            c = _theseRotors.get(i).convertForward(c);
        }
        c = _theseRotors.get(0).permutation().permute(c);
        for (Rotor i: _theseRotors.subList(1, _theseRotors.size())) {
            c = i.convertBackward(c);
        }

        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replace(" ", "");
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            result += _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots. */
    private int _numRotors;

    /** Number of pawls. */
    private int _pawls;

    /** List of all possible rotors in the configuration. */
    private Object[] _allRotors;

    /** Plugboard permutation. */
    private Permutation _plugboard;

    /** Rotors of the input. */
    private ArrayList<Rotor> _theseRotors;

}
