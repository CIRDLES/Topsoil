package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.plot.feature.Concordia;
import org.cirdles.topsoil.symbols.SimpleSymbolKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Defines a plot property of type {@code T}.
 *
 * @param <T>   the type of the value that should be associated with this property
 */
public final class PlotOption<T> extends SimpleSymbolKey<T> implements Serializable {

    public static PlotOption<?> forKey(String key) {
        for (PlotOption<?> option : ALL) {
            if (option.toJSONString().equals(key)) {
                return option;
            }
        }
        return null;
    }

    public static final PlotOption<String> TITLE = stringValue("title", "New Plot");

    public static final PlotOption<String> X_AXIS = stringValue("x_axis", "X Axis");
    public static final PlotOption<Number> X_MIN = numberValue("x_min", 0.0);
    public static final PlotOption<Number> X_MAX = numberValue("x_max", 1.0);

    public static final PlotOption<String> Y_AXIS = stringValue("y_axis", "Y Axis");
    public static final PlotOption<Number> Y_MIN = numberValue("y_min", 0.0);
    public static final PlotOption<Number> Y_MAX = numberValue("y_max", 1.0);

    public static final PlotOption<IsotopeSystem> ISOTOPE_SYSTEM =
            new PlotOption<>("isotope_system", IsotopeSystem.GENERIC, IsotopeSystem.class, IsotopeSystem::getName);
    public static final PlotOption<Uncertainty> UNCERTAINTY =
            new PlotOption<>("uncertainty", Uncertainty.TWO_SIGMA_ABSOLUTE, Uncertainty.class, Uncertainty::getMultiplier);
    public static final PlotOption<Number> LAMBDA_U234 = numberValue("lambda_234", Lambda.U234.getDefaultValue());
    public static final PlotOption<Number> LAMBDA_U235 = numberValue("lambda_235", Lambda.U235.getDefaultValue());
    public static final PlotOption<Number> LAMBDA_U238 = numberValue("lambda_238", Lambda.U238.getDefaultValue());
    public static final PlotOption<Number> LAMBDA_TH230 = numberValue("lambda_230", Lambda.Th230.getDefaultValue());
    public static final PlotOption<Number> R238_235S = numberValue("R238_235S", 137.88);

    public static final PlotOption<Boolean> SHOW_UNINCLUDED = booleanValue("show_unincluded", true);
    public static final PlotOption<Boolean> RESET_VIEW_ON_CHANGE_UNC = booleanValue("reset_view_on_change_unc", true);

    public static final PlotOption<Boolean> POINTS = booleanValue("points", true);
    public static final PlotOption<String> POINTS_FILL = stringValue("points_fill", "steelblue");
    public static final PlotOption<Number> POINTS_OPACITY = numberValue("points_opacity", 1.0);

    public static final PlotOption<Boolean> ELLIPSES = booleanValue("ellipses", true);
    public static final PlotOption<String> ELLIPSES_FILL = stringValue("ellipses_fill", "red");
    public static final PlotOption<Number> ELLIPSES_OPACITY = numberValue("ellipses_opacity", 1.0);

    public static final PlotOption<Boolean> UNCTBARS = booleanValue("error_bars", false);
    public static final PlotOption<String> UNCTBARS_FILL = stringValue("error_bars_fill", "black");
    public static final PlotOption<Number> UNCTBARS_OPACITY = numberValue("error_bars_opacity", 1.0);

    public static final PlotOption<Concordia> CONCORDIA_TYPE =
            new PlotOption<>("concordia_type", Concordia.WETHERILL, Concordia.class, Concordia::getTitle);
    public static final PlotOption<Boolean> CONCORDIA_LINE = booleanValue("concordia_line", false);
    public static final PlotOption<String> CONCORDIA_LINE_FILL = stringValue("concordia_line_fill", "blue");
    public static final PlotOption<Number> CONCORDIA_LINE_OPACITY = numberValue("concordia_line_opacity", 1.0);
    public static final PlotOption<Boolean> CONCORDIA_ENVELOPE = booleanValue("concordia_envelope", false);
    public static final PlotOption<String> CONCORDIA_ENVELOPE_FILL = stringValue("concordia_envelope_fill", "lightgray");
    public static final PlotOption<Number> CONCORDIA_ENVELOPE_OPACITY = numberValue("concordia_envelope_opacity", 1.0);

    public static final PlotOption<Boolean> EVOLUTION = booleanValue("evolution", false);
    public static final PlotOption<Boolean> MCLEAN_REGRESSION = booleanValue("regression_mclean", false);
    public static final PlotOption<Boolean> MCLEAN_REGRESSION_ENVELOPE = booleanValue("regression_mclean_envelope", false);

    public static final List<PlotOption<?>> ALL;
    static {
        List<PlotOption<?>> all = new ArrayList<>();
        Collections.addAll(all,
                TITLE,

                X_AXIS, X_MIN, X_MAX,

                Y_AXIS, Y_MIN, Y_MAX,

                ISOTOPE_SYSTEM,
                UNCERTAINTY,
                LAMBDA_U234, LAMBDA_U235, LAMBDA_U238, LAMBDA_TH230,
                R238_235S,

                SHOW_UNINCLUDED,

                POINTS, POINTS_FILL, POINTS_OPACITY,

                ELLIPSES, ELLIPSES_FILL, ELLIPSES_OPACITY,

                UNCTBARS, UNCTBARS_FILL, UNCTBARS_OPACITY,

                CONCORDIA_TYPE,
                CONCORDIA_LINE, CONCORDIA_LINE_FILL, CONCORDIA_LINE_OPACITY,
                CONCORDIA_ENVELOPE, CONCORDIA_ENVELOPE_FILL, CONCORDIA_ENVELOPE_OPACITY,

                EVOLUTION,

                MCLEAN_REGRESSION, MCLEAN_REGRESSION_ENVELOPE
        );
        ALL = Collections.unmodifiableList(all);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private PlotOption(String key, T defaultValue, Class<T> valueType) {
        this(key, defaultValue, valueType, null);
    }

    private PlotOption(String key, T defaultValue, Class<T> valueType, Function<T, Object> toJS) {
        super(key, key, defaultValue, valueType, toJS);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static PlotOption<Number> forLambda(Lambda lambda) {
        switch(lambda) {
            case Th230:
                return LAMBDA_TH230;
            case U234:
                return LAMBDA_U234;
            case U235:
                return LAMBDA_U235;
            case U238:
                return LAMBDA_U238;
        }
        return null;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private static PlotOption<Number> numberValue(String keyString, Number defaultValue) {
        return new PlotOption<>(keyString, defaultValue, Number.class);
    }

    private static PlotOption<Boolean> booleanValue(String keyString, Boolean defaultValue) {
        return new PlotOption<>(keyString, defaultValue, Boolean.class);
    }

    private static PlotOption<String> stringValue(String keyString, String defaultValue) {
        return new PlotOption<>(keyString, defaultValue, String.class);
    }

}
