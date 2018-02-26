package org.cirdles.topsoil.plot.bridges;

import java.util.HashMap;

//Passes values from Javascript back into Java
public class PropertiesBridge {

    private HashMap<String, Object> axisProperties = new HashMap<>();
    private boolean updated = true;

    //true if Javascript has updated properties that Java doesn't know about
    //e.g. the plot has been zoomed and the Java axis extent properties need to be updated
    public void setIfUpdated(boolean update) {
        updated = update;
    }

    public boolean getIfUpdated() {
        return updated;
    }

    public HashMap<String, Object> getProperties() {
        return axisProperties;
    }

    //Takes axis extents from Javascript and passes them to Java
    public void setAxesExtents(String X_MIN, String X_MAX, String Y_MIN, String Y_MAX) {
        axisProperties.put("X Min", X_MIN);
        axisProperties.put("X Max", X_MAX);
        axisProperties.put("Y Min", Y_MIN);
        axisProperties.put("Y Max", Y_MAX);
    }
}
