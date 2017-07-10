package org.cirdles.topsoil.plot.base;

import java.util.HashMap;

import static org.cirdles.topsoil.plot.base.BasePlotProperties.*;

/**
 * A {@code HashMap} containing a set of default properties for a {@code BasePlot}.
 *
 * @author Emily Coleman
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
        put(BAR_FILL_COLOR, "black");
        put(POINT_OPACITY, 1.0);
        put(ELLIPSE_OPACITY, 1.0);
        put(BAR_OPACITY, 1.0);
        put(LAMBDA_235, 9.8485e-10);
        put(LAMBDA_238, 1.55125e-10);
        put(UNCERTAINTY, 2.0);
        put(POINTS, true);
        put(ELLIPSES, false);
        put(BARS, false);
        put(ISOTOPE_TYPE, "Generic");
        put(CONCORDIA_LINE, false);
        put(EVOLUTION_MATRIX, false);
    }

}
