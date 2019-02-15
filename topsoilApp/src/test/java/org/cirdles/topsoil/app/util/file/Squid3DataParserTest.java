package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.model.*;
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
            "Seg1:Row1,1.0,two,3.0,four,5.0,\n" +
            "Seg2,,,,,,\n" +
            "Seg2:Row1,2.0,three,4.0,five,6.0,\n"
    );

    static ColumnTree columnTreeOracle;
    static DataTable dataTableOracle;

    @BeforeClass
    public static void setup() {
        DataColumn<Double> col1 = new DataColumn<>("Col1 Col1 Col1", Double.class);
        DataColumn<String> col2 = new DataColumn<>("Col2 Col2 Col2 Col2", String.class);
        DataColumn<Double> col3 = new DataColumn<>("Col3", Double.class);
        DataColumn<String> col4 = new DataColumn<>("Col4 Col4", String.class);
        DataColumn<Double> col5 = new DataColumn<>("Col5", Double.class);

        DataCategory cat1 = new DataCategory("Cat1", col1, col2, col3);
        DataCategory cat2 = new DataCategory("Cat2", col4, col5);
        DataCategory cat3 = new DataCategory("Cat3");
        columnTreeOracle = new ColumnTree(Arrays.asList(cat1, cat2, cat3));

        DataSegment seg1 =
                new DataSegment("Seg1",
                                new DataRow("Seg1:Row1", Arrays.asList(
                                        new DoubleValue(col1, 1.0),
                                        new StringValue(col2, "two"),
                                        new DoubleValue(col3, 3.0),
                                        new StringValue(col4, "four"),
                                        new DoubleValue(col5, 5.0)
                                ))
                );
        DataSegment seg2 = new DataSegment(
                "Seg2",
                new DataRow("Seg2:Row1", Arrays.asList(
                        new DoubleValue(col1, 2.0),
                        new StringValue(col2, "three"),
                        new DoubleValue(col3, 4.0),
                        new StringValue(col4, "five"),
                        new DoubleValue(col5, 6.0)
                ))
        );
        dataTableOracle = new DataTable("CONTENT", columnTreeOracle, Arrays.asList(seg1, seg2));

    }

    @Test
    public void parseColumnTree_test() {
        ColumnTree cT = new Squid3DataParser().parseColumnTree(CONTENT, ",");
        assertEquals(columnTreeOracle, cT);
    }

    @Test
    public void parseDataTable_test() {
        DataTable table = new Squid3DataParser().parseDataTable(CONTENT, ",", "CONTENT");
        assertEquals(dataTableOracle, table);
    }

}
