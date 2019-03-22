package org.cirdles.topsoil.app.util;

import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class NumberColumnStringConverter extends StringConverter<Number> {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private String patternBase;
    private DecimalFormat df = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.getDefault());
    private boolean isScientificNotation = false;
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
                    pattern.append(" ");
                }
            }
            if (isScientificNotation) {
                pattern.append("e0##");
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

    public void setScientificNotation(boolean value) {
        isScientificNotation = value;
    }

    public void setNumFractionDigits(int n) {
        this.numFractionDigits = n;
    }

    public static int countFractionDigits(Number number) {
        if (number != null) {
            String str = Double.toString((double) number).toLowerCase();
            if (str.contains("e")) {
                return str.substring(str.indexOf(".") + 1, str.indexOf("e")).length();
            } else {
                return str.substring(str.indexOf(".") + 1).length();
            }
        }
        return -1;
    }

}
