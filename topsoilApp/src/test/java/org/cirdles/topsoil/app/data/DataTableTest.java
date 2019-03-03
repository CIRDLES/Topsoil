package org.cirdles.topsoil.app.data;

import javafx.beans.property.SimpleDoubleProperty;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.NumberColumn;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.file.parser.Squid3DataParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DataTableTest {

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

    private static DataTable oracle;

    @BeforeClass
    public static void setup() {
        oracle = new Squid3DataParser().parseDataTable(CONTENT, ",", "CONTENT");
    }

    @Test
    public void getRowByIndex_test() {
        assertEquals("Seg1:Row1", oracle.getRowByIndex(0).getLabel());
        assertEquals("Seg2:Row1", oracle.getRowByIndex(1).getLabel());
    }

    @Test
    public void equals_test() {
        NumberColumn col1, col2, col3, col4, col5;
        DataRow row1, row2;
        DataTable table = new DataTable(DataTemplate.SQUID_3, "CONTENT",
                new ColumnRoot(
                        new DataCategory("Cat1",
                                col1 = new NumberColumn("Col1 Col1 Col1"),
                                col2 = new NumberColumn("Col2 Col2 Col2 Col2"),
                                col3 = new NumberColumn("Col3")
                        ),
                        new DataCategory("Cat2",
                                col4 = new NumberColumn("Col4 Col4"),
                                col5 = new NumberColumn("Col5")
                        ),
                        new DataCategory("Cat3")
                ),
               new DataRoot(
                        new DataSegment("Seg1",
                                row1 = new DataRow("Seg1:Row1")
                        ),
                        new DataSegment("Seg2",
                                row2 = new DataRow("Seg2:Row1")
                        )
                )
        );
        row1.setPropertyForColumn(col1, new SimpleDoubleProperty(1.0));
        row1.setPropertyForColumn(col2, new SimpleDoubleProperty(2.0));
        row1.setPropertyForColumn(col3, new SimpleDoubleProperty(3.0));
        row1.setPropertyForColumn(col4, new SimpleDoubleProperty(4.0));
        row1.setPropertyForColumn(col5, new SimpleDoubleProperty(5.0));

        row2.setPropertyForColumn(col1, new SimpleDoubleProperty(1.0));
        row2.setPropertyForColumn(col2, new SimpleDoubleProperty(2.0));
        row2.setPropertyForColumn(col3, new SimpleDoubleProperty(3.0));
        row2.setPropertyForColumn(col4, new SimpleDoubleProperty(4.0));
        row2.setPropertyForColumn(col5, new SimpleDoubleProperty(5.0));

        assertEquals(oracle, table);
    }

}
