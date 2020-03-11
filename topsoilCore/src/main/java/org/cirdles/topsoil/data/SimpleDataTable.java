package org.cirdles.topsoil.data;

import java.util.List;

public class SimpleDataTable extends AbstractDataTable<SimpleDataColumn<?>, SimpleDataRow> {

    public SimpleDataTable(DataTemplate template, String title) {
        this(template, title, null, null, null);
    }

    public SimpleDataTable(DataTemplate template, String title, Uncertainty uncertainty, List<SimpleDataColumn<?>> columns, List<SimpleDataRow> rows) {
        super(template, title, uncertainty, columns, rows);
    }

}
