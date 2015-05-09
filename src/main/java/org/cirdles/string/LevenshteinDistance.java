/*
 * Copyright 2015 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.string;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Levenshtein distance between two words is the minimum number of
 * single-character edits (i.e. insertions, deletions or substitutions) required
 * to change one word into the other. (Wikipedia)
 *
 * @author John Zeringue
 */
public class LevenshteinDistance {

    private final String a;
    private final String b;

    /**
     * Creates a new {@code LevensheinDistance} for the strings {@code a} and
     * {@code b}.
     * 
     * @param a the first string to be compared
     * @param b the second string to be compared
     */
    public LevenshteinDistance(String a, String b) {
        this.a = a;
        this.b = b;
    }

    static int indicator(boolean condition) {
        return condition ? 1 : 0;
    }

    static int minimum(int... integers) {
        int result = Integer.MAX_VALUE;

        for (int i = 0; i < integers.length; i++) {
            result = min(result, integers[i]);
        }

        return result;
    }

    // can be made iterative for better performance
    private int compute(int i, int j) {
        int result;

        if (min(i, j) == 0) {
            result = max(i, j);
        } else {
            int substitutionCost
                    = indicator(a.charAt(i - 1) != b.charAt(j - 1));

            result = minimum(
                    compute(i - 1, j) + 1,
                    compute(i, j - 1) + 1,
                    compute(i - 1, j - 1) + substitutionCost
            );
        }

        return result;
    }

    /**
     * Computes the Levenshtein distance between {@code a} and
     * {@code b}.
     *
     * @return an integer representing the Levenshtein distance between
     * {@code a} and {@code b}
     */
    public int compute() {
        return compute(a.length(), b.length());
    }

}
