package org.cirdles.topsoil.data;

import org.cirdles.topsoil.symbols.SimpleSymbolMap;
import org.cirdles.topsoil.symbols.SymbolMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleDataRow extends AbstractDataComponent<DataRow> implements DataRow {

    private final List<SimpleDataRow> children = new ArrayList<>();
    private SymbolMap<DataColumn<?>> columnMap = new SimpleSymbolMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SimpleDataRow(String title) {
        this(title, true);
    }

    public SimpleDataRow(String title, boolean selected, SimpleDataRow... children) {
        this(title, selected, null, children);
    }

    public SimpleDataRow(String title, boolean selected, Map<DataColumn<?>, Object> map) {
        this(title, selected, map, null);
    }

    private SimpleDataRow(String title, boolean selected, Map<DataColumn<?>, Object> map, SimpleDataRow[] children) {
        super(title, selected);
        if (map != null) {
            columnMap.putAll(map);
        }
        if (children != null) {
            this.children.addAll(Arrays.asList(children));
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public List<SimpleDataRow> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public Map<? extends DataColumn<?>, Object> getColumnValueMap() {
        return new HashMap<>(columnMap);
    }

    @Override
    public <T> T getValueForColumn(DataColumn<T> column) {
        return columnMap.getAndCast(column);
    }

    @Override
    public <T> void setValueForColumn(DataColumn<T> column, T value) {
        columnMap.put(column, value);
    }

}
