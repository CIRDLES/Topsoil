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
public class ParametricCurve2DTest {
    
    public ParametricCurve2DTest() {
    }
    
    

    /**
     * Test of of method, of class ParametricCurve2D.
     */
    @Test
    public void testOf() {
        System.out.println("of");
        double t = 3.2;
        Function oneFunction = new ConstantFunction(1);
        Function twoFunction = new ConstantFunction(2);
        ParametricCurve2D instance = new ParametricCurve2D(oneFunction,twoFunction) ;
        Vector2D expResult = new Vector2D(1,2);
        Vector2D result = instance.of(t);
        assertEquals(expResult, result);
       
    }

    /**
     * Test of prime method, of class ParametricCurve2D.
     */
    @Test
    public void testPrime() {
        System.out.println("prime");
        Function oneFunction = new ConstantFunction(5);
        Function twoFunction = new ConstantFunction(3);
        ParametricCurve2D instance = new ParametricCurve2D(oneFunction,twoFunction) ;
        ParametricCurve2D expResult = new ParametricCurve2D(oneFunction.prime(),twoFunction.prime());
        ParametricCurve2D result = instance.prime();
        expResult.equals(result);
        
    }
    
}
