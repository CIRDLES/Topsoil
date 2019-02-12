package org.cirdles.topsoil.constant;

import javafx.beans.property.*;

/**
 * @author marottajb
 */
public enum Lambda implements Constant<Double> {

    U234("Lambda U234", "U234", 2.82206e-6),
    U235("Lambda U235", "U235", 9.8485e-10),
    U238("Lambda U238", "U238", 1.55125e-10),
    R238_235S("Lambda R238_235S", "R238_235S", 137.8),

    Th230("Lambda Th230", "Th230", 9.1705e-6);

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

    private DoubleProperty value = new SimpleDoubleProperty();
    public DoubleProperty valueProperty() {
        return value;
    }
    public final Double getValue() {
        return value.get();
    }
    public final void setValue(double d) {
        value.set(d);
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final double defaultValue;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    Lambda(String title, String abbreviation, double defaultValue) {
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
