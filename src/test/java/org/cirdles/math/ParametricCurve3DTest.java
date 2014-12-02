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
public class ParametricCurve3DTest {

    public ParametricCurve3DTest() {
    }

    /**
     * Test of x method, of class ParametricCurve3D.
     */
    @Test
    public void testX() {
        System.out.println("x");
        Function oneFunction = new ConstantFunction(1);
        ParametricCurve3D instance = new ParametricCurve3D(oneFunction, new ConstantFunction(2), new ConstantFunction(3));
        Function expResult = oneFunction;
        Function result = instance.x();
        assertEquals(expResult, result);

    }

    /**
     * Test of y method, of class ParametricCurve3D.
     */
    @Test
    public void testY() {
        System.out.println("y");
        Function oneFunction = new ConstantFunction(2);
        ParametricCurve3D instance = new ParametricCurve3D(new ConstantFunction(1), oneFunction, new ConstantFunction(3));
        Function expResult = oneFunction;
        Function result = instance.y();
        assertEquals(expResult, result);
       

    }

    /**
     * Test of z method, of class ParametricCurve3D.
     */
    @Test
    public void testZ() {
        System.out.println("z");
        Function oneFunction = new ConstantFunction(3);
        ParametricCurve3D instance = new ParametricCurve3D(new ConstantFunction(1), new ConstantFunction(2), oneFunction);
        Function expResult = oneFunction;
        Function result = instance.z();
        assertEquals(expResult, result);

    }

    /**
     * Test of of method, of class ParametricCurve3D.
     */
    @Test
    public void testOf() {
        System.out.println("of");
        double t = 2.3;
        Function oneFunction = new ConstantFunction(1);
        Function twoFunction = new ConstantFunction(2);
        Function threeFunction = new ConstantFunction(3);
        ParametricCurve3D instance = new ParametricCurve3D(oneFunction, twoFunction, threeFunction);
        Vector3D expResult = new Vector3D(1, 2, 3);
        Vector3D result = instance.of(t);
        assertEquals(expResult, result);

    }

    /**
     * Test of prime method, of class ParametricCurve3D.
     */
    @Test
    public void testPrime() {
        System.out.println("prime");
        Function oneFunction = new ConstantFunction(5);
        Function twoFunction = new ConstantFunction(3);
        Function threeFunction = new ConstantFunction(7);

        ParametricCurve3D instance = new ParametricCurve3D(oneFunction, twoFunction, threeFunction);
        ParametricCurve3D expResult =new ParametricCurve3D(oneFunction.prime(),twoFunction.prime(),threeFunction.prime());
        ParametricCurve3D result = instance.prime();
        expResult.equals(result);

    }

}
