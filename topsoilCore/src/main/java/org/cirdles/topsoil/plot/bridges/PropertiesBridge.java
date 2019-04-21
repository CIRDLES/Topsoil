package org.cirdles.topsoil.plot.bridges;

import org.cirdles.topsoil.plot.PlotProperties;

import java.util.HashMap;

//Passes values from Javascript back into Java
public class PropertiesBridge {

    private HashMap<PlotProperties.Property<?>, Object> axisProperties = new HashMap<>();
    private boolean updated = true;

    //true if Javascript has updated properties that Java doesn't know about
    //e.g. the plot has been zoomed and the Java axis extent properties need to be updated
    public void setIfUpdated(boolean update) {
        updated = update;
    }

    public boolean getIfUpdated() {
        return updated;
    }

    public HashMap<PlotProperties.Property<?>, Object> getProperties() {
        return axisProperties;
    }

    //Takes axis extents from Javascript and passes them to Java
    public void setAxesExtents(String X_MIN, String X_MAX, String Y_MIN, String Y_MAX) {
        axisProperties.put(PlotProperties.X_MIN, X_MIN);
        axisProperties.put(PlotProperties.X_MAX, X_MAX);
        axisProperties.put(PlotProperties.Y_MIN, Y_MIN);
        axisProperties.put(PlotProperties.Y_MAX, Y_MAX);
    }
}
