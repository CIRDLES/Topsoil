package org.cirdles.topsoil.app.data.value;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.StringConverter;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataLeaf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Represents a mapping of a {@link DataColumn} to some value. A string converter must be provided so that the label
 * property can be properly updated with a String format of the value.
 *
 * @author marottajb
 */
public abstract class DataValue<T extends Serializable> extends DataLeaf {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static final long serialVersionUID = 3466168248198642427L;
    private DataColumn<T> column;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private transient ObjectProperty<T> value;
    public ObjectProperty<T> valueProperty() {
        if (value == null) {
            value = new SimpleObjectProperty<>(null);
        }
        return value;
    }
    public final T getValue() {
        return valueProperty().get();
    }
    public final void setValue(T val) {
        valueProperty().set(val);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataValue(DataColumn<T> column, T value, StringConverter<T> converter) {
        super();
        this.column = column;
        setValue(value);
        setLabel(converter.toString(value));
        labelProperty().bind(Bindings.createStringBinding(() -> converter.toString(value), this.value));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public DataColumn<T> getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataValue) {
            DataValue<T> other = (DataValue<T>) object;
            if (! super.equals(other)) {
                return false;
            }
            if (! column.equals(((DataValue) object).getColumn())) {
                return false;
            }
            if (! getValue().equals(other.getValue())){
                return false;
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
        out.writeObject(valueProperty().get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setValue((T) in.readObject());
    }

}
