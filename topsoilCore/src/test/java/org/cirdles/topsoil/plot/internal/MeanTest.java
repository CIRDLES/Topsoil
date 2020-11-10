package org.cirdles.topsoil.plot.internal;

import org.cirdles.topsoil.utils.Mean;

import org.junit.Test;
import static org.junit.Assert.*;

public class MeanTest {

    @Test
    public void testMean() {
        double[] numList = new double[] {1.0,2.0,3.0,4.0,5.0};
        double expectedMean = (1 + 2 + 3 + 4 + 5)/5;
        double actualMean = Mean.getMean(numList);
        assertEquals(expectedMean, actualMean, 0);
    }

}
