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

    private static final long serialVersionUID = 7416030591288798050L;
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
        boolean eq = super.equals(object);
        if (object instanceof DataValue) {
            if (column.equals(((DataValue) object).getColumn())) {
                DataValue<T> other = (DataValue<T>) object;
                if (valueProperty().equals(other.getValue())){
                    eq = true;
                }
            }
        }
        return eq;
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
