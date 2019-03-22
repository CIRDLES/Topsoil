package org.cirdles.topsoil.app.data.row;

import org.cirdles.topsoil.app.data.column.DataColumn;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataRootTest {

    private static DataColumn<Number> columnOne = DataColumn.numberColumn("one");

    private static DataRow rowOne = new DataRow("row");

    private static DataSegment segmentOne, segmentTwo, segmentThree;

    private static DataRoot rootOne, rootOneCopy, rootTwo, rootThree;

    @BeforeClass
    public static void setup() {
        rowOne.setValueForColumn(columnOne, 1.0);

        segmentOne = new DataSegment("one", rowOne);
        segmentTwo = new DataSegment("two", rowOne);
        segmentThree = new DataSegment("three", rowOne);

        rootOne = new DataRoot(segmentOne, segmentTwo);
        rootOneCopy = new DataRoot(rootOne.getChildren().toArray(new DataSegment[]{}));
        rootTwo = new DataRoot(segmentOne, segmentThree);
        rootThree = new DataRoot(segmentOne);
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
