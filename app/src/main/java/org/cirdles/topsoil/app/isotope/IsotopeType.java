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
            new TopsoilPlotType[] {TopsoilPlotType.SCATTER_PLOT, TopsoilPlotType.UNCERTAINTY_ELLIPSE_PLOT}),

    UTh("UTh", "Uranium Thorium",
            new String[]{"[234Pb/238U]t", "[230Th/238U]t", "±2σ (%)", "±2σ (%)", "Corr Coef"},
            new TopsoilPlotType[] {TopsoilPlotType.SCATTER_PLOT, TopsoilPlotType.EVOLUTION_PLOT});

    private final String abbreviation;
    private final String name;
    private final String[] headers;
    private final TopsoilPlotType[] plots;

    public static final List<IsotopeType> ISOTOPE_TYPES;
    static {
        ISOTOPE_TYPES = Collections.unmodifiableList(Arrays.asList(
                UPb,
                UTh,
                Generic
        ));
    }

    IsotopeType(String abbr, String name, String [] headers, TopsoilPlotType [] plots) {
        this.abbreviation = abbr;
        this.name = name;
        this.headers = headers;
        this.plots = plots;
    }

    /**
     *
     * @return abbreviated name
     */
    public String getAbbreviation() {
        return this.abbreviation;
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
        String[] result = this.headers.clone();
        return result;
    }

    /**
     *
     * @return array of PlotTypes
     */
    public TopsoilPlotType[] getPlots() {
        return plots.clone();
    }
}
