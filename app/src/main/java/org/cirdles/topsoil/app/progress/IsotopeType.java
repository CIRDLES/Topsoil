package org.cirdles.topsoil.app.progress;

import java.util.Arrays;

/**
 * Created by sbunce on 6/27/2016.
 */
public enum IsotopeType {

    //Isotope abbreviation, isotope name, array of default headers as strings
    UPb("UPb", "Uranium Lead",
            new String[]{"207Pb*/235U", "±2σ (%)", "206Pb*/238U", "±2σ (%)", "Corr Coef"}),

    //TODO headers array is a placeholder for ACTUAL Uranium Thorium headers
    UTh("UTh", "Uranium Thorium",
            new String[]{"207Pb*/235U", "±2σ (%)", "206Pb*/238U", "±2σ (%)", "Corr Coef"});

    private final String abbr;
    private final String name;
    private final String[] headers;

    IsotopeType(String abbr, String name, String [] headers) {
        this.abbr = abbr;
        this.name = name;
        this.headers = headers;
    }

    /**
     *
     * @return abbreviated name
     */
    public String getAbbreviation() {
        return this.abbr;
    }

    /**
     *
     * @return full name
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return default headers
     */
    public String[] getHeaders() {
        String[] result = Arrays.copyOf(this.headers, headers.length);
        return result;
    }
}
