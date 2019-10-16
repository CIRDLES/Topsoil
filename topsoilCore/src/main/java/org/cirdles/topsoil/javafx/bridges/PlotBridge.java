package org.cirdles.topsoil.javafx.bridges;

import javafx.collections.ObservableMap;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.PlotOption;

/**
 * Intended to be added as a member to a WebViewPlot, so that the JavaScript may make upcalls to JavaFX.
 *
 * @author Emily Coleman
 */
public class PlotBridge {

    private PlotView plot;
//    public Regression regression;

    public PlotBridge(PlotView plot) {
        this.plot = plot;
//        this.regression = new Regression();
    }

    //Takes axis extents from Javascript and passes them to Java
    public void syncAxes(Double xMin, Double xMax, Double yMin, Double yMax) {
        ObservableMap<PlotOption<?>, Object> options = plot.getOptions();
        if (xMin != null) options.put(PlotOption.X_MIN, xMin);
        if (xMax != null) options.put(PlotOption.X_MAX, xMax);
        if (yMin != null) options.put(PlotOption.Y_MIN, yMin);
        if (yMax != null) options.put(PlotOption.Y_MAX, yMax);
    }

}
