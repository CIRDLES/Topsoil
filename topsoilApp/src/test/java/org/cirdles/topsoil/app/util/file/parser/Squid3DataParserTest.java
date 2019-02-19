package org.cirdles.topsoil.app.util.file.parser;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.column.ColumnTree;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.*;
import org.cirdles.topsoil.app.data.value.DoubleValue;
import org.cirdles.topsoil.app.data.value.StringValue;
import org.cirdles.topsoil.app.util.file.parser.Squid3DataParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class Squid3DataParserTest {

    private static String TEST_FILE = "Z626611pkPerm1_UnknownsBySampleReportTableForET_Redux.csv";

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

    static DataParser dataParser = new Squid3DataParser();
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
        columnTreeOracle = new ColumnTree(cat1, cat2, cat3);

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
        dataTableOracle = new DataTable(DataTemplate.SQUID_3, "CONTENT", columnTreeOracle, Arrays.asList(seg1, seg2));

    }

    @Test
    public void parseColumnTree_test() {
        ColumnTree cT = dataParser.parseColumnTree(CONTENT, Delimiter.COMMA.getValue());
        assertEquals(columnTreeOracle, cT);
    }

    @Test
    public void parseDataTable_test() {
        DataTable table = dataParser.parseDataTable(CONTENT, ",", "CONTENT");
        assertEquals(dataTableOracle, table);
    }

}
