package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.plot.impl.ScatterPlot;

/**
 * @author marottajb
 */
public enum PlotType {

    SCATTER("Scatter Plot", ScatterPlot.class);

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final String name;
    private final Class<? extends Plot> plot;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    PlotType(String name, Class<? extends Plot> plot) {
        this.name = name;
        this.plot = plot;
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
}
