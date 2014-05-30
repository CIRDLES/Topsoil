/*
 * Copyright 2014 zeringuej.
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
 * @author zeringuej
 */
public class Vector3DTest {

    /**
     * Test of x method, of class Vector3D.
     */
    @Test
    public void testX() {
        System.out.println("x");
        
        Vector3D instance = new Vector3D(21.45, 7, 9);
        double expResult = 21.45;
        double result = instance.x();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of y method, of class Vector3D.
     */
    @Test
    public void testY() {
        System.out.println("y");
        
        Vector3D instance = new Vector3D(21.45, 7, 9);
        double expResult = 7;
        double result = instance.y();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of z method, of class Vector3D.
     */
    @Test
    public void testZ() {
        System.out.println("z");
        
        Vector3D instance = new Vector3D(21.45, 7, 9);
        double expResult = 9;
        double result = instance.z();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of plus method, of class Vector3D.
     */
    @Test
    public void testPlus() {
        System.out.println("plus");
        
        Vector3D vector = new Vector3D(21.45, 7, 9);
        Vector3D instance = new Vector3D(1, 2.2, 33);
        Vector3D expResult = new Vector3D(21.45 + 1, 7 + 2.2, 9 + 33);
        Vector3D result = instance.plus(vector);
        assertEquals(expResult, result);
    }

    /**
     * Test of minus method, of class Vector3D.
     */
    @Test
    public void testMinus() {
        System.out.println("minus");
        
        Vector3D vector = new Vector3D(21.45, 7, 9);
        Vector3D instance = new Vector3D(1, 2.2, 33);
        Vector3D expResult = new Vector3D(1 - 21.45, 2.2 - 7, 33 - 9);
        Vector3D result = instance.minus(vector);
        assertEquals(expResult, result);
    }

    /**
     * Test of times method, of class Vector3D.
     */
    @Test
    public void testTimes() {
        System.out.println("times");
        
        double scalar = .75;
        Vector3D instance = new Vector3D(21.45, 7, 9);
        Vector3D expResult = new Vector3D(21.45 * .75, 7 * .75, 9 * .75);
        Vector3D result = instance.times(scalar);
        assertEquals(expResult, result);
    }

    /**
     * Test of dividedBy method, of class Vector3D.
     */
    @Test
    public void testDividedBy() {
        System.out.println("dividedBy");
        
        double scalar = 5.24;
        Vector3D instance = new Vector3D(21.45, 7, 9);
        Vector3D expResult = new Vector3D(21.45 / 5.24, 7 / 5.24, 9 / 5.24);
        Vector3D result = instance.dividedBy(scalar);
        assertEquals(expResult, result);
    }

    /**
     * Test of dot method, of class Vector3D.
     */
    @Test
    public void testDot() {
        System.out.println("dot");
        
        Vector3D vector = new Vector3D(-5, 14, 8);
        Vector3D instance = new Vector3D(21.45, 7, 9);
        double expResult = -5 * 21.45 + 14 * 7 + 8 * 9;
        double result = instance.dot(vector);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of norm method, of class Vector3D.
     */
    @Test
    public void testNorm() {
        System.out.println("norm");
        
        Vector3D instance = new Vector3D(21.45, 7, 9);
        double expResult = Math.sqrt(21.45 * 21.45 + 7 * 7 + 9 * 9);
        double result = instance.norm();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of normalized method, of class Vector3D.
     */
    @Test
    public void testNormalized() {
        System.out.println("normalized");
        
        Vector3D instance = new Vector3D(21.45, 7, 9);
        double magnitude = Math.sqrt(21.45 * 21.45 + 7 * 7 + 9 * 9);
        Vector3D expResult = new Vector3D(21.45 / magnitude, 7 / magnitude, 9 / magnitude);
        Vector3D result = instance.normalized();
        assertEquals(expResult, result);
        
        assertEquals(result.norm(), 1, 1e-10);
    }

    /**
     * Test of toString method, of class Vector3D.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        
        Vector3D instance = new Vector3D(21.45, 7, 9);
        String expResult = "(21.4500,7.0000,9.0000)";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Vector3D.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        
        // equal
        Object obj = new Vector3D(21.45, 7, 9);
        Vector3D instance = new Vector3D(21.45, 7, 9);
        boolean expResult = true;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        
        // unequal
        obj = new Vector2D(21.45, 7);
        expResult = false;
        result = instance.equals(obj);
        assertEquals(expResult, result);
        
        // null
        obj = null;
        expResult = false;
        result = instance.equals(obj);
        assertEquals(expResult, result);
        
        // non-vector
        obj = "test";
        expResult = false;
        result = instance.equals(obj);
        assertEquals(expResult, result);
    }
}
