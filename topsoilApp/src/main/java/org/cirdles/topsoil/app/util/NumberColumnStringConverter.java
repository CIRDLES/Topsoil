package org.cirdles.topsoil.app.util;

import javafx.util.StringConverter;

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
    private int numFractionDigits = 9;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public NumberColumnStringConverter() {
        super();
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
        patternBase = "0" + symbols.getDecimalSeparator() + "0";
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public String toString(Number number) {
        if (number != null) {
            int valueFractionDigits = countFractionDigits(number);
            StringBuilder pattern = new StringBuilder(patternBase);
            for (int i = 1; i < numFractionDigits; i++) {
                if (i < valueFractionDigits) {
                    pattern.append("0");
                } else {
                    pattern.append(" ");        // Pad extra fraction places with whitespace
                }
            }
            df.applyLocalizedPattern(pattern.toString());
            return df.format((double) number);
        }
        return "";
    }

    @Override
    public Number fromString(String str) {
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

    public void setNumFractionDigits(int n) {
        this.numFractionDigits = n;
    }

    public static int countFractionDigits(Number number) {
        if (number != null) {
            String str = Double.toString((double) number).toLowerCase();
            int dotIndex = str.indexOf(".");
            return str.substring(dotIndex + 1).length();
        }
        return -1;
    }

}
