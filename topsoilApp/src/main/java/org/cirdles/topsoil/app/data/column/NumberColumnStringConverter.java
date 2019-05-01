package org.cirdles.topsoil.app.data.column;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.StringConverter;
import org.cirdles.topsoil.app.data.DataUtils;

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

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

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
            int valueFractionDigits = DataUtils.countFractionDigits(number);
            StringBuilder pattern = new StringBuilder(patternBase);
            for (int i = 1; i < numFractionDigits.get(); i++) {
                if (i < valueFractionDigits) {
                    pattern.append("0");
                } else {
                    pattern.append(" ");        // Pad extra fraction places with whitespace
                }
            }
            df.applyLocalizedPattern(pattern.toString());
            return df.format(number);
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
