/*
 * Copyright (c)  Sofun Gaming SAS.
 * Copyright (c)  Julien Anguenot <julien@anguenot.org>
 * Copyright (c)  Julien De Preaumont <juliendepreaumont@gmail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Julien Anguenot <julien@anguenot.org> - initial API and implementation
*/

package org.sofun.core.api;

import java.security.SecureRandom;

/**
 * Core Password Generator.
 * 
 * <p/>
 * 
 * Password should meet at least the following to validate:
 * 
 * <ul>
 * <li>Password must be at least 8 characters long</li>
 * <li>First character must be an upper case letter</li>
 * <li>Last character must be number</li>
 * <li>No character repetition allowed</li>
 * </ul>
 * 
 * <p/>
 * 
 * Although, symbols are not mandatory for a password to validate we do include symbols at password generation time.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class PasswordGenerator {

    private static final int PASSWORD_LENGTH = 5;

    private static final int REPETITION = 0;

    protected static final char[] NUMBERS_AND_LETTERS_ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', };

    protected static final char[] SYMBOLS_ALPHABET = { '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.',
            '/', ':', ';', '<', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~', };

    private static final char[] PRINTABLE_ALPHABET = { '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.',
            '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^',
            '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', };

    protected static final char[] LOWERCASE_LETTERS_ALPHABET = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', };

    protected static final char[] LOWERCASE_LETTERS_AND_NUMBERS_ALPHABET = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', };

    protected static final char[] LETTERS_ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', };

    private static final char[] UPPERCASE_LETTERS_ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', };

    protected static final char[] NONCONFUSING_ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P',
            'Q', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r',
            's', 't', 'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9', };

    private static final char[] NUMBER_ALPHABET = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', };

    private SecureRandom random;

    private char[] alphabet;

    private char[] firstAlphabet;

    private char[] lastAlphabet;

    public PasswordGenerator() {
        this(new SecureRandom(), PRINTABLE_ALPHABET);
    }

    protected PasswordGenerator(SecureRandom rand, char[] alphabet) {
        this.random = rand;
        this.alphabet = alphabet;
    }

    /**
     * Generates a new password.
     * 
     * See class java doc above for more information.
     * 
     * @return a clear text password.
     */
    public static final String generate() {
        PasswordGenerator generator = new PasswordGenerator();
        generator.setAlphabet(PRINTABLE_ALPHABET);
        generator.setFirstAlphabet(UPPERCASE_LETTERS_ALPHABET);
        generator.setLastAlphabet(NUMBER_ALPHABET);
        return generator.getPass(PASSWORD_LENGTH);
    }

    protected void setAlphabet(char[] alphabet) {
        if (alphabet == null)
            throw new NullPointerException("Null alphabet");
        if (alphabet.length == 0)
            throw new ArrayIndexOutOfBoundsException("No characters in alphabet");
        this.alphabet = alphabet;
    }

    protected void setRandomGenerator(SecureRandom rand) {
        this.random = rand;
    }

    protected void setFirstAlphabet(char[] alphabet) {
        if (alphabet == null || alphabet.length == 0) {
            this.firstAlphabet = null;
        } else {
            this.firstAlphabet = alphabet;
        }
    }

    protected void setLastAlphabet(char[] alphabet) {
        if (alphabet == null || alphabet.length == 0) {
            this.lastAlphabet = null;
        } else {
            this.lastAlphabet = alphabet;
        }
    }

    protected char[] getPassChars(char[] pass) {
        boolean verified = false;
        while (!verified) {
            int length = pass.length;
            for (int i = 0; i < length; i++) {
                char[] useAlph = alphabet;
                if (i == 0 && firstAlphabet != null) {
                    useAlph = firstAlphabet;
                } else if (i == length - 1 && lastAlphabet != null) {
                    useAlph = lastAlphabet;
                }
                int size = avoidRepetition(useAlph, pass, i, REPETITION, useAlph.length);
                pass[i] = useAlph[random.nextInt(size)];
            }
            verified = true;
        }
        return (pass);
    }

    private static int avoidRepetition(char[] alph, char[] pass, int passSize, int repetition, int alphSize) {
        if (repetition > -1) {
            // limit the alphabet to those characters that
            // will not cause repeating sequences
            int repPos = 0;
            while ((repPos = findRep(pass, repPos, passSize, repetition)) != -1) {
                // shuffle characters that would cause repetition
                // to the end of the alphabet and adjust the size
                // so that they will not be used.
                alphSize -= moveto(alph, alphSize, pass[repPos + repetition]);
                repPos++;
            }
            if (alphSize == 0)
                alphSize = alph.length;
        }
        return alphSize;
    }

    private static int findRep(char[] pass, int start, int end, int length) {
        for (int i = start; i < end - length; i++) {
            boolean onTrack = true;
            for (int j = 0; onTrack && j < length; j++) {
                if (pass[i + j] != pass[end - length + j])
                    onTrack = false;
            }
            if (onTrack)
                return i;
        }
        return -1;
    }

    private static int moveto(char[] alph, int numGood, char c) {
        int count = 0;
        for (int i = 0; i < numGood; i++) {
            if (alph[i] == c) {
                numGood--;
                char temp = alph[numGood];
                alph[numGood] = alph[i];
                alph[i] = temp;
                count++;
            }
        }
        return count;
    }

    private String getPass(int length) {
        return (new String(getPassChars(new char[length])));
    }

    /**
     * Verifies a clear text password.
     * 
     * See class java doc above for more informatioN.
     * 
     * @param password: a clear text password.
     * @return true or false.
     */
    public static final boolean verify(String password) {
        if (!(password.length() >= PASSWORD_LENGTH)) {
            return false;
        }
        return true;
    }

    protected static boolean contains(char[] array, char data) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == data) {
                return true;
            }
        }
        return false;
    }
    
}
