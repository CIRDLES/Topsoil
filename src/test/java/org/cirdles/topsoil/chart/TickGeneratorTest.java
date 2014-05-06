/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cirdles.topsoil.chart;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class TickGeneratorTest {

    /**
     * Test of majorTicksForRange method, of class TickGenerator.
     */
    @Test
    public void testMajorTicksForRange() {
        System.out.println("majorTicksForRange");
        
        double lowerBound, upperBound;
        TickGenerator instance;
        List<Number> expResult, result;
        
        // Case #1
        lowerBound = 0;
        upperBound = 1;
        instance = new TickGenerator(.5, 1);
        expResult = Arrays.asList(.5);
        result = instance.majorTicksForRange(lowerBound, upperBound);
        assertEquals(expResult, result);
        
        // Case #2
        lowerBound = 0;
        upperBound = 1;
        instance = new TickGenerator(-.5, 1);
        expResult = Arrays.asList(.5);
        result = instance.majorTicksForRange(lowerBound, upperBound);
        assertEquals(expResult, result);
        
        // Case #3
        lowerBound = 0;
        upperBound = 1;
        instance = new TickGenerator(1.5, 1);
        expResult = Arrays.asList(.5);
        result = instance.majorTicksForRange(lowerBound, upperBound);
        assertEquals(expResult, result);
        
        // Case #4
        lowerBound = .1;
        upperBound = .2;
        instance = new TickGenerator(.5, 1);
        expResult = Arrays.asList();
        result = instance.majorTicksForRange(lowerBound, upperBound);
        assertEquals(expResult, result);
    }

    /**
     * Test of minorTicksForRange method, of class TickGenerator.
     */
    @Test
    public void testMinorTicksForRange() {
        System.out.println("minorTicksForRange");
        
        double lowerBound, upperBound;
        TickGenerator instance;
        List<Number> expResult, result;
        
        // Case #1
        lowerBound = 0;
        upperBound = 1;
        instance = new TickGenerator(.5, 1);
        expResult = Arrays.asList(0., .25, .75, 1.);
        result = instance.minorTicksForRange(lowerBound, upperBound);
        assertEquals(expResult, result);
        
        // Case #2
        lowerBound = 0;
        upperBound = 1;
        instance = new TickGenerator(-.5, 1);
        expResult = Arrays.asList(0., .25, .75, 1.);
        result = instance.minorTicksForRange(lowerBound, upperBound);
        assertEquals(expResult, result);
        
        // Case #3
        lowerBound = 0;
        upperBound = 1;
        instance = new TickGenerator(1.5, 1);
        expResult = Arrays.asList(0., .25, .75, 1.);
        result = instance.minorTicksForRange(lowerBound, upperBound);
        assertEquals(expResult, result);
        
        // Case #4
        lowerBound = .1;
        upperBound = .2;
        instance = new TickGenerator(.5, 1);
        expResult = Arrays.asList();
        result = instance.minorTicksForRange(lowerBound, upperBound);
        assertEquals(expResult, result);
        
        // Case #5
        lowerBound = .1;
        upperBound = .3;
        instance = new TickGenerator(.5, 1);
        expResult = Arrays.asList(.25);
        result = instance.minorTicksForRange(lowerBound, upperBound);
        assertEquals(expResult, result);
    }
}
