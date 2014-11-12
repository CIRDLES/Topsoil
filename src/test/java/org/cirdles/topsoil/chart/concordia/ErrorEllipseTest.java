package org.cirdles.topsoil.chart.concordia;

import Jama.Matrix;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zeringuej
 */
public class ErrorEllipseTest {

    /**
     * Test of getControlPoints method, of class ErrorEllipse.
     */
    @Test
    public void testGetControlPoints() {
        System.out.println("getControlPoints");
        ErrorEllipse instance = new ErrorEllipse() {

            @Override
            public double getX() {
                return 7.2136E-2;
            }

            @Override
            public double getSigmaX() {
                return 6.2603E-5;
            }

            @Override
            public double getY() {
                return 1.1028E-2;
            }

            @Override
            public double getSigmaY() {
                return 2.7280E-6;
            }

            @Override
            public double getRho() {
                return 5.7610E-1;
            }
        };

        double expResult0_0 = 7.2261E-2;
        double result0_0 = instance.getControlPoints(2).get(0, 0);
        assertEquals(expResult0_0, result0_0, 1E-6);

        double expResult0_1 = 1.1031E-2;
        double result0_1 = instance.getControlPoints(2).get(0, 1);
        assertEquals(expResult0_1, result0_1, 1E-6);

        double expResult5_0 = 7.2011E-2;
        double result5_0 = instance.getControlPoints(2).get(5, 0);
        assertEquals(expResult5_0, result5_0, 1E-6);

        double expResult5_1 = 1.1027E-2;
        double result5_1 = instance.getControlPoints(2).get(5, 1);
        assertEquals(expResult5_1, result5_1, 1E-6);
    }
    
    @Test
    public void testCalculateU() {
        System.out.println("calculateU");
        
        for (int i = 0; i < 5000; i++) {
            double sigmaX = Math.random() * 10;
            double sigmaY = Math.random() * 10;
            double rho = Math.random() * 2 - 1;
            
            Matrix a = ErrorEllipse.calculateU(sigmaX, sigmaY, rho);
            Matrix b = ErrorEllipse.calculateUOld(sigmaX, sigmaY, rho);
            
            assertEquals(a.get(0, 0), b.get(0, 0), 1e-12);
            assertEquals(a.get(0, 1), b.get(0, 1), 1e-12);
            assertEquals(a.get(1, 0), b.get(1, 0), 1e-12);
            assertEquals(a.get(1, 1), b.get(1, 1), 1e-12);
        }
    }

}
