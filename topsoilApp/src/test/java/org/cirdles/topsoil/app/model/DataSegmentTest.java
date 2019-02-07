package org.cirdles.topsoil.app.model;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author marottajb
 */
public class DataSegmentTest {

    static DataColumn[] COLUMNS = new DataColumn[]{
            new DataColumn("Zero"),
            new DataColumn("One"),
            new DataColumn("Two")
    };

    DataSegment segOne;
    DataSegment segTwo;

    static DataRow rowOne;
    static DataRow rowTwo;
    static DataRow rowThree;
    static {
        Map<DataColumn, Object> valueMap = new HashMap<>();
        valueMap.put(COLUMNS[0], "0.0");
        valueMap.put(COLUMNS[1], "1.0");
        rowOne = new DataRow("SameRow", valueMap);
        rowTwo = new DataRow("SameRow", valueMap);

        valueMap = new HashMap<>();
        valueMap.put(COLUMNS[0], "0.0");
        valueMap.put(COLUMNS[1], "2.0");
        valueMap.put(COLUMNS[2], "4.0");
        rowThree = new DataRow("DifferentRow", valueMap);
    }

    @Test
    public void equals_test() {
        segOne = new DataSegment("Segment", rowOne, rowTwo);
        System.out.println(segOne);
        System.out.println("#rows: " + segOne.getChildren().size());
        segTwo = new DataSegment("Segment", rowOne, rowTwo);
        System.out.println(segTwo);
        System.out.println("#rows: " + segTwo.getChildren().size());
        assertEquals(segOne, segTwo);
    }
}
