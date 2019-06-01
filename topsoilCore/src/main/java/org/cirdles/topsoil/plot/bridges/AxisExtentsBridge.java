package org.cirdles.topsoil.plot.bridges;

import org.cirdles.topsoil.plot.PlotOption;

import java.util.HashMap;

//Passes values from Javascript back into Java
public class AxisExtentsBridge {

    private HashMap<PlotOption<?>, Object> axisProperties = new HashMap<>();
    private boolean updated = true;

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
    public void update(Double xMin, Double xMax, Double yMin, Double yMax) {
        if (xMin != null) axisProperties.put(PlotOption.X_MIN, xMin);
        if (xMax != null) axisProperties.put(PlotOption.X_MAX, xMax);
        if (yMin != null) axisProperties.put(PlotOption.Y_MIN, yMin);
        if (yMax != null) axisProperties.put(PlotOption.Y_MAX, yMax);
    }
}
