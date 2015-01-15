/*
 * Copyright 2014 CIRDLES.
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
package org.cirdles.math;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author devatas
 */
public class Vector2DTest {

    public Vector2DTest() {
    }

    /**
     * Test of plus method, of class Vector2D.
     */
    @Test
    public void testPlus() {
        System.out.println("plus");
        Vector2D vector = new Vector2D(2.3, 4.5);
        Vector2D instance = new Vector2D(3, 4);
        Vector2D expResult = new Vector2D(3 + 2.3, 4 + 4.5);
        Vector2D result = instance.plus(vector);
        assertEquals(expResult, result);

    }

    /**
     * Test of minus method, of class Vector2D.
     */
    @Test
    public void testMinus() {
        System.out.println("minus");
        Vector2D vector = new Vector2D(2.1, 3.2);
        Vector2D instance = new Vector2D(4, 8);
        Vector2D expResult = new Vector2D(4 - 2.1, 8 - 3.2);
        Vector2D result = instance.minus(vector);
        assertEquals(expResult, result);

    }

    /**
     * Test of times method, of class Vector2D.
     */
    @Test
    public void testTimes() {
        System.out.println("times");
        double scalar = 2.4;
        Vector2D instance = new Vector2D(4.5, 6.7);
        Vector2D expResult = new Vector2D(4.5 * 2.4, 6.7 * 2.4);
        Vector2D result = instance.times(scalar);
        assertEquals(expResult, result);

    }

    /**
     * Test of dividedBy method, of class Vector2D.
     */
    @Test
    public void testDividedBy() {
        System.out.println("dividedBy");
        double scalar = 2;
        Vector2D instance = new Vector2D(3.4, 7.8);
        Vector2D expResult = new Vector2D(3.4 / 2, 7.8 / 2);
        Vector2D result = instance.dividedBy(scalar);
        assertEquals(expResult, result);

    }

    /**
     * Test of normalized method, of class Vector2D.
     */
    @Test
    public void testNormalized() {
        System.out.println("normalized");
        Vector2D instance = new Vector2D(2.3, 5.6);
        double magnitude = Math.sqrt(2.3 * 2.3 + 5.6 * 5.6);
        Vector2D expResult = new Vector2D(2.3 / magnitude, 5.6 / magnitude);
        Vector2D result = instance.normalized();
        assertEquals(expResult, result);

    }

    /**
     * Test of toString method, of class Vector2D.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Vector2D instance = new Vector2D(3.4, 4.5);
        String expResult = "(3.40000, 4.50000)";
        String result = instance.toString();
        assertEquals(expResult, result);

    }

}
