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
        put(X_MIN, "");
        put(X_MAX, "");
        put(Y_MIN, "");
        put(Y_MAX, "");
        put(POINT_FILL_COLOR, "steelblue");
        put(ELLIPSE_FILL_COLOR, "red");
        put(BAR_FILL_COLOR, "black");
        put(POINT_OPACITY, 1.0);
        put(ELLIPSE_OPACITY, 1.0);
        put(BAR_OPACITY, 1.0);
        put(LAMBDA_U234, 2.82206e-6);
        put(LAMBDA_U235, 9.8485e-10);
        put(LAMBDA_U238, 1.55125e-10);
        put(LAMBDA_Th230, 9.1705e-6);
        put(UNCERTAINTY, 2.0);
        put(POINTS, true);
        put(ELLIPSES, false);
        put(BARS, false);
        put(ISOTOPE_TYPE, "Generic");
        put(CONCORDIA_LINE, false);
        put(REGRESSION_LINE, false);
        put(EVOLUTION_MATRIX, false);
    }

}
