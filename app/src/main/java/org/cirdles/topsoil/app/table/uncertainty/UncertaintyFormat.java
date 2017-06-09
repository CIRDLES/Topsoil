package org.cirdles.topsoil.app.table.uncertainty;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Provides a format for plot uncertainty, including a name and a {@code Double} value.
 *
 * @author Jake Marotta
 */
public enum UncertaintyFormat {

    ONE_SIGMA_ABSOLUTE("1σ (abs)", 1.0),
    TWO_SIGMA_ABSOLUTE("2σ (abs)", 2.0),
    ONE_SIGMA_PERCENT("1σ (%)", 100.0),
    TWO_SIGMA_PERCENT("2σ (%)", 200.0),
    NINETY_FIVE_PERCENT_CONFIDENCE("95% Confidence", 2.4477);

    /**
     * The name of the uncertainty format.
     */
    private String name;

    /**
     * The {@code Double} value of the uncertainty format.
     */
    private Double value;

    public static final List<UncertaintyFormat> ALL = Collections.unmodifiableList(asList(
            ONE_SIGMA_ABSOLUTE,
            TWO_SIGMA_ABSOLUTE,
            ONE_SIGMA_PERCENT,
            TWO_SIGMA_PERCENT,
            NINETY_FIVE_PERCENT_CONFIDENCE
    ));

    public static final List<UncertaintyFormat> PLOT_FORMATS = Collections.unmodifiableList(asList(
            ONE_SIGMA_ABSOLUTE,
            TWO_SIGMA_ABSOLUTE,
            NINETY_FIVE_PERCENT_CONFIDENCE
    ));

    public static final List<UncertaintyFormat> PERCENT_FORMATS = Collections.unmodifiableList(asList(
            ONE_SIGMA_PERCENT,
            TWO_SIGMA_PERCENT
    ));

    public static final List<UncertaintyFormat> ABSOLUTE_FORMATS = Collections.unmodifiableList(asList(
            ONE_SIGMA_ABSOLUTE,
            TWO_SIGMA_ABSOLUTE
    ));

    /**
     * Constructs a new {@code UncertaintyFormat} with the specified name and value.
     *
     * @param name  String name
     * @param value Double value
     */
    UncertaintyFormat(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the name of the {@code UncertaintyFormat}.
     *
     * @return  String name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the {@code UncertaintyFormat}.
     *
     * @return  Double value
     */
    public Double getValue() {
        return value;
    }
}
