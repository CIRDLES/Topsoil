package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.plot.impl.ScatterPlot;

/**
 * Pre-defined types of plots available in Topsoil.
 *
 * @author marottajb
 */
public enum PlotType {

    SCATTER("Scatter Plot", ScatterPlot.class, "impl/ScatterPlot.js",
            "impl/data/Points.js",
            "impl/data/Ellipses.js",
            "impl/data/UncertaintyBars.js",
            "impl/feature/Concordia.js",
            "impl/feature/TWConcordia.js",
            "impl/feature/Regression.js",
            "impl/feature/Evolution.js",
            "impl/DefaultLambda.js",
            "impl/Utils.js");

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final String name;
    private final Class<? extends Plot> plot;
    private final String plotFile;
    private final String[] resources;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    PlotType(String name, Class<? extends Plot> plot, String plotFile, String... resources) {
        this.name = name;
        this.plot = plot;
        this.plotFile = plotFile;
        this.resources = resources;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns the readable name of this plot type.
     *
     * @return  String name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a new {@code Plot} instance of this type.
     *
     * @return  new Plot
     */
    public Plot getPlot() {
        try {
            return plot.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the name of the associated JS file for this plot type.
     *
     * @return  String file name
     */
    public String getPlotFile() {
        return plotFile;
    }

    /**
     * An array of file names of resource files used by this plot type's associated JS file.
     *
     * @return  String[] of file names
     */
    public String[] getResources() {
        return resources;
    }
}
