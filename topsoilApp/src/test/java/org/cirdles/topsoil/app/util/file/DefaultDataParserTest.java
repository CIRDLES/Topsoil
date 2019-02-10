package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.model.*;
import org.cirdles.topsoil.app.model.generic.DataValue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class DefaultDataParserTest {

    static String CONTENT = "Col1,Col2,Col3,Col4,Col5\n" +
                            "0.0,1.0,2.0,3.0,4.0\n" +
                            "0.1,1.1,2.1,3.1,4.1\n" +
                            "0.2,1.2,2.2,3.2,4.2\n" +
                            "0.3,1.3,2.3,3.3,4.3\n" +
                            "0.4,1.4,2.4,3.4,4.4\n";
    static DefaultDataParser dataParser;
    static ColumnTree columnTreeOracle;
    static List<DataSegment> dataSegmentsOracle;

    @BeforeClass
    public static void setup() {
        dataParser = new DefaultDataParser(CONTENT);

        DataColumn col1 = new DataColumn<>("Col1", Double.class);
        DataColumn col2 = new DataColumn<>("Col2", Double.class);
        DataColumn col3 = new DataColumn<>("Col3", Double.class);
        DataColumn col4 = new DataColumn<>("Col4", Double.class);
        DataColumn col5 = new DataColumn<>("Col5", Double.class);
        columnTreeOracle = new ColumnTree(Arrays.asList(col1, col2, col3, col4, col5));

        List<DataRow> rows = new ArrayList<>();
        List<DataValue<?>> values;
        for (int i = 0; i < 5; i++) {
            values = new ArrayList<>();
            values.add(new DoubleValue(col1, Double.parseDouble("0." + i)));
            values.add(new DoubleValue(col2, Double.parseDouble("1." + i)));
            values.add(new DoubleValue(col3, Double.parseDouble("2." + i)));
            values.add(new DoubleValue(col4, Double.parseDouble("3." + i)));
            values.add(new DoubleValue(col5, Double.parseDouble("4." + i)));
            rows.add(new DataRow("row" + (i + 1), values));
        }
        dataSegmentsOracle = new ArrayList<>();
        dataSegmentsOracle.add(new DataSegment("model", rows.toArray(new DataRow[]{})));
    }

    @Test
    public void parseColumnTree_test() {
        ColumnTree columnTree = dataParser.parseColumnTree();
        assertEquals(columnTreeOracle, columnTree);
    }

    @Test
    public void parseData_test() {
        List<DataSegment> dataSegments = dataParser.parseData();
        System.out.println(dataSegments.get(0).getChildren());
        System.out.println(dataSegmentsOracle.get(0).getChildren());
        assertEquals(dataSegmentsOracle, dataSegments);
    }

}
