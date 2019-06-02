package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.plot.feature.Concordia;
import org.cirdles.topsoil.symbols.SimpleSymbolKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Defines a plot property of type {@code T}.
 *
 * @param <T>   the type of the value that should be associated with this property
 */
public final class PlotOption<T> extends SimpleSymbolKey<T> {

    public static PlotOption<?> forKey(String key) {
        for (PlotOption<?> option : ALL) {
            if (option.toJSONString().equals(key)) {
                return option;
            }
        }
        return null;
    }

    public static final PlotOption<String> TITLE = stringValue("title", "New Plot");

    public static final PlotOption<String> X_AXIS = stringValue("x-axis", "X Axis");
    public static final PlotOption<Number> X_MIN = numberValue("x-min", 0.0);
    public static final PlotOption<Number> X_MAX = numberValue("x-max", 1.0);

    public static final PlotOption<String> Y_AXIS = stringValue("y-axis", "Y Axis");
    public static final PlotOption<Number> Y_MIN = numberValue("y-min", 0.0);
    public static final PlotOption<Number> Y_MAX = numberValue("y-max", 1.0);

    public static final PlotOption<IsotopeSystem> ISOTOPE_SYSTEM =
            new PlotOption<>("isotope-system", IsotopeSystem.GENERIC, IsotopeSystem.class, IsotopeSystem::getName);
    public static final PlotOption<Uncertainty> UNCERTAINTY =
            new PlotOption<>("uncertainty", Uncertainty.ONE_SIGMA_ABSOLUTE, Uncertainty.class, Uncertainty::getMultiplier);
    public static final PlotOption<Number> LAMBDA_U234 = numberValue("lambda-234", Lambda.U234.getDefaultValue());
    public static final PlotOption<Number> LAMBDA_U235 = numberValue("lambda-235", Lambda.U235.getDefaultValue());
    public static final PlotOption<Number> LAMBDA_U238 = numberValue("lambda-238", Lambda.U238.getDefaultValue());
    public static final PlotOption<Number> LAMBDA_TH230 = numberValue("lambda-230", Lambda.Th230.getDefaultValue());
    public static final PlotOption<Number> R238_235S = numberValue("R238_235S", 137.88);

    public static final PlotOption<Boolean> POINTS = booleanValue("points", true);
    public static final PlotOption<String> POINTS_FILL = stringValue("points-fill", "steelblue");
    public static final PlotOption<Number> POINTS_OPACITY = numberValue("points-opacity", 1.0);

    public static final PlotOption<Boolean> ELLIPSES = booleanValue("ellipses", true);
    public static final PlotOption<String> ELLIPSES_FILL = stringValue("ellipses-fill", "red");
    public static final PlotOption<Number> ELLIPSES_OPACITY = numberValue("ellipses-opacity", 1.0);

    public static final PlotOption<Boolean> UNCTBARS = booleanValue("unctbars", false);
    public static final PlotOption<String> UNCTBARS_FILL = stringValue("unctbars-fill", "black");
    public static final PlotOption<Number> UNCTBARS_OPACITY = numberValue("unctbars-opacity", 1.0);

    public static final PlotOption<Concordia> CONCORDIA_TYPE =
            new PlotOption<>("concordia-type", Concordia.WETHERILL, Concordia.class, Concordia::getTitle);
    public static final PlotOption<Boolean> CONCORDIA_LINE = booleanValue("concordia-line", false);
    public static final PlotOption<String> CONCORDIA_LINE_FILL = stringValue("concordia-line-fill", "blue");
    public static final PlotOption<Number> CONCORDIA_LINE_OPACITY = numberValue("concordia-line-opacity", 1.0);
    public static final PlotOption<Boolean> CONCORDIA_ENVELOPE = booleanValue("concordia-envelope", false);
    public static final PlotOption<String> CONCORDIA_ENVELOPE_FILL = stringValue("concordia-envelope-fill", "lightgray");
    public static final PlotOption<Number> CONCORDIA_ENVELOPE_OPACITY = numberValue("concordia-envelope-opacity", 1.0);

    public static final PlotOption<Boolean> EVOLUTION = booleanValue("evolution", false);
    public static final PlotOption<Boolean> MCLEAN_REGRESSION = booleanValue("regression-mclean", false);
    public static final PlotOption<Boolean> MCLEAN_REGRESSION_ENVELOPE = booleanValue("regression-mclean-envelope", false);

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
