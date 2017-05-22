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
        put(TITLE, "New Plot");
        put(X_AXIS, "X-axis");
        put(Y_AXIS, "Y-axis");
        put(POINT_FILL_COLOR, "steelblue");
        put(ELLIPSE_FILL_COLOR, "red");
//        put(CROSS_FILL_COLOR, "black")
        put(LAMBDA_235, 9.8485e-10);
        put(LAMBDA_238, 1.55125e-10);
        put(UNCERTAINTY, 2.0);
        put(POINTS, true);
        put(ELLIPSES, false);
//        put(CROSSES, false);
        put(ISOTOPE_TYPE, "Generic");
        put(CONCORDIA_LINE, false);
    }

}
