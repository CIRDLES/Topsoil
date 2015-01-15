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

import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author devatas
 */
public class CubicBezierCurveTest {
    
    public CubicBezierCurveTest() {
    }
    
    

    /**
     * Test of asPath method, of class CubicBezierCurve.
     */
    @Test
    public void testAsPath() {
        System.out.println("asPath");
        Vector2D vector1 = new Vector2D(2.3, 4.5);
        Vector2D vector2 = new Vector2D(3, 4.5);
        Vector2D vector3 = new Vector2D(5, 6);
        Vector2D vector4 = new Vector2D(5.6, 4.5);
        CubicBezierCurve instance = new CubicBezierCurve(vector1,vector2,vector3,vector4);
        Path expResult = new Path(new MoveTo(2.3,4.5));
        Path result = instance.asPath();
        expResult.equals(result);
        
        
    }

    /**
     * Test of asCubicCurveTo method, of class CubicBezierCurve.
     */
    @Test
    public void testAsCubicCurveTo() {
        System.out.println("asCubicCurveTo");
        Vector2D vector1 = new Vector2D(2.3, 4.5);
        Vector2D vector2 = new Vector2D(3.0, 4.5);
        Vector2D vector3 = new Vector2D(5, 6);
        Vector2D vector4 = new Vector2D(5.6, 4.5);
        CubicBezierCurve instance =new CubicBezierCurve(vector1,vector2,vector3,vector4) ;
        double X1=2.5;
        double Y1=5.6;
        double X2=5.4;
        double Y2=9.8;
        double x=3.1;
        double y=4.3;
        CubicCurveTo expResult = new CubicCurveTo(x,y,X1,Y1,X2,Y2);
        CubicCurveTo result = instance.asCubicCurveTo();
        expResult.equals(result);
        
    }

    /**
     * Test of approximate method, of class CubicBezierCurve.
     */
    @Test
    public void testApproximate() {
        System.out.println("approximate");
        Function oneFunction = new ConstantFunction(1);
        Function twoFunction = new ConstantFunction(2);
        ParametricCurve2D curve = new ParametricCurve2D(oneFunction,twoFunction) ;
        double t0 = 2.1;
        double t1 = 3.4;
        Vector2D p0 = curve.of(t0);
        Vector2D p3 = curve.of(t1);
        Vector2D p1 = p0.plus(curve.prime().of(t0).times((t1 - t0) / 3));
        Vector2D p2 = p3.minus(curve.prime().of(t1).times((t1 - t0) / 3));
        CubicBezierCurve expResult =new CubicBezierCurve(p0,p1,p2,p3) ;
        CubicBezierCurve result = CubicBezierCurve.approximate(curve, t0, t1);
        expResult.equals(result);
        
    }
    
}
