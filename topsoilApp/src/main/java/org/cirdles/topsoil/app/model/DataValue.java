package org.cirdles.topsoil.app.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.StringConverter;
import org.cirdles.topsoil.app.model.composite.DataLeaf;

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

    private transient ObjectProperty<T> value = new SimpleObjectProperty<>(null);
    public ObjectProperty<T> valueProperty() {
        return value;
    }
    public final T getValue() {
        return value.get();
    }
    public final void setValue(T val) {
        value.set(val);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataValue(DataColumn<T> column, T value, StringConverter<T> converter) {
        super();
        this.column = column;
        this.value.set(value);
        this.label.set(converter.toString(value));
        this.label.bind(Bindings.createStringBinding(() -> converter.toString(value), this.value));
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
                if (value.equals(other.getValue())){
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
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("column", column);
        out.writeFields();
        out.writeObject(value.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = in.readFields();
        column = (DataColumn<T>) fields.get("column", null);
        value.set((T) in.readObject());
    }

}
