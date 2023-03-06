package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;
/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Abhiroop Mathur
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = new ArrayList<>();
        for (int i = 0; i < chars.length(); i++) {
            if (_chars.contains(Character.toString((chars.charAt(i))))) {
                continue;
            }
            _chars.add(Character.toString(chars.charAt(i)));
        }


    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.size();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _chars.contains(Character.toString(ch));
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        try {
            return _chars.get(index).charAt(0);
        } catch (IndexOutOfBoundsException EnigmaException) {
            throw error("Index out of bounds!");
        }
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return _chars.indexOf(Character.toString(ch));
    }

    /** Characters. */
    protected ArrayList<String> _chars;


}
