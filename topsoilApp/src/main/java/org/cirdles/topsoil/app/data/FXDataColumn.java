package org.cirdles.topsoil.app.data;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.data.DataColumn;

public class FXDataColumn<T> extends FXDataComponent<DataColumn<?>> implements DataColumn<T> {

    private ListProperty<FXDataColumn<?>> childColumns = new SimpleListProperty<>(
            // Initializes the list property with an observable list that listens to changes in the properties of each
            // column
            FXCollections.observableArrayList(column -> {
                return new Observable[]{
                        column.titleProperty(),
                        column.selectedProperty(),
                        column.childColumnsProperty()
                };
            })
    );
    public final ReadOnlyListProperty<FXDataColumn<?>> childColumnsProperty() {
        return childColumns;
    }
    @Override
    public final ObservableList<FXDataColumn<?>> getChildren() {
        return childColumns.get();
    }

    private Class<T> valueType;
    private T defaultValue;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public FXDataColumn(DataColumn<T> column) {
        super(column.getTitle(), column.isSelected());
        this.valueType = column.getType();
        this.defaultValue = column.getDefaultValue();

        for (DataColumn<?> child : column.getChildren()) {
            childColumns.add(new FXDataColumn<>(child));
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Class<T> getType() {
        return valueType;
    }

}
