package org.cirdles.topsoil.constant;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author marottajb
 */
public class SimpleConstant<T> {

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private StringProperty title = new SimpleStringProperty();
    public StringProperty titleProperty() {
        return title;
    }
    public String getTitle() {
        return title.get();
    }

    private ObjectProperty<T> value = new SimpleObjectProperty<>();
    public ObjectProperty<T> valueProperty() {
        return value;
    }
    public T getValue() {
        return value.get();
    }
    public void setValue(T obj) {
        value.set(obj);
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private T defaultValue;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SimpleConstant(String title, T value) {
        this.title.set(title);
        this.defaultValue = value;
        setValue(defaultValue);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void resetToDefault() {
        setValue(defaultValue);
    }

}
