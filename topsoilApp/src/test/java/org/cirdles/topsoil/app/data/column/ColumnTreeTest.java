package org.cirdles.topsoil.app.data.column;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class ColumnTreeTest {

    static ColumnTree oracle;

    @Test
    public void equals_test() {
        oracle = new ColumnTree(
                new DataCategory("Cat1",
                                 new DataColumn<>("Co1", String.class),
                                 new DataColumn<>("Col2", String.class)
                )
        );
        ColumnTree same = new ColumnTree(
                new DataCategory("Cat1",
                                 new DataColumn<>("Co1", String.class),
                                 new DataColumn<>("Col2", String.class)
                )
        );
        ColumnTree different = new ColumnTree(
                new DataColumn<>("Co1", String.class),
                new DataColumn<>("Col2", String.class)
        );
        assertEquals(oracle, same);
        assertNotEquals(oracle, different);
    }

}
