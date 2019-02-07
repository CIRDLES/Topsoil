package org.cirdles.topsoil.app.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author marottajb
 */
public class DataRowTest {

    static DataColumn[] COLUMNS = new DataColumn[]{
            new DataColumn("Zero"),
            new DataColumn("One")
    };

    DataRow rowOne;
    DataRow rowTwo;

    @Test
    public void equals_test() {
        Map<DataColumn, Object> valueMapOne = new HashMap<>();
        valueMapOne.put(COLUMNS[0], "0.0");
        valueMapOne.put(COLUMNS[1], "1.0");
        rowOne = new DataRow("Row", valueMapOne);

        Map<DataColumn, Object> valueMapTwo = new HashMap<>();
        valueMapTwo.put(COLUMNS[0], "0.0");
        valueMapTwo.put(COLUMNS[1], "1.0");
        rowTwo = new DataRow("Row", valueMapTwo);

        boolean equal = rowOne.equals(rowTwo);
        Assert.assertTrue(equal);
    }

    @Test
    public void equalsFalseLabel_test() {
        Map<DataColumn, Object> valueMapOne = new HashMap<>();
        valueMapOne.put(COLUMNS[0], "0.0");
        valueMapOne.put(COLUMNS[1], "1.0");
        rowOne = new DataRow("Row1", valueMapOne);

        Map<DataColumn, Object> valueMapTwo = new HashMap<>();
        valueMapTwo.put(COLUMNS[0], "0.0");
        valueMapTwo.put(COLUMNS[1], "1.0");
        rowTwo = new DataRow("Row2", valueMapTwo);

        boolean equal = rowOne.equals(rowTwo);
        Assert.assertFalse(equal);
    }

    @Test
    public void equalsFalseSelected_test() {
        Map<DataColumn, Object> valueMapOne = new HashMap<>();
        valueMapOne.put(COLUMNS[0], "0.0");
        valueMapOne.put(COLUMNS[1], "1.0");
        rowOne = new DataRow("Row", valueMapOne);
        rowOne.setSelected(false);

        Map<DataColumn, Object> valueMapTwo = new HashMap<>();
        valueMapTwo.put(COLUMNS[0], "0.0");
        valueMapTwo.put(COLUMNS[1], "1.0");
        rowTwo = new DataRow("Row", valueMapTwo);

        boolean equal = rowOne.equals(rowTwo);
        Assert.assertFalse(equal);
    }

    @Test
    public void equalsFalseValueMap_test() {
        Map<DataColumn, Object> valueMapOne = new HashMap<>();
        valueMapOne.put(COLUMNS[0], "0.0");
        rowOne = new DataRow("Row", valueMapOne);

        Map<DataColumn, Object> valueMapTwo = new HashMap<>();
        valueMapTwo.put(COLUMNS[0], "1.0");
        valueMapTwo.put(COLUMNS[1], "0.0");
        rowTwo = new DataRow("Row", valueMapTwo);

        boolean equal = rowOne.equals(rowTwo);
        Assert.assertFalse(equal);
    }

}
