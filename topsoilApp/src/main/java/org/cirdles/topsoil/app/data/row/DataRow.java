package org.cirdles.topsoil.app.data.row;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataLeaf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single entry of data as a set of column/value mappings.
 *
 * @author marottajb
 */
public class DataRow extends DataLeaf {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final ObservableMap<DataColumn<?>, DataValue<?>> values = FXCollections.observableHashMap();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataRow(String label) {
        super(label);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Map<DataColumn<?>, DataValue<?>> getValueMap() {
        return values;
    }

    /**
     * Returns the property for the provided {@code DataColumn}.
     *
     * @param column    DataColumn
     * @param <T>       the type of the data for the DataColumn
     * @return          the row's property for column
     */
    public <T> DataValue<T> getValueForColumn(DataColumn<T> column) {
        return (DataValue<T>) values.get(column);
    }

    public <T> void setValueForColumn(DataColumn<T> column, T value) {
        DataValue<T> dataValue = getValueForColumn(column);
        if (dataValue != null) {
            dataValue.setValue(value);
        } else {
            values.put(column, new DataValue<>(value));
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DataRow) {
            DataRow other = (DataRow) object;
            if (! other.getLabel().equals(this.getLabel())) {
                return false;
            }
            if (other.isSelected() != this.isSelected()) {
                return false;
            }
            if (other.getValueMap().size() != this.getValueMap().size()) {
                return false;
            }
            DataColumn<?> column;
            DataValue<?> thisValue, thatValue;
            for (Map.Entry<DataColumn<?>, DataValue<?>> entry : other.getValueMap().entrySet()) {
                column = entry.getKey();
                thisValue = this.getValueMap().get(column);
                thatValue = other.getValueMap().get(column);
                if (thisValue != thatValue) {
                    if (thisValue != null && thatValue != null) {
                        if (! thisValue.getValue().equals(thatValue.getValue())) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        List<Object> objects = new ArrayList<>();
        objects.add(getLabel());
        objects.add(isSelected());
        for (Map.Entry<DataColumn<?>, DataValue<?>> entry : getValueMap().entrySet()) {
            objects.add(new Pair(entry.getKey(), entry.getValue()));
        }
        return Objects.hash(objects.toArray());
    }

    private static class Pair {

        private DataColumn column;
        private DataValue value;

        Pair(DataColumn column, DataValue value) {
            this.column = column;
            this.value = value;
        }

    }

    public static class DataValue<T> {

        //**********************************************//
        //                  PROPERTIES                  //
        //**********************************************//

        private ObjectProperty<T> value;
        public ObjectProperty<T> valueProperty() {
            if (value == null) {
                value = new SimpleObjectProperty<>();
            }
            return value;
        }
        public final T getValue() {
            return valueProperty().get();
        }
        public final void setValue(T value) {
            valueProperty().set(value);
        }

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        public DataValue(T value) {
            setValue(value);
        }

    }
}
