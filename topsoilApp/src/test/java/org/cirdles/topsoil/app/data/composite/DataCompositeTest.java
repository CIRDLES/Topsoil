package org.cirdles.topsoil.app.data.composite;

import org.cirdles.topsoil.app.data.column.ColumnTree;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class DataCompositeTest {

    static DataColumn col1 = new DataColumn<>("col1", String.class);
    static DataColumn col2 = new DataColumn<>("col2", String.class);
    static DataColumn col3 = new DataColumn<>("col3", String.class);
    static DataColumn col4 = new DataColumn<>("col4", String.class);

    static DataCategory cat1 = new DataCategory("cat1", col1, col3);
    static DataCategory cat2 = new DataCategory("cat2", col2);

    static ColumnTree columnTree = new ColumnTree(col4, cat1, cat2);

    @Test
    public void order_test() {
        List<DataColumn<?>> columns = columnTree.getLeafNodes();
        assertSame(columns.get(0), col4);
        assertSame(columns.get(1), col1);
        assertSame(columns.get(2), col3);
        assertSame(columns.get(3), col2);
    }

    @Test
    public void findIn_test() {
        assertSame(col1, ColumnTree.findIn("col1", columnTree));
        assertSame(cat2, ColumnTree.findIn("cat2", columnTree));
    }

    @Test
    public void countLeafNodes_test() {
        assertEquals(4, columnTree.countLeafNodes());
    }

    @Test
    public void getDepth_test() {
        assertEquals(2, columnTree.getDepth());
    }

}
