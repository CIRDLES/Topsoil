package org.cirdles.topsoil.app.data.row;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataRowTest {

    private static DataColumn<Number> columnOne = DataColumn.numberColumn("one");
    private static DataColumn<String> columnTwo = DataColumn.stringColumn("two");

    private static Property<Number> propertyOne = new SimpleDoubleProperty(1.0);
    private static Property<Number> propertyTwo = new SimpleDoubleProperty(2.0);
    private static Property<String> propertyThree = new SimpleStringProperty("three");
    private static Property<String> propertyFour = new SimpleStringProperty("four");

    private static DataRow rowOne, rowOneCopy, rowTwo, rowThree, rowFour, rowFive, rowSix;

    @BeforeClass
    public static void setup() {
        rowOne = new DataRow("row");
        rowOne.setPropertyForColumn(columnOne, propertyOne);
        rowOne.setPropertyForColumn(columnTwo, propertyThree);

        rowOneCopy = new DataRow(rowOne.getLabel());
        rowOneCopy.setPropertyForColumn(columnOne, new SimpleDoubleProperty((double) rowOne.getPropertyForColumn(columnOne).getValue()));
        rowOneCopy.setPropertyForColumn(columnTwo, new SimpleStringProperty(rowOne.getPropertyForColumn(columnTwo).getValue()));

        rowTwo = new DataRow(rowOne.getLabel());
        rowTwo.setPropertyForColumn(columnOne, propertyTwo);
        rowTwo.setPropertyForColumn(columnTwo, propertyFour);

        rowThree = new DataRow("ROW");
        rowThree.setPropertyForColumn(columnOne, rowOne.getPropertyForColumn(columnOne));
        rowThree.setPropertyForColumn(columnTwo, rowOne.getPropertyForColumn(columnTwo));

        rowFour = new DataRow(rowOne.getLabel());
        rowFour.setPropertyForColumn(columnOne, rowOne.getPropertyForColumn(columnOne));
        rowFour.setPropertyForColumn(columnTwo, rowOne.getPropertyForColumn(columnTwo));
        rowFour.setSelected(! rowOne.isSelected());

        rowFive = new DataRow(rowOne.getLabel());
        rowFive.setPropertyForColumn(columnOne, rowOne.getPropertyForColumn(columnOne));

        rowSix = new DataRow(rowOne.getLabel());
        rowSix.setPropertyForColumn(columnOne, rowOne.getPropertyForColumn(columnOne));
        rowSix.setPropertyForColumn(columnTwo, null);
    }

    @Test
    public void equals_test() {
        assertEquals(rowOne, rowOne);
        assertEquals(rowOne, rowOneCopy);
        assertNotEquals(rowOne, rowTwo);
        assertNotEquals(rowOne, rowThree);
        assertNotEquals(rowOne, rowFour);
        assertNotEquals(rowOne, rowFive);
        assertNotEquals(rowOne, rowSix);
        assertNotEquals(rowOne, "rowSeven");
    }

}
