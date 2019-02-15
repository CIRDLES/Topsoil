package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.ColumnTree;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.*;
import org.cirdles.topsoil.app.data.value.DoubleValue;
import org.cirdles.topsoil.app.data.value.StringValue;
import org.cirdles.topsoil.app.util.file.parser.DefaultFileParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class DefaultFileParserTest {

    static String CONTENT = "Col1,Col2,Col3,Col4,Col5\n" +
                            "sub1,,,sub2,\n" +
                            "0.0,one,2.0,three,4.0\n" +
                            "1.0,two,3.0,four,5.0\n";
    static DefaultFileParser dataParser;
    static ColumnTree columnTreeOracle;
    static List<DataSegment> dataSegmentsOracle;

    @BeforeClass
    public static void setup() {
        dataParser = new DefaultFileParser();

        DataColumn<Double> col1 = new DataColumn<>("Col1\nsub1", Double.class);
        DataColumn<String> col2 = new DataColumn<>("Col2", String.class);
        DataColumn<Double> col3 = new DataColumn<>("Col3", Double.class);
        DataColumn<String> col4 = new DataColumn<>("Col4\nsub2", String.class);
        DataColumn<Double> col5 = new DataColumn<>("Col5", Double.class);
        columnTreeOracle = new ColumnTree(Arrays.asList(col1, col2, col3, col4, col5));

        DataRow row1 = new DataRow("row1", Arrays.asList(
                new DoubleValue(col1, 0.0),
                new StringValue(col2, "one"),
                new DoubleValue(col3, 2.0),
                new StringValue(col4, "three"),
                new DoubleValue(col5, 4.0)
        ));
        DataRow row2 = new DataRow("row2", Arrays.asList(
                new DoubleValue(col1, 1.0),
                new StringValue(col2, "two"),
                new DoubleValue(col3, 3.0),
                new StringValue(col4, "four"),
                new DoubleValue(col5, 5.0)
        ));
        dataSegmentsOracle = new ArrayList<>();
        dataSegmentsOracle.add(new DataSegment("model", row1, row2));
    }

    @Test
    public void parseColumnTree_test() {
        ColumnTree columnTree = dataParser.parseColumnTree(CONTENT, ",");
        assertEquals(columnTreeOracle, columnTree);
        assertSame("Second column should be of type String.",String.class,
                   ((DataColumn<?>) columnTree.find("Col2")).getType());
        assertSame("Third column should be of type Double.", Double.class,
                   ((DataColumn<?>) columnTree.find("Col3")).getType());
    }

    @Test
    public void parseDataTable_test() {
        DataTable table = dataParser.parseDataTable(CONTENT, ",", "CONTENT");
        assertEquals(dataSegmentsOracle, table.getChildren());
    }

}
