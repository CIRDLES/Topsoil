package org.cirdles.topsoil.app.data.column;

import javafx.util.StringConverter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;

public class NumberColumnStringConverter extends StringConverter<Number> implements Serializable {

    private static final long serialVersionUID = -1906996296754218388L;
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

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(numFractionDigits);
        out.writeBoolean(isScientificNotation);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        numFractionDigits = in.readInt();
        isScientificNotation = in.readBoolean();
    }

}
