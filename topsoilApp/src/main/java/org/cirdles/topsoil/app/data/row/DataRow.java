package org.cirdles.topsoil.app.data.row;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataLeaf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
    public <T extends Serializable> Property<T> getPropertyForColumn(DataColumn<T> column) {
        return (Property<T>) properties.get(column);
    }

    public <T extends Serializable> Property<T> setPropertyForColumn(DataColumn<T> column, Property<T> property) {
        return (Property<T>) properties.put(column, property);
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataRow) {
            DataRow other = (DataRow) object;
            for (Map.Entry<DataColumn<?>, Property<?>> entry : properties.entrySet()) {
                if (entry.getValue().equals(other.getPropertyForColumn(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
