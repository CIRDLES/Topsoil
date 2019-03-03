package org.cirdles.topsoil.app.data.row;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.column.NumberColumn;
import org.cirdles.topsoil.app.data.column.StringColumn;
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
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -8788288059689780519L;

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

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        SerializableValue<?> sValue;
        // TODO Ennsure that column and property have same parameterized type
        out.writeInt(properties.size());
        for (Map.Entry<DataColumn<?>, Property<?>> entry : properties.entrySet()) {
            if (entry.getKey() instanceof NumberColumn) {
                sValue = newSerializableValue((NumberColumn) entry.getKey(), (Property<Number>) entry.getValue());
            } else {
                sValue = newSerializableValue((StringColumn) entry.getKey(), (Property<String>) entry.getValue());
            }
            out.writeObject(sValue);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        properties = new HashMap<>();

        SerializableValue<?> sValue;
        int numValues = in.readInt();
        for (int i = 0; i < numValues; i++) {
            sValue = (SerializableValue<?>) in.readObject();
            properties.put(sValue.getColumn(), sValue.getProperty());
        }
    }

    private <T extends Serializable> SerializableValue<T> newSerializableValue(DataColumn<T> column, Property<T> property) {
        return new SerializableValue<>(column, property);
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    class SerializableValue<T extends Serializable> implements Serializable {

        private DataColumn<T> column;
        private T value;
        private Class<? extends Property> propertyClass;

        SerializableValue(DataColumn<T> column, Property<T> property) {
            this.column = column;
            this.value = property.getValue();
            this.propertyClass = property.getClass();
        }

        DataColumn<T> getColumn() {
            return column;
        }

        Property<T> getProperty() {
            try {
                if (propertyClass == SimpleObjectProperty.class) {
                    return propertyClass.getConstructor(Object.class).newInstance(value);
                } else {
                    return propertyClass.getConstructor(value.getClass()).newInstance(value);
                }
            } catch (NoSuchMethodException|IllegalAccessException|InstantiationException|InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
