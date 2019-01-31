package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.DataCategory;
import org.cirdles.topsoil.app.data.DataColumn;
import org.cirdles.topsoil.app.data.DataRow;
import org.cirdles.topsoil.app.data.DataSegment;
import org.cirdles.topsoil.app.data.node.DataNode;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class Squid3DataParserTest {

    String[][] cells = new String[][]{
            new String[]{ "", "Cat1", "", "", "Cat2", "" },
            new String[]{ "", "", "Col2", "", "", "" },
            new String[]{ "", "Col1", "Col2", "", "", "" },
            new String[]{ "", "Col1", "Col2", "", "Col4", "" },
            new String[]{ "", "Col1", "Col2", "Col3", "Col4", "Col5" },
            new String[]{ "Seg1", "", "", "", "", "" },
            new String[]{ "Row1", "0.0", "0.0", "0.0", "0.0", "0.0" }
    };
    DataColumn[] columns = new DataColumn[]{
            new DataColumn("Col1 Col1 Col1"),
            new DataColumn("Col2 Col2 Col2 Col2"),
            new DataColumn("Col3"),
            new DataColumn("Col4 Col4"),
            new DataColumn("Col5")
    };

    @Test
    public void parseCategory_test() {
        DataCategory category = Squid3DataParser.parseCategory(cells, 1, 4);
        assertEquals("Incorrect DataCategory label.","Cat1", category.getLabel());
        assertEquals("Incorrect # of DataColumns.", 3, category.getChildren().size());

        for (DataNode node : category.getChildren()) {
            assertThat(node, instanceOf(DataColumn.class));
        }
        assertEquals("Col1 Col1 Col1", category.getChildren().get(0).getLabel());
        assertEquals("Col2 Col2 Col2 Col2", category.getChildren().get(1).getLabel());
        assertEquals("Col3", category.getChildren().get(2).getLabel());
    }

    @Test
    public void parseEmptyCategory_test() {
        DataCategory category = Squid3DataParser.parseCategory(cells, 4, -1);
    }

    @Test
    public void parseDataSegment_test() {
        DataSegment segment = Squid3DataParser.parseDataSegment(cells, 5, -1, Arrays.asList(columns));
        assertEquals("Incorrect DataSegment label.","Seg1", segment.getLabel());
        assertEquals("Incorrect # of DataRows.", 1, segment.getChildren().size());

        for (DataNode node : segment.getChildren()) {
            assertThat(node, instanceOf(DataRow.class));
        }
        DataRow row = segment.getRow(0);
        for (DataColumn column : columns) {
            assertEquals("0.0", row.getValuePropertyForColumn(column).get().toString());
        }
    }

//    @Test
//    public void parseColumnTree_test() {
//
//    }
//
//    @Test
//    public void parseData_test() {
//
//    }

}
