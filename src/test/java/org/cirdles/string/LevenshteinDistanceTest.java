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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Zeringue
 */
public class LevenshteinDistanceTest {
    
    @Test
    public void testDistanceBetweenEmptyStringsIsZero() {
        assertEquals(0, new LevenshteinDistance("", "").compute());
    }

    @Test
    public void testDistanceWithEmptyStringIsLengthOfOtherString() {
        assertEquals(4, new LevenshteinDistance("test", "").compute());
        assertEquals(4, new LevenshteinDistance("", "test").compute());
    }
    
    @Test
    public void testCountsInsertionsAndDeletions() {
        assertEquals(5, new LevenshteinDistance("test case", "test").compute());
        assertEquals(5, new LevenshteinDistance("test", "test case").compute());
        
        assertEquals(5, new LevenshteinDistance("test case", "case").compute());
        assertEquals(5, new LevenshteinDistance("case", "test case").compute());
    }
    
    @Test
    public void testCountsSubstitutions() {
        assertEquals(3, new LevenshteinDistance("John", "Jack").compute());
        assertEquals(4, new LevenshteinDistance("John", "Kels").compute());
    }
    
    @Test
    public void testIndicator() {
        assertEquals(1, LevenshteinDistance.indicator(true));
        assertEquals(0, LevenshteinDistance.indicator(false));
    }
    
    @Test
    public void testMinimum() {
        assertEquals(1, LevenshteinDistance.minimum(2, 1, 3));
        assertEquals(2, LevenshteinDistance.minimum(5, 2, 3));
    }
    
}
