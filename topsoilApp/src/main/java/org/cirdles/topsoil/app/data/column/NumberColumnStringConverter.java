package org.cirdles.topsoil.app.data.column;

import javafx.util.StringConverter;

import java.text.DecimalFormat;

public class NumberColumnStringConverter extends StringConverter<Number> {

    private static final String PATTERN_BASE = "0.0";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private DecimalFormat df = new DecimalFormat(PATTERN_BASE);
    private boolean isScientificNotation = false;
    private int numFractionDigits = 9;

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public String toString(Number number) {
        if (number != null) {
            int valueFractionDigits = countFractionDigits(number);
            StringBuilder pattern = new StringBuilder(PATTERN_BASE);
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
            df.applyPattern(pattern.toString());
            return df.format((double) number);
        }
        return "";
    }

    @Override
    public Number fromString(String str) {
        if (! str.isEmpty()) {
            return Double.parseDouble(str);
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
        String str = Double.toString((double) number).toLowerCase();
        if (str.contains("e")) {
            return str.substring(str.indexOf(".") + 1, str.indexOf("e")).length();
        } else {
            return str.substring(str.indexOf(".") + 1).length();
        }
    }

}
