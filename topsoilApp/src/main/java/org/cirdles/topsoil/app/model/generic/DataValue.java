package org.cirdles.topsoil.app.model.generic;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.StringConverter;
import org.cirdles.topsoil.app.model.DataColumn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author marottajb
 */
public abstract class DataValue<T extends Serializable> extends LeafNode {

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
        out.writeFields();
        out.writeObject(value.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readFields();
        value.set((T) in.readObject());
    }

}
