package org.cirdles.topsoil.app.plot;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * A set of {@code Double} uncertainty formats, for plots.
 *
 * @author Jake Marotta
 */
public class UncertaintyFormats {

    /**
     * Uncertainty values are used as-is.
     */
    public static final UncertaintyFormat ONE_SIGMA_ABSOLUTE = new UncertaintyFormat("1σ (abs)", 1.0);

    /**
     * Uncertainty values are doubled.
     */
    public static final UncertaintyFormat TWO_SIGMA_ABSOLUTE = new UncertaintyFormat("2σ (abs", 2.0);

    /**
     * Uncertainty values are multiplied by 2.4477.
     */
    public static final UncertaintyFormat NINETY_FIVE_PERCENT_CONFIDENCE = new UncertaintyFormat("95% Confidence",
                                                                                                 2.4477);
    /**
     * A {@code List} of all defined {@code UncertaintyFormat}s.
     */
    public static final List<UncertaintyFormat> UNCERTAINTY_FORMATS = asList(
            ONE_SIGMA_ABSOLUTE,
            TWO_SIGMA_ABSOLUTE,
            NINETY_FIVE_PERCENT_CONFIDENCE
    );

    private UncertaintyFormats() {
        // Prevents this class from being instantiated.
    }
}
