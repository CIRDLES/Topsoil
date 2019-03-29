package org.cirdles.topsoil.plot;

import java.util.HashMap;

import static org.cirdles.topsoil.plot.PlotProperty.*;

public class DefaultProperties extends HashMap<PlotProperty, Object> {

    public DefaultProperties() {
	    configure();
    }

    private void configure() {
	    put(TITLE, "New Plot");

	    put(X_AXIS, "X-axis");
	    put(X_MIN, "");
	    put(X_MAX, "");

	    put(Y_AXIS, "Y-axis");
	    put(Y_MIN, "");
	    put(Y_MAX, "");

	    put(POINTS, true);
	    put(POINTS_FILL, "steelblue");
	    put(POINTS_OPACITY, 1.0);

	    put(ELLIPSES, true);
	    put(ELLIPSES_FILL, "red");
	    put(ELLIPSES_OPACITY, 1.0);

	    put(UNCTBARS, false);
	    put(UNCTBARS_FILL, "black");
	    put(UNCTBARS_OPACITY, 1.0);

	    put(WETHERILL_LINE, false);
		put(WETHERILL_ENVELOPE, false);
	    put(WETHERILL_LINE_FILL, "blue");
	    put(WETHERILL_ENVELOPE_FILL, "lightgray");
		put(WASSERBURG_LINE, false);
		put(WASSERBURG_ENVELOPE, false);
		put(WASSERBURG_LINE_FILL, "blue");
		put(WASSERBURG_ENVELOPE_FILL, "lightgray");
	    put(MCLEAN_REGRESSION, false);
	    put(MCLEAN_REGRESSION_ENVELOPE, false);
	    put(EVOLUTION, false);

	    put(UNCERTAINTY, 2.0);
	    put(ISOTOPE_SYSTEM, "Generic");
	    put(LAMBDA_U234, 2.82206e-6);
	    put(LAMBDA_U235, 9.8485e-10);
	    put(LAMBDA_U238, 1.55125e-10);
	    put(LAMBDA_TH230, 9.1705e-6);
	    put(R238_235S, 137.88);
    }
}
