package org.cirdles.topsoil.plot;

/**
 * PlotProperties are used as keys when it is necessary to store plot properties as key-value pairs.
 */
public enum PlotProperty {

	TITLE("Title"),

	X_AXIS("X Axis"),
	X_MIN("X Min"),
	X_MAX("X Max"),

	Y_AXIS("Y Axis"),
	Y_MIN("Y Min"),
	Y_MAX("Y Max"),

	POINTS("Points"),
	POINTS_FILL("Points Fill"),
	POINTS_OPACITY("Points Opacity"),

	ELLIPSES("Ellipses"),
	ELLIPSES_FILL("Ellipses Fill"),
	ELLIPSES_OPACITY("Ellipses Opacity"),

	UNCTBARS("Unct Bars"),
	UNCTBARS_FILL("Unct Bars Fill"),
	UNCTBARS_OPACITY("Unct Bars Opacity"),

	WETHERILL_LINE("Wetherill Line"),
	WETHERILL_ENVELOPE("Wetherill Envelope"),
	WETHERILL_LINE_FILL("Wetherill Line Fill"),
	WETHERILL_ENVELOPE_FILL("Wetherill Envelope Fill"),
	WASSERBURG_LINE("Wasserburg Line"),
	WASSERBURG_ENVELOPE("Wasserburg Envelope"),
	WASSERBURG_LINE_FILL("Wasserburg Line Fill"),
	WASSERBURG_ENVELOPE_FILL("Wasserburg Envelope Fill"),
	EVOLUTION("Evolution Matrix"),
	MCLEAN_REGRESSION("McLean Regression"),
	MCLEAN_REGRESSION_ENVELOPE("McLean Regression Envelope"),

	UNCERTAINTY("Uncertainty"),
	ISOTOPE_SYSTEM("Isotope System"),
	LAMBDA_U234("U234"),
	LAMBDA_U235("U235"),
	LAMBDA_U238("U238"),
	LAMBDA_TH230("Th230"),
	R238_235S("R238_235S");

	private String key;

	PlotProperty(String s) {
		this.key = s;
	}

	@Override
	public String toString() {
		return key;
	}

}