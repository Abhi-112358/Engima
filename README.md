Enigma

Source/Skeleton Code: UC Berkeley's CS61B: Data Structures

This project is based on the historical Enigma machine used by the German military during World War II to encrypt their communications.
You can input a message, and based on the chosen configuration file, Enigma will go through and encrypt each character through a series of rotors, much like the physical rotors in the machine of WWII.


This projects runs on Java 17.

To run:
1. Clone Repository.
2. Look through testing/correct and pick out a conf file to use as the configuration of the rotors.
3. Pick out a input in testing/correct, or make your own input that matches the format of the rest.
4. Choose a file as an output file.
5. In terminal, run java -ea enigma.Main [configuration file] [input file] [output file]
