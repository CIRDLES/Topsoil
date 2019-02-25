package org.cirdles.topsoil.app.data.row;

import javafx.beans.property.Property;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataLeaf;

import java.io.Serializable;
import java.util.HashMap;

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

    private HashMap<DataColumn<?>, Property<?>> properties = new HashMap<>();

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
        return "DataRow(\"" + this.label.get() + "\")" + properties.toString();
    }

}
