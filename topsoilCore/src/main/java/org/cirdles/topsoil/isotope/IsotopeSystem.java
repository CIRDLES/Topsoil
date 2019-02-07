package org.cirdles.topsoil.isotope;

import org.cirdles.topsoil.plot.PlotType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * Created by sbunce on 6/27/2016.
 */
public enum IsotopeSystem {

    //Isotope abbreviation, isotope name, array of default headers as strings
    GENERIC("Gen", "Generic",
            new String[]{"x", "y", "xσ", "yσ", "Corr Coef"},
            new PlotType[] {PlotType.SCATTER}),

    UPB("UPb", "Uranium Lead",
        new String[]{"207Pb*/235U", "206Pb*/238U", "±2σ (%)", "±2σ (%)", "Corr Coef"},
        new PlotType[] {PlotType.SCATTER}),

    UTH("UTh", "Uranium Thorium",
        new String[]{"[234Pb/238U]t", "[230Th/238U]t", "±2σ (%)", "±2σ (%)", "Corr Coef"},
        new PlotType[] {PlotType.SCATTER});

    /**
     * An abbreviation of the {@code IsotopeSystem}'s name.
     */
    private final String abbreviation;

    /**
     * The name of the {@code IsotopeSystem}.
     */
    private final String name;

    /**
     * Default headers for model of an {@code IsotopeSystem}.
     */
    private final String[] headers;

    /**
     * The available {@code PlotType}s for the {@code IsotopeSystem}.
     */
    private final PlotType[] plots;

    /**
     * A {@code List} of all {@code IsotopeSystem}s.
     */
    public static final List<IsotopeSystem> ISOTOPE_SYSTEMS;
    static {
	    ISOTOPE_SYSTEMS = Collections.unmodifiableList(Arrays.asList(
                GENERIC,
                UPB,
                UTH
	                                                                ));
    }

    //***********************
    // Constructors
    //***********************

    IsotopeSystem( String abbr, String name, String [] headers, PlotType[] plots ) {
        this.abbreviation = abbr;
        this.name = name;
        this.headers = headers;
        this.plots = plots;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the appropriate {@code IsotopeSystem} for the given name, if one exists.
     *
     * @param name  String name
     * @return      associated IsotopeSystem
     */
    public static IsotopeSystem fromName( String name ) {
        for (IsotopeSystem i : values()) {
            if (name.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }

    /**
     * Returns the appropriate {@code IsotopeSystem} for the given abbreviation, if one exists.
     *
     * @param abbr  String abbreviation
     * @return      associated IsotopeSystem
     */
    public static IsotopeSystem fromAbbreviation( String abbr ) {
        for (IsotopeSystem i : values()) {
            if (abbr.equals(i.getAbbreviation())) {
                return i;
            }
        }
        return null;
    }

    /**
     * Returns the abbreviated name of the {@code IsotopeSystem}.
     *
     * @return abbreviated String name
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Returns the name of the {@code IsotopeSystem}.
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the default headers for model of {@code IsotopeSystem}.
     *
     * @return array of String headers
     */
    public String[] getHeaders() {
        return headers.clone();
    }

    /**
     * Returns the available {@code PlotType}s for the {@code IsotopeSystem}.
     *
     * @return array of TopsoilPlotTypes
     */
    public PlotType[] getPlots() {
        return plots.clone();
    }

    @Override
    public String toString() {
        return abbreviation;
    }
}
