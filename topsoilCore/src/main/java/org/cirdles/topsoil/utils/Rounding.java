package org.cirdles.topsoil.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Rounding {

    /**
     * Performs excel-style rounding of double to a given number of significant
     * figures.
     *
     * @param value double to round
     * @param sigFigs count of significant digits for rounding
     * @return double rounded to sigFigs significant digits
     */
    public static double roundedToSize(double value, int sigFigs) {
        BigDecimal valueBDtoSize = BigDecimal.ZERO;
        if (Double.isFinite(value)) {
            BigDecimal valueBD = new BigDecimal(value);
            int newScale = sigFigs - (valueBD.precision() - valueBD.scale());
            valueBDtoSize = valueBD.setScale(newScale, RoundingMode.HALF_UP);
        }
        return valueBDtoSize.doubleValue();
    }

}
