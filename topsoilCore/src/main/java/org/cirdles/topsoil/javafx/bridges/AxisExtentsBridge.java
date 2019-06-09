package org.cirdles.topsoil.javafx.bridges;

import javafx.collections.ObservableMap;
import org.apache.commons.lang3.Validate;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.PlotOption;

import java.util.HashMap;
import java.util.Map;

//Passes values from Javascript back into Java
public class AxisExtentsBridge {

    private HashMap<PlotOption<?>, Object> axisProperties = new HashMap<>();
    private boolean updated = true;

    private PlotView plot;

    public AxisExtentsBridge(PlotView plot) {
        Validate.notNull(plot, "Plot cannot be null.");
        this.plot = plot;
    }

    //true if Javascript has updated properties that Java doesn't know about
    //e.g. the plot has been zoomed and the Java axis extent properties need to be updated
    public void setIfUpdated(boolean update) {
        updated = update;
    }

    public boolean getIfUpdated() {
        return updated;
    }

    public HashMap<PlotOption<?>, Object> getProperties() {
        return axisProperties;
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
