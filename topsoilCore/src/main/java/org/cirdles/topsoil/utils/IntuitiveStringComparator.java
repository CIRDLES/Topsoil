/* Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.topsoil.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * <p>
 * A comparator that emulates the "intuitive" sorting used by Windows
 * Explorer. The rules are as follows:</p>
 * <ul>
 * <li>Any sequence of one or more digits is treated as an atomic
 * unit, a number. When these number units are matched up, they're compared
 * according to their respective numeric values. If they're numerically
 * equal, but one has more leading zeroes than the other, the longer
 * sequence is considered larger.</li>
 * <li>Numbers always sort before any other kind of character.</li>
 * <li>Spaces and all punctuation characters always sort before
 * letters.</li>
 * <li>Letters are sorted case-insensitively.</li>
 * </ul>
 * <p>
 * Explorer's sort order for punctuation characters is not quite the same
 * as their ASCII order. Also, some characters aren't allowed in file names,
 * so I don't know how they would be sorted. This class just sorts them all
 * according to their ASCII values.</p>
 * <p>
 * This comparator is only guaranteed to work with 7-bit ASCII strings.</p>
 *
 * @author Alan Moore 23a. * repaired by James F. Bowring 2010 24.
 *
 * @param <T>   type of CharSequence to compare
 */
public class IntuitiveStringComparator<T extends CharSequence>
        implements Comparator<T>, Serializable {

    private static final long serialVersionUID = 7971843528648376464L;

    private T str1, str2;
    private int pos1, pos2, len1, len2;

    public int compare(T s1, T s2) {
        str1 = s1;
        str2 = s2;
        len1 = str1.length();
        len2 = str2.length();
        pos1 = pos2 = 0;

        // may 2011 to handle identical strings immediately
        if (s1.toString().trim().equalsIgnoreCase(s2.toString().trim())) {
            return 0;
        }

        if (len1 == 0) {
            return len2 == 0 ? 0 : -1;
        } else if (len2 == 0) {
            return 1;
        }

        while ((pos1 < len1) && (pos2 < len2)) {
            char ch1 = str1.charAt(pos1);
            char ch2 = str2.charAt(pos2);
            int result = 0;

            if (Character.isDigit(ch1)) {
                result = Character.isDigit(ch2) ? compareNumbers() : -1;
            } else if (Character.isLetter(ch1)) {
                result = Character.isLetter(ch2) ? compareOther(true) : 1;
            } else {
                result = Character.isDigit(ch2) ? 1
                        : Character.isLetter(ch2) ? -1
                        : compareOther(false);
            }

            if (result != 0) {
                return result;
            }

        }

        return len1 - len2;
    }

    // jan 2015
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.str1);
        hash = 79 * hash + Objects.hashCode(this.str2);
        return hash;
    }

    private int compareNumbers() {
        int delta = 0;

        int zeroes1 = 0, zeroes2 = 0;

        char ch1 = (char) 0, ch2 = (char) 0;

        // Skip leading zeroes, but keep a count of them.
        while (pos1 < len1 && (ch1 = str1.charAt(pos1++)) == '0') {
            zeroes1++;

        }
        while (pos2 < len2 && (ch2 = str2.charAt(pos2++)) == '0') {
            zeroes2++;

        }

        // If one sequence contains more significant digits than the
        // other, it's a larger number.  In case they turn out to have
        // equal lengths, we compare digits at each position; the first
        // unequal pair determines which is the bigger number.
        while (true) {
            boolean noMoreDigits1 = (ch1 == 0) || !Character.isDigit(ch1);

            boolean noMoreDigits2 = (ch2 == 0) || !Character.isDigit(ch2);

            if (noMoreDigits1 && noMoreDigits2) {

                // bowring to handle case of other after identical strings ending in numbers
                pos1--;
                pos2--;

                return delta != 0 ? delta : zeroes1 - zeroes2;

            } else if (noMoreDigits1) {
                return -1;

            } else if (noMoreDigits2) {
                return 1;

            } else if (delta == 0 && ch1 != ch2) {
                delta = ch1 - ch2;

            }

            ch1 = pos1 < len1 ? str1.charAt(pos1++) : (char) 0;

            ch2 = pos2 < len2 ? str2.charAt(pos2++) : (char) 0;

        }
    }

    private int compareOther(boolean isLetters) {
        char ch1 = str1.charAt(pos1++);

        char ch2 = str2.charAt(pos2++);

        if (ch1 == ch2) {
            return 0;

        }

        if (isLetters) {
            ch1 = Character.toUpperCase(ch1);

            ch2 = Character.toUpperCase(ch2);

            if (ch1 != ch2) {
                ch1 = Character.toLowerCase(ch1);

                ch2 = Character.toLowerCase(ch2);

            }
        }

        return ch1 - ch2;

    }

    public static void main(String[] args) {
        String[] list = {
                "1z1", "1z2", "1z14",
                "1d", "1c",
                "1b",
                "foo 03",
                "foo 00003",
                "foo 5",
                "foo 003",
                "foo~03",
                "foo 10far",
                "foo 10boo",
                "foo 10bar",
                "foo 10",
                "foo!03"
        };

        Arrays.sort(list, new IntuitiveStringComparator<String>());

        for (String s : list) {
            //System.out.println(s);

        }
    }
}