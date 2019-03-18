package org.cirdles.topsoil.app.data.column;

import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataCategoryTest {

    private static DataColumn<String> stringColumn = DataColumn.stringColumn("string");
    private static DataColumn<Number> numberColumn = DataColumn.numberColumn("number");
    private static DataColumn<String> thirdColumn = DataColumn.stringColumn("third");

    private static DataCategory categoryOne, categoryOneCopy, categoryTwo, categoryThree, categoryFour, categoryFive;

    @BeforeClass
    public static void setup() {
        categoryOne = new DataCategory("category", stringColumn, numberColumn);
        categoryOneCopy = new DataCategory(categoryOne.getLabel(), categoryOne.getChildren().toArray(new DataComponent[]{}));
        categoryTwo = new DataCategory("CATEGORY", categoryOne.getChildren().toArray(new DataComponent[]{}));
        categoryThree = new DataCategory(categoryOne.getLabel(), stringColumn);
        categoryFour = new DataCategory(categoryOne.getLabel(), stringColumn, thirdColumn);
        categoryFive = new DataCategory(categoryOne.getLabel(), categoryOne.getChildren().toArray(new DataComponent[]{}));
        categoryFive.setSelected(false);
    }

    @Test
    public void equals_test() {
        assertEquals(categoryOne, categoryOne);
        assertEquals(categoryOne, categoryOneCopy);
        assertNotEquals(categoryOne, categoryTwo);
        assertNotEquals(categoryOne, categoryThree);
        assertNotEquals(categoryOne, categoryFour);
        assertNotEquals(categoryOne, categoryFive);
    }

}
