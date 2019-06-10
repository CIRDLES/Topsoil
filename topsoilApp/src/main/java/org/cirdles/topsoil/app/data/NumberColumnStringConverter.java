package org.cirdles.topsoil.app.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.StringConverter;
import org.cirdles.topsoil.data.TableUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

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
        if (number != null) {
            int valueFractionDigits = TableUtils.countFractionDigits(number);
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
            System.out.println("\"" + pattern.toString() + "\": " + df.format(number).toLowerCase());
            return df.format(number).toLowerCase();
        }
        return "";
    }

    @Override
    public Number fromString(String str) {
        df.applyPattern(patternBase);
        if (! str.isEmpty()) {
            try {
                return df.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
                return Double.NaN;
            }
        }
        return null;
    }

}
