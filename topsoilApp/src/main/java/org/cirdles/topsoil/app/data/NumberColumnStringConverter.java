package org.cirdles.topsoil.app.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.StringConverter;
import org.cirdles.topsoil.data.TableUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A custom {@code StringConverter} that uses a {@code DecimalFormat} to enforce a specific number of fraction digits on
 * converted strings. If the number provided has fewer than the set number of fraction digits, the number is padded with
 * whitespace on the right.
 */
public class NumberColumnStringConverter extends StringConverter<Number> {

    private String patternBase;
    private DecimalFormat df = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.getDefault());

    private IntegerProperty numFractionDigits = new SimpleIntegerProperty(9);
    public IntegerProperty numFractionDigitsProperty() {
        return numFractionDigits;
    }
    public final int getNumFractionDigits() {
        return numFractionDigits.get();
    }
    public final void setNumFractionDigits(int n) {
        numFractionDigits.set(n);
    }

    private BooleanProperty isScientificNotation = new SimpleBooleanProperty(false);
    public BooleanProperty scientificNotationProperty() {
        return isScientificNotation;
    }
    public Boolean isScientificNotation() {
        return isScientificNotation.get();
    }
    public void setScientificNotation(boolean value) {
        isScientificNotation.set(value);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public NumberColumnStringConverter() {
        super();
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
        patternBase = "0" + symbols.getDecimalSeparator() + "0";
        df.applyPattern(patternBase);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public String toString(Number number) {
        if (number == null) {
            return "";
        }

        int valueFractionDigits = Math.max(1, TableUtils.countFractionDigits(number));
        StringBuilder pattern = new StringBuilder(patternBase);
        for (int i = 1; i < Math.min(valueFractionDigits, numFractionDigits.get()); i++) {
            pattern.append("0");
        }
        if (isScientificNotation()) {
            pattern.append("E00");
        }
        for (int i = valueFractionDigits; i < numFractionDigits.get(); i++) {
            pattern.append(" ");
        }
        df.applyLocalizedPattern(pattern.toString());

        // When an instance of Number is passed as an argument to df.format(), it is first cast to a double. If this
        // number is actually an int, then a placeholder 0 will be added in the tenths place, and the number of
        // significant digits used when formatting the number in scientific notation will be off by +1. So, if the
        // number is an Integer, it is cast to a Long, which does not undergo conversion to a double.
        return df.format((number instanceof Integer) ? new Long((int) number) : number).toLowerCase();
    }

    @Override
    public Number fromString(String str) {
        if (str.isEmpty()) {
            return null;
        }
        df.applyPattern(patternBase);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            try {
                return df.parse(str);
            } catch (ParseException e2) {
                e.printStackTrace();
                return Double.NaN;
            }
        }
    }

}
