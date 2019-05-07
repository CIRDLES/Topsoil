package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.constant.ParameterizedConstant;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Uncertainty;
import org.cirdles.topsoil.plot.feature.Concordia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This class defines numerous plot properties that may be set on a plot, and instances of this class contain a map of
 * {@link Property} objects to {@code Object}s that represent their value.
 *
 * @author marottajb
 */
public class PlotProperties {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    public static final Property<String> TITLE = new Property<>("title", "New Plot");

    public static final Property<String> X_AXIS = new Property<>("x-axis", "X Axis");
    public static final Property<Number> X_MIN = new Property<>("x-min", 0.0);
    public static final Property<Number> X_MAX = new Property<>("x-max", 1.0);

    public static final Property<String> Y_AXIS = new Property<>("y-axis", "Y Axis");
    public static final Property<Number> Y_MIN = new Property<>("y-min", 0.0);
    public static final Property<Number> Y_MAX = new Property<>("y-max", 1.0);

    public static final Property<IsotopeSystem> ISOTOPE_SYSTEM = new Property<>("isotope-system", IsotopeSystem.GENERIC, IsotopeSystem::getName);
    public static final Property<Uncertainty> UNCERTAINTY = new Property<>("uncertainty", Uncertainty.ONE_SIGMA_ABSOLUTE, Uncertainty::getMultiplier);
    public static final Property<Number> LAMBDA_U234 = new Property<>("lambda-234", Lambda.U234.getDefaultValue());
    public static final Property<Number> LAMBDA_U235 = new Property<>("lambda-235", Lambda.U235.getDefaultValue());
    public static final Property<Number> LAMBDA_U238 = new Property<>("lambda-238", Lambda.U238.getDefaultValue());
    public static final Property<Number> LAMBDA_TH230 = new Property<>("lambda-230", Lambda.Th230.getDefaultValue());
    public static final Property<Number> R238_235S = new Property<>("R238_235S", 137.88);

    public static final Property<Boolean> POINTS = new Property<>("points", true);
    public static final Property<String> POINTS_FILL = new Property<>("points-fill", "steelblue");
    public static final Property<Number> POINTS_OPACITY = new Property<>("points-opacity", 1.0);

    public static final Property<Boolean> ELLIPSES = new Property<>("ellipses", true);
    public static final Property<String> ELLIPSES_FILL = new Property<>("ellipses-fill", "red");
    public static final Property<Number> ELLIPSES_OPACITY = new Property<>("ellipses-opacity", 1.0);

    public static final Property<Boolean> UNCTBARS = new Property<>("unctbars", false);
    public static final Property<String> UNCTBARS_FILL = new Property<>("unctbars-fill", "black");
    public static final Property<Number> UNCTBARS_OPACITY = new Property<>("unctbars-opacity", 1.0);

    public static final Property<Concordia> CONCORDIA_TYPE = new Property<>("concordia-type", Concordia.WETHERILL, Concordia::getTitle);
    public static final Property<Boolean> CONCORDIA_LINE = new Property<>("concordia-line", false);
    public static final Property<String> CONCORDIA_LINE_FILL = new Property<>("concordia-line-fill", "blue");
    public static final Property<Number> CONCORDIA_LINE_OPACITY = new Property<>("concordia-line-opacity", 1.0);
    public static final Property<Boolean> CONCORDIA_ENVELOPE = new Property<>("concordia-envelope", false);
    public static final Property<String> CONCORDIA_ENVELOPE_FILL = new Property<>("concordia-envelope-fill", "lightgray");
    public static final Property<Number> CONCORDIA_ENVELOPE_OPACITY = new Property<>("concordia-envelope-opacity", 1.0);

    public static final Property<Boolean> EVOLUTION = new Property<>("evolution", false);
    public static final Property<Boolean> MCLEAN_REGRESSION = new Property<>("regression-mclean", false);
    public static final Property<Boolean> MCLEAN_REGRESSION_ENVELOPE = new Property<>("regression-mclean-envelope", false);

    public static final List<Property<?>> ALL;
    static {
        List<Property<?>> all = new ArrayList<>();
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
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Map<Property<?>, Object> properties = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new instance of {@code PlotProperties} with no settings.
     */
    public PlotProperties() {

    }

    /**
     * Constructs a new instance of {@code PlotProperties} with the same settings as the provided {@code PlotProperties}
     * instance.
     *
     * @param props     provided PlotProperties
     */
    public PlotProperties(PlotProperties props) {
        if (props != null) {
            for (Property<?> property : ALL) {
                this.properties.put(property, props.get(property));
            }
        }
    }

    /**
     * Returns a new instance of {@code PlotProperties} with default settings.
     *
     * @return  default PlotProperties
     */
    public static PlotProperties defaultProperties() {
        PlotProperties properties = new PlotProperties();
        properties.setDefault();
        return properties;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Sets all {@link Property} keys in {@link PlotProperties#properties} to their default values.
     */
    public void setDefault() {
        for (Property<?> property : ALL) {
            properties.put(property, property.getDefaultValue());
        }
    }

    /**
     * Returns the value associated with the specified {@code Property}.
     *
     * @param property  Property object
     * @param <T>       type of the Property object and of the value object
     *
     * @return          value object
     */
    public <T> T get(Property<T> property) {
        Class<T> clazz = property.getType();
        return clazz.cast(properties.get(property));
    }

    /**
     * Sets the value of the specified {@code Property} to the provided {@code Object}.
     *
     * @param property      Property key
     * @param value         Object value
     */
    public void set(Property<?> property, Object value) {
        if (! property.match(value)) {
            throw new IllegalArgumentException(
                    "Value \"" + value +
                    "\" must be of the same type as the Property (" + property.getType() + ")."
            );
        }
        properties.put(property, value);
    }

    /**
     * Sets the values of all properties to those in the provided {@code PlotProperties} instance.
     *
     * @param plotProps     PlotProperties instance
     */
    public void setAll(PlotProperties plotProps) {
        for (Map.Entry<Property<?>, Object> entry : plotProps.properties.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
        return;
    }

    /**
     * Sets the values of the provided properties to their mapped values.
     *
     * @param properties    Map of Property keys to Object values
     */
    public void setAll(Map<Property<?>, Object> properties) {
        for (Map.Entry<Property<?>, Object> entry : properties.entrySet()) {
            if (! entry.getKey().match(entry.getValue())) {
                throw new IllegalArgumentException(
                        "Value \"" + entry.getValue() +
                        "\" must be of the same type as the Property (" + entry.getKey().getType() + ")."
                );
            }
        }
        this.properties.putAll(properties);
    }

    /**
     * Returns an unmodifiable map of properties and their associated {@code Object} values.
     *
     * @return  Map of Property keys to Object values
     */
    public Map<Property<?>, Object> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    /**
     * Returns the {@code Property} with the specified {@code String} key.
     *
     * @param key   String key
     *
     * @return      Property with key
     */
    public static Property<?> propertyForKey(String key) {
        for (Property<?> property : ALL) {
            if (property.getKeyString().equals(key)) {
                return property;
            }
        }
        return null;
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    /**
     * Defines a plot property that may be set with a value of the same type {@code T}.
     *
     * @param <T>   the type of the Property's value
     */
    public static final class Property<T> extends ParameterizedConstant<T> {

        private Function<T, Object> jsConverter;

        private Property(String key, T defaultValue) {
            this(key, defaultValue, null);
        }

        private Property(String key, T defaultValue, Function<T, Object> toJS) {
            super(key, key, defaultValue);

            if (toJS == null) {
                toJS = (T value) -> value;
            }
            this.jsConverter = toJS;
        }

        public Object toJSCompatibleValue(Object value) {
            if (value != null) {
                if (match(value)) {
                    return jsConverter.apply((T) value);
                } else {
                    throw new IllegalArgumentException("Value must be of type " + type + ".");
                }
            }
            return null;
        }
        
    }
    
}
