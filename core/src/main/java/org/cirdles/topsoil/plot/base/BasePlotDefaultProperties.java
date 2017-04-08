package org.cirdles.topsoil.plot.base;

import java.util.HashMap;

import static org.cirdles.topsoil.plot.base.BasePlotProperties.*;

/**
 * Created by Emily on 2/24/17.
 */
public class BasePlotDefaultProperties extends HashMap<String, Object> {
    public BasePlotDefaultProperties() {
        configure();
    }

    private void configure() {
        put(TITLE, "Plot");
        put(X_AXIS, "X-axis");
        put(Y_AXIS, "Y-axis");
        put(POINT_FILL_COLOR, "black");
        put(ELLIPSE_FILL_COLOR, "red");
        put(LAMBDA_235, 9.8485e-10);
        put(LAMBDA_238, 1.55125e-10);
        put(UNCERTAINTY, 2);
        put(ELLIPSES, false);
        put(ISOTOPE, "Generic");
    }

}
