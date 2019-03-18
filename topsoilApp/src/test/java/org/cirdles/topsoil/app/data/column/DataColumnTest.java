package org.cirdles.topsoil.app.data.column;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataColumnTest {

    private static DataColumn<String> stringColumn = DataColumn.stringColumn("column");
    private static DataColumn<String> stringColumnCopy = DataColumn.stringColumn("column");
    private static DataColumn<Number> numberColumn = DataColumn.numberColumn("column");
    private static DataColumn<String> diffLabelColumn = DataColumn.stringColumn("COLUMN");
    private static DataColumn<String> deselectedColumn = DataColumn.stringColumn("column");

    @BeforeClass
    public static void setup() {
        deselectedColumn.setSelected(false);
    }

    @Test
    public void equals_test() {
        assertEquals(stringColumn, stringColumn);
        assertEquals(stringColumn, stringColumnCopy);
        assertNotEquals(stringColumn, numberColumn);
        assertNotEquals(stringColumn, diffLabelColumn);
        assertNotEquals(stringColumn, deselectedColumn);
    }

}
