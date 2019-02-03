package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class Squid3DataParserTest {

    private static String TEST_FILE = "squid3-sample.csv";
    static String[][] cells = new String[][]{
            new String[]{ "", "Cat1", "", "", "Cat2", "", "Cat3" },
            new String[]{ "", "", "Col2", "", "", "", "" },
            new String[]{ "", "Col1", "Col2", "", "", "", "" },
            new String[]{ "", "Col1", "Col2", "", "Col4", "", "" },
            new String[]{ "", "Col1", "Col2", "Col3", "Col4", "Col5", "" },
            new String[]{ "Seg1", "", "", "", "", "", "" },
            new String[]{ "Seg1:Row1", "0.0", "0.0", "0.0", "0.0", "0.0", "" }
    };
    private static String CONTENT = (
            ",Cat1,,,Cat2,,Cat3\n" +
            ",,Col2,,,,\n" +
            ",Col1,Col2,,,,\n" +
            ",Col1,Col2,,Col4,,\n" +
            ",Col1,Col2,Col3,Col4,Col5,\n" +
            "Seg1,,,,,,\n" +
            "Seg1:Row1,0.0,0.0,0.0,0.0,0.0,\n"
    );

    static ColumnTree columnTreeOracle;
    static DataSegment[] dataSegmentsOracle;

    @BeforeClass
    public static void setup() {
        DataCategory cat1 = new DataCategory(
                "Cat1",
                new DataColumn("Col1 Col1 Col1"),
                new DataColumn("Col2 Col2 Col2 Col2"),
                new DataColumn("Col3")
        );
        DataCategory cat2 = new DataCategory(
                "Cat2",
                new DataColumn("Col4 Col4"),
                new DataColumn("Col5")
        );
        DataCategory cat3 = new DataCategory(
                "Cat3"
        );
        columnTreeOracle = new ColumnTree(Arrays.asList(cat1, cat2, cat3));

        Map<DataColumn, Object> valueMap = new HashMap<>();
        valueMap.put((DataColumn) cat1.getChildren().get(0), "0.0");
        valueMap.put((DataColumn) cat1.getChildren().get(1), "0.0");
        valueMap.put((DataColumn) cat1.getChildren().get(2), "0.0");
        valueMap.put((DataColumn) cat2.getChildren().get(0), "0.0");
        valueMap.put((DataColumn) cat2.getChildren().get(1), "0.0");
        DataSegment seg1 = new DataSegment(
                "Seg1",
                new DataRow("Seg1:Row1", valueMap)
        );
        dataSegmentsOracle = new DataSegment[]{
                seg1
        };

    }

    @Test
    public void parseColumnTree_test() {
        ColumnTree cT = new Squid3DataParser(CONTENT).parseColumnTree();
        assertEquals(columnTreeOracle, cT);
    }

    @Test
    public void parseData_test() {
        DataSegment[] data = new Squid3DataParser(CONTENT).parseData();
        assertEquals(dataSegmentsOracle, data);
    }

}
