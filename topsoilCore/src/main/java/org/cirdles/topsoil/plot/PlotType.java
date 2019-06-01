package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.Variable;

/**
 * Pre-defined types of plots available in Topsoil.
 *
 * @author marottajb
 */
public enum PlotType {

    SCATTER(
            "Scatter Plot",
            "impl/ScatterPlot.js",
            new Variable[]{
                    Variable.X,
                    Variable.Y
            },
            new String[]{
                    "impl/data/Points.js",
                    "impl/data/Ellipses.js",
                    "impl/data/UncertaintyBars.js",
                    "impl/feature/Concordia.js",
                    "impl/feature/TWConcordia.js",
                    "impl/feature/Regression.js",
                    "impl/feature/Evolution.js",
                    "impl/DefaultLambda.js",
                    "impl/Utils.js"
            }
    );

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final String name;
    private final String plotFile;
    private final Variable[] requiredVariables;
    private final String[] resourceFiles;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    PlotType(String name, String plotFile, Variable[] requiredVariables, String[] resourceFiles) {
        this.name = name;
        this.plotFile = plotFile;
        this.requiredVariables = requiredVariables;
        this.resourceFiles = resourceFiles;
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
     * Returns the name of the associated JS file for this plot type.
     *
     * @return  String file name
     */
    public String getPlotFile() {
        return plotFile;
    }

    public Variable[] getRequiredVariables() {
        return requiredVariables;
    }

    public String[] getResourceFiles() {
        return resourceFiles;
    }

}
