package org.cirdles.topsoil.app.data.column;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataCategoryTest {

    @Test
    public void equals_test() {
        DataColumn col1 = new DataColumn<>("Col1", String.class);
        DataColumn col2 = new DataColumn<>("Col2", Double.class);
        DataCategory oracle = new DataCategory("Cat1", col1, col2);
        DataCategory same = new DataCategory("Cat1", col1, col2);
        DataCategory differentLabel = new DataCategory("Cat2", col1, col2);
        DataCategory differentChildren = new DataCategory("Cat1", same);

        assertEquals(oracle, same);
        assertNotEquals(oracle, differentLabel);
        assertNotEquals(oracle, differentChildren);
    }

}
