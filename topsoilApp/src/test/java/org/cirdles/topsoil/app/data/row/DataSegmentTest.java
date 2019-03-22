package org.cirdles.topsoil.app.data.row;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataSegmentTest {

    private static DataColumn<Number> columnOne = DataColumn.numberColumn("one");
    private static DataColumn<String> columnTwo = DataColumn.stringColumn("two");

    private static Property<Number> propertyOne = new SimpleDoubleProperty(1.0);
    private static Property<Number> propertyTwo = new SimpleDoubleProperty(2.0);
    private static Property<String> propertyThree = new SimpleStringProperty("three");
    private static Property<String> propertyFour = new SimpleStringProperty("four");

    private static DataRow rowOne, rowTwo, rowThree;

    private static DataSegment segmentOne, segmentOneCopy, segmentTwo, segmentThree, segmentFour, segmentFive;

    @BeforeClass
    public static void setup() {
        rowOne = new DataRow("one");
        rowOne.setValueForColumn(columnOne, propertyOne);
        rowOne.setValueForColumn(columnTwo, propertyThree);

        rowTwo = new DataRow("two");
        rowTwo.setValueForColumn(columnOne, propertyTwo);
        rowTwo.setValueForColumn(columnTwo, propertyFour);

        rowThree = new DataRow("three");
        rowOne.setValueForColumn(columnOne, propertyOne);
        rowOne.setValueForColumn(columnTwo, propertyFour);

        segmentOne = new DataSegment("segment", rowOne, rowTwo);
        segmentOneCopy = new DataSegment(segmentOne.getLabel(), segmentOne.getChildren().toArray(new DataRow[]{}));
        segmentTwo = new DataSegment("SEGMENT", segmentOne.getChildren().toArray(new DataRow[]{}));
        segmentThree = new DataSegment(segmentOne.getLabel(), rowOne, rowThree);
        segmentFour = new DataSegment(segmentOne.getLabel(), segmentOne.getChildren().toArray(new DataRow[]{}));
        segmentFour.setSelected(! segmentOne.isSelected());
        segmentFive = new DataSegment(segmentOne.getLabel(), rowOne);
    }

    @Test
    public void equals_test() {
        assertEquals(segmentOne, segmentOne);
        assertEquals(segmentOne, segmentOneCopy);
        assertNotEquals(segmentOne, segmentTwo);
        assertNotEquals(segmentOne, segmentThree);
        assertNotEquals(segmentOne, segmentFour);
        assertNotEquals(segmentOne, segmentFive);
        assertNotEquals(segmentOne, "segmentSix");
    }
}
