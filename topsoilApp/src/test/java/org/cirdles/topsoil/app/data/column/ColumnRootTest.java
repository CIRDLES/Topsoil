package org.cirdles.topsoil.app.data.column;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ColumnRootTest {

    private static DataColumn<String> columnOne = DataColumn.stringColumn("one");
    private static DataColumn<String> columnTwo = DataColumn.stringColumn("two");
    private static DataColumn<String> columnThree = DataColumn.stringColumn("three");

    private static DataCategory categoryOne = new DataCategory("one", columnOne, columnTwo);
    private static DataCategory categoryTwo = new DataCategory("two", columnTwo, columnThree);

    private static ColumnRoot rootOne, rootOneCopy, rootTwo, rootThree;

    @BeforeClass
    public static void setup() {
        rootOne = new ColumnRoot(categoryOne, columnThree);
        rootOneCopy = new ColumnRoot(categoryOne, columnThree);
        rootTwo = new ColumnRoot(categoryOne);
        rootThree = new ColumnRoot(columnOne, categoryTwo);
    }

    @Test
    public void equals_test() {
        assertEquals(rootOne, rootOne);
        assertEquals(rootOne, rootOneCopy);
        assertNotEquals(rootOne, rootTwo);
        assertNotEquals(rootOne, rootThree);
        assertNotEquals(rootOne, "rootFour");
    }

}
