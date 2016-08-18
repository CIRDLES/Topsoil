package org.cirdles.topsoil.app.progress.isotope;

import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;

/**
 * Created by sbunce on 6/27/2016.
 */
public enum IsotopeType {

    //Isotope abbreviation, isotope name, array of default headers as strings
    UPb("UPb", "Uranium Lead",
            new String[]{"207Pb*/235U", "±2σ (%)", "206Pb*/238U", "±2σ (%)", "Corr Coef"},
            new TopsoilPlotType[] {TopsoilPlotType.SCATTER_PLOT, TopsoilPlotType.UNCERTAINTY_ELLIPSE_PLOT}),

    //TODO headers array is a placeholder for ACTUAL Uranium Thorium headers
    UTh("UTh", "Uranium Thorium",
            new String[]{"207Pb*/235U", "±2σ (%)", "206Pb*/238U", "±2σ (%)", "Corr Coef"},
            new TopsoilPlotType[] {TopsoilPlotType.SCATTER_PLOT});

    private final String abbreviation;
    private final String name;
    private final String[] headers;
    private final TopsoilPlotType[] plots;

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

    public TopsoilPlotType[] getPlots() {
        return plots.clone();
    }
}
