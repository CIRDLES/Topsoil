package org.cirdles.topsoil.app.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

/**
 * @author marottajb
 */
public class DecimalStringConverter extends StringConverter<Double> {

    private static String DEFAULT_PATTERN = "0.0#";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private transient DecimalFormat df = new DecimalFormat(DEFAULT_PATTERN);

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private transient StringProperty pattern = new SimpleStringProperty(DEFAULT_PATTERN);
    public StringProperty patternProperty() {
        return pattern;
    }
    public final String getPattern() {
        return pattern.get();
    }
    public final void setPattern(String str) {
        df.applyPattern(str);
        pattern.set(str);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DecimalStringConverter() {
        super();
    }

    public DecimalStringConverter(String pattern) {
        this();
        setPattern(pattern);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public String toString(Double value) {
        return df.format(value);
    }

    @Override
    public Double fromString(String string) {
        return Double.parseDouble(string);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeFields();
        out.writeObject(pattern.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readFields();
        setPattern(String.valueOf(in.readObject()));
    }

}
