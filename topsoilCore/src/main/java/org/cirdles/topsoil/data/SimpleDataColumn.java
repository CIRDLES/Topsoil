package org.cirdles.topsoil.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleDataColumn<T> extends AbstractDataComponent<DataColumn<?>> implements DataColumn<T> {

    private final List<SimpleDataColumn<?>> children = new ArrayList<>();

    private final T defaultValue;
    private final Class<T> valueType;
    private DataColumn<T> dependentColumn;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SimpleDataColumn(String title) {
        this(title, true, null, null);
    }

    public SimpleDataColumn(String title, boolean selected, T defaultValue, Class<T> valueType) {
        this(title, selected, defaultValue, valueType, null);
    }

    private SimpleDataColumn(
            String title,
            boolean selected,
            T defaultValue,
            Class<T> valueType,
            SimpleDataColumn<?>[] children
    ) {
        super(title, selected);

        this.defaultValue = defaultValue;
        this.valueType = valueType;

        if (children != null) {
            this.children.addAll(Arrays.asList(children));
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public List<SimpleDataColumn<?>> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Class<T> getType() {
        return valueType;
    }

    @Override
    public DataColumn<T> getDependentColumn() {
        return this.dependentColumn;
    }

    @Override
    public void setDependentColumn(DataColumn<T> column) {
        this.dependentColumn = column;
    }

}
