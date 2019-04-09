package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.plot.impl.ScatterPlot;

/**
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

    public String getName() {
        return name;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Plot getPlot() {
        try {
            return plot.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPlotFile() {
        return plotFile;
    }

    public String[] getResources() {
        return resources;
    }
}
