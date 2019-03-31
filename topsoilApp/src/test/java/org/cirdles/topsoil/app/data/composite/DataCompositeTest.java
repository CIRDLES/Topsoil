package org.cirdles.topsoil.app.data.composite;

import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.junit.Test;

import java.util.List;

import static org.cirdles.topsoil.app.data.column.DataColumn.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class DataCompositeTest {

    static DataColumn col1 = stringColumn("col1");
    static DataColumn col2 = stringColumn("col2");
    static DataColumn col3 = stringColumn("col3");
    static DataColumn col4 = stringColumn("col4");

    static DataCategory cat1 = new DataCategory("cat1", col1, col3);
    static DataCategory cat2 = new DataCategory("cat2", col2);

    static ColumnRoot columnRoot = new ColumnRoot(col4, cat1, cat2);

    @Test
    public void order_test() {
        List<DataColumn<?>> columns = columnRoot.getLeafNodes();
        assertSame(columns.get(0), col4);
        assertSame(columns.get(1), col1);
        assertSame(columns.get(2), col3);
        assertSame(columns.get(3), col2);
    }

    @Test
    public void findIn_test() {
        assertSame(col1, ColumnRoot.findIn("col1", columnRoot));
        assertSame(cat2, ColumnRoot.findIn("cat2", columnRoot));
    }

    @Test
    public void countLeafNodes_test() {
        assertEquals(4, columnRoot.countLeafNodes());
    }

    @Test
    public void getDepth_test() {
        assertEquals(2, columnRoot.getDepth());
    }

}
