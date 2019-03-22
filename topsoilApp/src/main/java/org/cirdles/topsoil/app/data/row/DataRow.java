package org.cirdles.topsoil.app.data.row;

import javafx.beans.property.Property;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataLeaf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    private transient Map<DataColumn<?>, Property<?>> properties = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataRow(String label) {
        super(label);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Map<DataColumn<?>, Property<?>> getProperties() {
        return properties;
    }

    /**
     * Returns the property for the provided {@code DataColumn}.
     *
     * @param column    DataColumn
     * @param <T>       the type of the data for the DataColumn
     * @return          the row's property for column
     */
    public <T> Property<T> getPropertyForColumn(DataColumn<T> column) {
        return (Property<T>) properties.get(column);
    }

    public <T> Property<T> setPropertyForColumn(DataColumn<T> column, Property<T> property) {
        return (Property<T>) properties.put(column, property);
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
            if (other.getProperties().size() != properties.size()) {
                return false;
            }
            DataColumn<?> column;
            Property<?> thisProperty, thatProperty;
            for (Map.Entry<DataColumn<?>, Property<?>> entry : other.properties.entrySet()) {
                column = entry.getKey();
                thisProperty = properties.get(column);
                thatProperty = other.properties.get(column);
                if (thisProperty != thatProperty) {
                    if (thisProperty != null && thatProperty != null) {
                        if (! thisProperty.getValue().equals(thatProperty.getValue())) {
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
        for (Map.Entry<DataColumn<?>, Property<?>> entry : properties.entrySet()) {
            objects.add(new Pair(entry.getKey(), entry.getValue()));
        }
        return Objects.hash(objects.toArray());
    }

    private class Pair {

        private DataColumn column;
        private Property property;

        Pair(DataColumn column, Property property) {
            this.column = column;
            this.property = property;
        }

    }

}
