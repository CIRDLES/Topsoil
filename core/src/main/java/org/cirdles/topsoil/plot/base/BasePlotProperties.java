package org.cirdles.topsoil.plot.base;

/**
 * A set of keys for defining {@code BasePlot} properties.
 *
 * @author Emily Coleman
 */
public class BasePlotProperties {
    public static final String TITLE = "Title";
    public static final String X_AXIS = "X Axis";
    public static final String Y_AXIS = "Y Axis";
    public static final String POINTS = "Points";
    public static final String ELLIPSES = "Ellipses";
    public static final String CROSSES = "Crosses";
    public static final String POINT_FILL_COLOR = "Point Fill Color";
    public static final String ELLIPSE_FILL_COLOR = "Ellipse Fill Color";
    public static final String CROSS_FILL_COLOR = "Cross Fill Color";
    public static final String POINT_OPACITY = "Point Opacity";
    public static final String ELLIPSE_OPACITY = "Ellipse Opacity";
    public static final String CROSS_OPACITY = "Cross Opacity";
    public static final String UNCERTAINTY = "Uncertainty";
    public static final String LAMBDA_235 = "LAMBDA_235";
    public static final String LAMBDA_238 = "LAMBDA_238";
    public static final String ISOTOPE_TYPE = "Isotope";
    public static final String CONCORDIA_LINE = "Concordia";
    public static final String EVOLUTION_MATRIX = "Evolution";

    private BasePlotProperties() {
        // prevents this class from being instantiated
    }
}
