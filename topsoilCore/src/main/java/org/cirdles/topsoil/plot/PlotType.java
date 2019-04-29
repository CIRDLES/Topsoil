package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.plot.impl.ScatterPlot;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;

/**
 * Pre-defined types of plots available in Topsoil.
 *
 * @author marottajb
 */
public enum PlotType {

    SCATTER("Scatter Plot", ScatterPlot.class, "impl/ScatterPlot.js",
            Variables.X,
            Variables.Y
    );

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final String name;
    private final Class<? extends Plot> plot;
    private final String plotFile;
    private final Variable[] requiredVariables;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    PlotType(String name, Class<? extends Plot> plot, String plotFile, Variable... requiredVariables) {
        this.name = name;
        this.plot = plot;
        this.plotFile = plotFile;
        this.requiredVariables = requiredVariables;
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

    public Variable[] getRequiredVariables() {
        return requiredVariables;
    }}
