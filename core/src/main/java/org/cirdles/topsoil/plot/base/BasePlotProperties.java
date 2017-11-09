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
    public static final String X_MIN = "X Min";
    public static final String X_MAX = "X Max";
    public static final String Y_MIN = "Y Min";
    public static final String Y_MAX = "Y Max";
    public static final String POINTS = "Points";
    public static final String ELLIPSES = "Ellipses";
    public static final String BARS = "Bars";
    public static final String POINT_FILL_COLOR = "Point Fill Color";
    public static final String ELLIPSE_FILL_COLOR = "Ellipse Fill Color";
    public static final String BAR_FILL_COLOR = "Bar Fill Color";
    public static final String POINT_OPACITY = "Point Opacity";
    public static final String ELLIPSE_OPACITY = "Ellipse Opacity";
    public static final String BAR_OPACITY = "Bar Opacity";
    public static final String UNCERTAINTY = "Uncertainty";
    public static final String LAMBDA_U234 = "U234";
    public static final String LAMBDA_U235 = "U235";
    public static final String LAMBDA_U238 = "U238";
    public static final String LAMBDA_Th230 = "Th230";
    public static final String ISOTOPE_TYPE = "Isotope";
    public static final String CONCORDIA_LINE = "Concordia";
    public static final String REGRESSION_LINE = "Regression";
    public static final String REGRESSION_ENVELOPE = "Regression Envelope";
    public static final String EVOLUTION_MATRIX = "Evolution";

    private BasePlotProperties() {
        // prevents this class from being instantiated
    }
}
