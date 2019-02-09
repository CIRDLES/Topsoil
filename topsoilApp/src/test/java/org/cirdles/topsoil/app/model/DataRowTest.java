package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.generic.DataValue;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author marottajb
 */
public class DataRowTest {

    private static DataColumn<String> stringCol = new DataColumn<>("StringCol", String.class);
    private static DataColumn<Double> doubleCol = new DataColumn<>("DoubleCol", Double.class);
    private DataRow rowOne, rowTwo;

    @Test
    public void equals_test() {
        List<DataValue<?>> valueListOne = new ArrayList<>();
        valueListOne.add(new StringValue(stringCol, "0.0"));
        valueListOne.add(new DoubleValue(doubleCol, 0.0));
        rowOne = new DataRow("Row", valueListOne);

        List<DataValue<?>> valueListTwo = new ArrayList<>();
        valueListTwo.add(new StringValue(stringCol, "0.0"));
        valueListTwo.add(new DoubleValue(doubleCol, 0.0));
        rowTwo = new DataRow("Row", valueListTwo);

        Assert.assertEquals(rowOne, rowTwo);
    }

    @Test
    public void equalsFalseLabel_test() {
        List<DataValue<?>> valueListOne = new ArrayList<>();
        valueListOne.add(new StringValue(stringCol, "0.0"));
        valueListOne.add(new DoubleValue(doubleCol, 0.0));
        rowOne = new DataRow("Row1", valueListOne);

        List<DataValue<?>> valueListTwo = new ArrayList<>();
        valueListTwo.add(new StringValue(stringCol, "0.0"));
        valueListTwo.add(new DoubleValue(doubleCol, 0.0));
        rowTwo = new DataRow("Row2", valueListTwo);

        Assert.assertEquals(rowOne, rowTwo);
    }

    @Test
    public void equalsFalseSelected_test() {
        List<DataValue<?>> valueListOne = new ArrayList<>();
        valueListOne.add(new StringValue(stringCol, "0.0"));
        valueListOne.add(new DoubleValue(doubleCol, 0.0));
        rowOne = new DataRow("Row", valueListOne);
        rowOne.setSelected(true);

        List<DataValue<?>> valueListTwo = new ArrayList<>();
        valueListTwo.add(new StringValue(stringCol, "0.0"));
        valueListTwo.add(new DoubleValue(doubleCol, 0.0));
        rowTwo = new DataRow("Row", valueListTwo);
        rowTwo.setSelected(false);

        Assert.assertEquals(rowOne, rowTwo);
    }

    @Test
    public void equalsFalseValues_test() {
        List<DataValue<?>> valueListOne = new ArrayList<>();
        valueListOne.add(new StringValue(stringCol, "0.0"));
        valueListOne.add(new DoubleValue(doubleCol, 0.0));
        rowOne = new DataRow("Row", valueListOne);

        List<DataValue<?>> valueListTwo = new ArrayList<>();
        valueListTwo.add(new StringValue(stringCol, "1.0"));
        valueListTwo.add(new DoubleValue(doubleCol, 1.0));
        rowTwo = new DataRow("Row", valueListTwo);

        Assert.assertEquals(rowOne, rowTwo);
    }

}
