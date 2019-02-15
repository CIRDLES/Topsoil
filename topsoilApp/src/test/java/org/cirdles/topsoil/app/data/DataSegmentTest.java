package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.*;
import org.cirdles.topsoil.app.data.value.DataValue;
import org.cirdles.topsoil.app.data.value.DoubleValue;
import org.cirdles.topsoil.app.data.value.StringValue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author marottajb
 */
public class DataSegmentTest {

    static DataColumn<String> stringCol = new DataColumn<>("StringCol", String.class);
    static DataColumn<Double> doubleCol = new DataColumn<>("DoubleCol", Double.class);

    DataSegment segOne;
    DataSegment segTwo;

    static DataRow rowOne;
    static DataRow rowTwo;
    static DataRow rowThree;

    @BeforeClass
    public static void setup() {
        List<DataValue<?>> values = new ArrayList<>();
        values.add(new StringValue(stringCol, "0.0"));
        values.add(new DoubleValue(doubleCol, 0.0));
        rowOne = new DataRow("SameRow", values);
        rowTwo = new DataRow("SameRow", values);

        values = new ArrayList<>();
        values.add(new StringValue(stringCol, "1.0"));
        values.add(new DoubleValue(doubleCol, 1.0));
        rowThree = new DataRow("DifferentRow", values);
    }

    @Test
    public void equals_test() {
        segOne = new DataSegment("Segment", rowOne, rowTwo);
        segTwo = new DataSegment("Segment", rowOne, rowTwo);
        assertEquals(segOne, segTwo);
    }
}
