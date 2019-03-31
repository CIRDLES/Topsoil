package org.cirdles.topsoil.constant;

import javafx.beans.property.*;

/**
 * @author marottajb
 */
public enum Lambda implements Constant<Number> {

    U234("Lambda 234", "234", 2.82206e-6),
    U235("Lambda 235", "235", 9.8485e-10),
    U238("Lambda 238", "238", 1.55125e-10),

    Th230("Lambda 230", "230", 9.1705e-6);

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private StringProperty title = new SimpleStringProperty();
    public StringProperty titleProperty() {
        return title;
    }
    public final String getTitle() {
        return title.get();
    }

    private StringProperty abbreviation = new SimpleStringProperty();
    public StringProperty abbreviationProperty() {
        return abbreviation;
    }
    public final String getAbbreviation() {
        return abbreviation.get();
    }

    private ObjectProperty<Number> value = new SimpleObjectProperty<>();
    public ObjectProperty<Number> valueProperty() {
        return value;
    }
    public final Number getValue() {
        return value.get();
    }
    public final void setValue(Number n) {
        value.set(n);
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final Number defaultValue;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    Lambda(String title, String abbreviation, Number defaultValue) {
        this.title.set(title);
        this.abbreviation.set(abbreviation);
        this.defaultValue = defaultValue;
        setValue(this.defaultValue);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void resetToDefault() {
        setValue(this.defaultValue);
    }
}
