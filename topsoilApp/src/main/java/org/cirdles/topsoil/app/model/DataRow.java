package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.composite.DataComposite;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

/**
 * Represents a single entry of data as a set of column/value mappings.
 *
 * @author marottajb
 */
public class DataRow extends DataComposite<DataValue<?>> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -1183289797433461340L;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataRow(String label) {
        super(label);
    }

    public DataRow(String label, List<DataValue<?>> values) {
        this(label);
        this.getChildren().addAll(values);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns the value for the provided {@code DataColumn}.
     *
     * @param column    DataColumn
     * @param <T>       the type of the data for the DataColumn
     * @return          the row's value for column
     */
    public <T extends Serializable> DataValue<T> getValueForColumn(DataColumn<T> column) {
        for (DataValue<?> val : this.getChildren()) {
            if (val.getColumn().equals(column)) {
                return (DataValue<T>) val;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataRow) {
            DataRow other = (DataRow) object;
            if (! this.getLabel().equals(other.getLabel())) {
                return false;
            }
            if (this.isSelected() != other.isSelected()) {
                return false;
            }
            if (this.getChildren().size() != other.getChildren().size()) {
                return false;
            }
            if (! this.getChildren().equals(other.getChildren())) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        for (DataValue<?> value : this.getChildren()) {
            joiner.add("\"" + value.getColumn().getLabel() + "\" => " + value.getLabel());
        }
        return "DataRow(\"" + this.label.get() + "\"){ " + joiner.toString() + " }";
    }

}
