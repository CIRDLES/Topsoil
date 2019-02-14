package org.cirdles.topsoil.app.model;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataValueTest {

    private static DataColumn<Double> colOne, colTwo;
    private static DataValue<Double> valOne, valTwo, valThree;

    @BeforeClass
    public static void setup() {
        colOne = new DataColumn<>("colOne", Double.class);
        colTwo = new DataColumn<>("colTwo", Double.class);
        valOne = new DoubleValue(colOne, 1.0);
        valTwo = new DoubleValue(colOne, 1.0);
        valThree = new DoubleValue(colTwo, 2.0);
    }

    @Test
    public void equals_test() {
        assertEquals(valOne, valTwo);
        assertNotEquals(valOne, valThree);
        assertNotEquals(valTwo, valThree);
    }

}
