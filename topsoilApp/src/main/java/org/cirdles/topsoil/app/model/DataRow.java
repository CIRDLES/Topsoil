package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.generic.BranchNode;
import org.cirdles.topsoil.app.model.generic.DataValue;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author marottajb
 */
public class DataRow extends BranchNode<DataValue<?>> {

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

    public <T extends Serializable> DataValue<T> getValueForColumn(DataColumn<T> column) {
        DataValue<T> value = null;
        for (DataValue<?> val : this.getChildren()) {
            if (val.getColumn() == column) {
                value = (DataValue<T>) val;
                break;
            }
        }
        return value;
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
            DataValue<?> thisValue;
            DataValue<?> otherValue;
            for (int i = 0; i < this.getChildren().size(); i++) {
                thisValue = this.getChildren().get(i);
                otherValue = other.getChildren().get(i);
                if (! thisValue.equals(otherValue)) {
                    return false;
                }
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
