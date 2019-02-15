package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.model.*;
import org.cirdles.topsoil.app.model.DataValue;
import org.cirdles.topsoil.app.model.composite.DataComponent;
import org.cirdles.topsoil.app.model.composite.DataComposite;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class Squid3DataParserTest {

    private static String CONTENT = (
            ",Cat1,,,Cat2,,Cat3\n" +
            ",,Col2,,,,\n" +
            ",Col1,Col2,,,,\n" +
            ",Col1,Col2,,Col4,,\n" +
            ",Col1,Col2,Col3,Col4,Col5,\n" +
            "Seg1,,,,,,\n" +
            "Seg1:Row1,1.0,2.0,3.0,4.0,5.0,\n" +
            "Seg2,,,,,,\n" +
            "Seg2:Row1,1.0,2.0,3.0,4.0,5.0,\n"
    );

    static ColumnTree columnTreeOracle;
    static List<DataSegment> dataSegmentsOracle;

    @BeforeClass
    public static void setup() {
        DataCategory cat1 = new DataCategory(
                "Cat1",
                new DataColumn<>("Col1 Col1 Col1", Double.class),
                new DataColumn<>("Col2 Col2 Col2 Col2", Double.class),
                new DataColumn<>("Col3", Double.class)
        );
        DataCategory cat2 = new DataCategory(
                "Cat2",
                new DataColumn<>("Col4 Col4", Double.class),
                new DataColumn<>("Col5", Double.class)
        );
        DataCategory cat3 = new DataCategory(
                "Cat3"
        );
        columnTreeOracle = new ColumnTree(Arrays.asList(cat1, cat2, cat3));

        List<DataValue<?>> values = new ArrayList<>();
        values.add(new DoubleValue(((DataColumn<Double>) cat1.getChildren().get(0)), 1.0));
        values.add(new DoubleValue(((DataColumn<Double>) cat1.getChildren().get(1)), 2.0));
        values.add(new DoubleValue(((DataColumn<Double>) cat1.getChildren().get(2)), 3.0));
        values.add(new DoubleValue(((DataColumn<Double>) cat2.getChildren().get(0)), 4.0));
        values.add(new DoubleValue(((DataColumn<Double>) cat2.getChildren().get(1)), 5.0));
        DataSegment seg1 = new DataSegment(
                "Seg1",
                new DataRow("Seg1:Row1", values)
        );
        values = new ArrayList<>();
        values.add(new DoubleValue(((DataColumn<Double>) cat1.getChildren().get(0)), 1.0));
        values.add(new DoubleValue(((DataColumn<Double>) cat1.getChildren().get(1)), 2.0));
        values.add(new DoubleValue(((DataColumn<Double>) cat1.getChildren().get(2)), 3.0));
        values.add(new DoubleValue(((DataColumn<Double>) cat2.getChildren().get(0)), 4.0));
        values.add(new DoubleValue(((DataColumn<Double>) cat2.getChildren().get(1)), 5.0));
        DataSegment seg2 = new DataSegment(
                "Seg2",
                new DataRow("Seg2:Row1", values)
        );
        dataSegmentsOracle = new ArrayList<>();
        dataSegmentsOracle.addAll(Arrays.asList(seg1, seg2));

    }

    @Test
    public void parseColumnTree_test() {
        ColumnTree cT = new Squid3DataParser().parseColumnTree(CONTENT, ",");
        assertEquals(columnTreeOracle, cT);
    }

    @Test
    public void parseData_test() {
        DataTable table = new Squid3DataParser().parseDataTable(CONTENT, ",", "CONTENT");
        assertEquals(dataSegmentsOracle, table.getChildren());
    }

}
