package org.cirdles.topsoil.app.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.StringConverter;
import org.cirdles.topsoil.data.TableUtils;

import java.math.BigDecimal;
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
    private char decSeparator;
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
        decSeparator = symbols.getDecimalSeparator();
        patternBase = "0";
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

        // Construct the correct format pattern
        StringBuilder pattern = new StringBuilder(patternBase);
        String str = number.toString();

        int decimalIndex = str.indexOf(decSeparator);
        int numberFractionDigits;       // # of fraction digits in the Number value
        if (isScientificNotation()) {
            numberFractionDigits = TableUtils.countSignificantDigits(number) - 1;
        } else if (decimalIndex > -1) {
            // number is a decimal value
            numberFractionDigits = str.length() - (decimalIndex + 1);
        } else {
            // number is an integer value
            numberFractionDigits = 0;
        }
        int maxFractionDigits = numFractionDigits.get();    // max # of fraction digits for the converter
        int formatFractionDigits = Math.min(numberFractionDigits, maxFractionDigits);     // # of fraction digits to put in the format

        // add fraction digits, if present
        if (formatFractionDigits > 0) {
            pattern.append(".");
            for (int i = 0; i < formatFractionDigits; i++) {
                pattern.append("0");
            }
        }
        // add sci notation, if necessary
        if (isScientificNotation()) {
            pattern.append("E0");
            if (number.doubleValue() >= 1) {
                // Add an extra space to account for the lack of a negative sign for the exponent
                pattern.append(" ");
            }
        }
        // pad remaining places with space
        for (int i = formatFractionDigits; i < maxFractionDigits; i++) {
            pattern.append(" ");
        }
        // add an extra space, if there is no decimal separator
        if (numberFractionDigits == 0 && maxFractionDigits > 0) {
            pattern.append(" ");
        }
        df.applyLocalizedPattern(pattern.toString());

        return df.format(number).toLowerCase();
    }

    @Override
    public Number fromString(String str) {
        if (str.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }

    }

}
