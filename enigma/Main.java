package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Scanner;

import java.util.List;

import java.util.Set;

import java.util.HashSet;

import ucb.util.CommandArgs;

import java.util.NoSuchElementException;

import java.util.ArrayList;

import java.util.Arrays;

import java.util.Collection;

import static enigma.EnigmaException.*;
import static java.util.Arrays.copyOfRange;

/** Enigma simulator.
 *  @author Abhiroop Mathur
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine mach = readConfig();
        String setting = _input.nextLine();
        if (!Character.toString(setting.charAt(0)).equals("*")) {
            throw new EnigmaException("Input does not start with a setting!");
        }
        setUp(mach, setting);
        if (!_input.hasNextLine()) {
            _output.print("");
        }

        while (_input.hasNextLine()) {
            if (!_input.hasNext()) {
                break;
            }
            String nextormsg = _input.nextLine();
            int space = 0;
            while (nextormsg.isEmpty()) {
                if (_input.hasNextLine()) {
                    nextormsg = _input.nextLine();
                    space++;
                }
            }
            while (Character.toString(nextormsg.charAt(0)).equals("*")) {
                setUp(mach, nextormsg);
                if (!_input.hasNextLine()) {
                    return;
                }
                nextormsg = _input.nextLine();
            }

            for (int i = 0; i < space; i++) {
                _output.println();
            }
            nextormsg = nextormsg.replace(" ", "");
            for (int i = 0; i
                < nextormsg.length(); i++) {
                if (!_alphabet.contains((nextormsg.charAt(i)))) {
                    throw new EnigmaException("Message contains "
                        + "characters outside of the alphabet!");
                }
            }
            String print = mach.convert(nextormsg);
            printMessageLine(print);
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        Collection<Rotor> therotors = new ArrayList<>();
        try {
            _alphabet = new Alphabet(_config.next());
            int rotorcount = _config.nextInt();
            int pawlcount = _config.nextInt();
            _config.nextLine();
            while (_config.hasNextLine()) {
                String line = _config.nextLine();
                if (!line.isBlank()) {
                    therotors.add(readRotor(line));
                }
            }
            return new Machine(_alphabet, rotorcount, pawlcount, therotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config.
     * @param line *read of _config.nextLine()s*
     */
    Rotor readRotor(String line) {
        try {
            String rotorline = line;
            String [] rotorlist = rotorline.split("\\s+");
            if (rotorlist[0].isEmpty()) {
                String[] rotorlst = new String[rotorlist.length - 1];
                System.arraycopy(rotorlist, 1, rotorlst,
                        0, rotorlist.length - 1);
                rotorlist = rotorlst;
            }
            String name = rotorlist[0];
            String type = rotorlist[1].substring(0, 1);
            String notchesofthisrotor = rotorlist[1].substring(1);
            String cycles = "";
            for (int i = 2; i < rotorlist.length; i++) {
                cycles += rotorlist[i] + " ";
            }


            if (_config.hasNext("\\([a-zA-Z._]+\\)")) {
                String rest = _config.nextLine();

                cycles += rest;
            }


            if (type.equals("M")) {
                return new MovingRotor(name, new Permutation(cycles, _alphabet),
                        notchesofthisrotor);
            } else if (type.equals("N")) {
                return new FixedRotor(name, new Permutation(cycles, _alphabet));
            }

            return new Reflector(name, new Permutation(cycles, _alphabet));
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] sEttings = settings.split(" ");
        if (sEttings.length < M.numRotors() + 2) {
            throw new EnigmaException("Wrong number of arguments in setting!");
        }

        String[] rOtors = copyOfRange(sEttings, 1, M.numRotors() + 1);
        List rotors = Arrays.asList(rOtors);
        Set checkdup = new HashSet(rotors);
        if (checkdup.size() < rotors.size()) {
            throw new EnigmaException("Duplicate Rotor!");
        }

        M.insertRotors(rOtors);
        if (!(M.getRotor(0) instanceof Reflector)) {
            throw new EnigmaException("First rotor not a reflector!");
        }
        for (int i = 0; i < sEttings[M.numRotors() + 1].length(); i++) {
            if (!_alphabet.contains((sEttings[M.numRotors() + 1].charAt(i)))) {
                throw new EnigmaException("Settings contain "
                    + "characters outside of the alphabet!");
            }
        }
        M.setRotors(sEttings[M.numRotors() + 1]);

        String cycles = "";
        for (int i = M.numRotors() + 2; i < sEttings.length; i++) {
            cycles += sEttings[i] + " ";
        }
        M.setPlugboard(new Permutation(cycles, _alphabet));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        _output.print(msg.charAt(0));
        for (int i = 1; i < msg.length(); i++) {
            if (i % 5 != 0) {
                _output.print(msg.charAt(i));
            } else {
                _output.print(" ");
                _output.print(msg.charAt(i));
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
