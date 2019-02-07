package org.cirdles.topsoil.uncertainty;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Provides a format for plot uncertainty, including a name and a {@code Double} multiplier.
 *
 * @author marottajb
 */
public enum Uncertainty {

    ONE_SIGMA_ABSOLUTE("1σ (abs)", 1.0),
    TWO_SIGMA_ABSOLUTE("2σ (abs)", 2.0),
    ONE_SIGMA_PERCENT("1σ (%)", 1.0),
    TWO_SIGMA_PERCENT("2σ (%)", 2.0),
    NINETY_FIVE_PERCENT_CONFIDENCE("95% Conf.", 2.4477);

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    public static final List<Uncertainty> PERCENT_FORMATS = Collections.unmodifiableList(asList(
            ONE_SIGMA_PERCENT,
            TWO_SIGMA_PERCENT
    ));

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private String name;
    private Double multiplier;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    Uncertainty(String name, Double value) {
        this.name = name;
        this.multiplier = value;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static Uncertainty fromValue(double val) {
        for (Uncertainty format : values()) {
            if (Double.compare(val, format.getMultiplier()) == 0) {
                return format;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return name;
    }

    //**********************************************//
    //                 CLASS METHODS                //
    //**********************************************//

    public static Uncertainty getFromValue(Double value) {
        for (Uncertainty format : values()) {
            if (Double.compare(format.getMultiplier(), value) == 0) {
                return format;
            }
        }
        return null;
    }
}
