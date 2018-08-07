package org.cirdles.topsoil.app.isotope;

import org.cirdles.topsoil.app.plot.TopsoilPlotType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * Created by sbunce on 6/27/2016.
 */
public enum IsotopeType {

    //Isotope abbreviation, isotope name, array of default headers as strings
    Generic("Gen", "Generic",
            new String[]{"x", "y", "xσ", "yσ", "Corr Coef"},
            new TopsoilPlotType[] {TopsoilPlotType.BASE_PLOT}),

    UPb("UPb", "Uranium Lead",
            new String[]{"207Pb*/235U", "206Pb*/238U", "±2σ (%)", "±2σ (%)", "Corr Coef"},
            new TopsoilPlotType[] {TopsoilPlotType.BASE_PLOT}),

    UTh("UTh", "Uranium Thorium",
            new String[]{"[234Pb/238U]t", "[230Th/238U]t", "±2σ (%)", "±2σ (%)", "Corr Coef"},
            new TopsoilPlotType[] {TopsoilPlotType.BASE_PLOT});

    /**
     * An abbreviation of the {@code IsotopeType}'s name.
     */
    private final String abbreviation;

    /**
     * The name of the {@code IsotopeType}.
     */
    private final String name;

    /**
     * Default headers for data of an {@code IsotopeType}.
     */
    private final String[] headers;

    /**
     * The available {@code TopsoilPlotType}s for the {@code IsotopeType}.
     */
    private final TopsoilPlotType[] plots;

    //***********************
    // Constructors
    //***********************

    IsotopeType(String abbr, String name, String [] headers, TopsoilPlotType [] plots) {
        this.abbreviation = abbr;
        this.name = name;
        this.headers = headers;
        this.plots = plots;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the appropriate {@code IsotopeType} for the given name, if one exists.
     *
     * @param name  String name
     * @return      associated IsotopeType
     */
    public static IsotopeType fromName(String name) {
        for (IsotopeType i : values()) {
            if (name.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }

    /**
     * Returns the appropriate {@code IsotopeType} for the given abbreviation, if one exists.
     *
     * @param abbr  String abbreviation
     * @return      associated IsotopeType
     */
    public static IsotopeType fromAbbreviation(String abbr) {
        for (IsotopeType i : values()) {
            if (abbr.equals(i.getAbbreviation())) {
                return i;
            }
        }
        return null;
    }

    /**
     * Returns the abbreviated name of the {@code IsotopeType}.
     *
     * @return abbreviated String name
     */
    public String getAbbreviation() {
        return this.abbreviation;
    }

    /**
     * Returns the name of the {@code IsotopeType}.
     *
     * @return String name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the default headers for data of {@code IsotopeType}.
     *
     * @return array of String headers
     */
    public String[] getHeaders() {
        String[] result = this.headers.clone();
        return result;
    }

    /**
     * Returns the available {@code TopsoilPlotType}s for the {@code IsotopeType}.
     *
     * @return array of TopsoilPlotTypes
     */
    public TopsoilPlotType[] getPlots() {
        return plots.clone();
    }
}
