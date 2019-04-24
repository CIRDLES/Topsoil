package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.constant.ParameterizedConstant;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Uncertainty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final Property<String> TITLE = new Property<>("Title", "New Plot");

    public static final Property<String> X_AXIS = new Property<>("X Axis", "X Axis");
    public static final Property<String> X_MIN = new Property<>("X Min", "");
    public static final Property<String> X_MAX = new Property<>("X Max", "");

    public static final Property<String> Y_AXIS = new Property<>("Y Axis", "Y Axis");
    public static final Property<String> Y_MIN = new Property<>("Y Min", "");
    public static final Property<String> Y_MAX = new Property<>("Y Max", "");

    public static final Property<String> ISOTOPE_SYSTEM = new Property<>("Isotope System", IsotopeSystem.GENERIC.getName());
    public static final Property<Number> UNCERTAINTY = new Property<>("Uncertainty", Uncertainty.ONE_SIGMA_ABSOLUTE.getMultiplier());
    public static final Property<Number> LAMBDA_U234 = new Property<>("U234", Lambda.U234.getDefaultValue());
    public static final Property<Number> LAMBDA_U235 = new Property<>("U235", Lambda.U235.getDefaultValue());
    public static final Property<Number> LAMBDA_U238 = new Property<>("U238", Lambda.U238.getDefaultValue());
    public static final Property<Number> LAMBDA_TH230 = new Property<>("Th230", Lambda.Th230.getDefaultValue());
    public static final Property<Number> R238_235S = new Property<>("R238_235S", 137.88);

    public static final Property<Boolean> POINTS = new Property<>("Points", true);
    public static final Property<String> POINTS_FILL = new Property<>("Points Fill", "steelblue");
    public static final Property<Number> POINTS_OPACITY = new Property<>("Points Opacity", 1.0);

    public static final Property<Boolean> ELLIPSES = new Property<>("Ellipses", true);
    public static final Property<String> ELLIPSES_FILL = new Property<>("Ellipses Fill", "red");
    public static final Property<Number> ELLIPSES_OPACITY = new Property<>("Ellipses Opacity", 1.0);

    public static final Property<Boolean> UNCTBARS = new Property<>("Unct Bars", false);
    public static final Property<String> UNCTBARS_FILL = new Property<>("Unct Bars Fill", "black");
    public static final Property<Number> UNCTBARS_OPACITY = new Property<>("Unct Bars Opacity", 1.0);

    public static final Property<Boolean> WETHERILL_LINE = new Property<>("Wetherill Line", false);
    public static final Property<Boolean> WETHERILL_ENVELOPE = new Property<>("Wetherill Envelope", false);
    public static final Property<String> WETHERILL_LINE_FILL = new Property<>("Wetherill Line Fill", "blue");
    public static final Property<String> WETHERILL_ENVELOPE_FILL = new Property<>("Wetherill Envelope Fill", "lightgray");

    public static final Property<Boolean> WASSERBURG_LINE = new Property<>("Wasserburg Line", false);
    public static final Property<Boolean> WASSERBURG_ENVELOPE = new Property<>("Wasserburg Envelope", false);
    public static final Property<String> WASSERBURG_LINE_FILL = new Property<>("Wasserburg Line Fill", "blue");
    public static final Property<String> WASSERBURG_ENVELOPE_FILL = new Property<>("Wasserburg Envelope Fill", "lightgray");

    public static final Property<Boolean> EVOLUTION = new Property<>("Evolution Matrix", false);
    public static final Property<Boolean> MCLEAN_REGRESSION = new Property<>("McLean Regression", false);
    public static final Property<Boolean> MCLEAN_REGRESSION_ENVELOPE = new Property<>("McLean Regression Envelope", false);

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

                WETHERILL_LINE, WETHERILL_LINE_FILL,
                WETHERILL_ENVELOPE, WETHERILL_ENVELOPE_FILL,

                WASSERBURG_LINE, WASSERBURG_LINE_FILL,
                WASSERBURG_ENVELOPE, WASSERBURG_ENVELOPE_FILL,

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
     * Constructs a new instance of {@code PlotProperties} with default settings.
     */
    public PlotProperties() {
        this(null);
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
        } else {
            setDefault();
        }
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

        private Property(String key, T defaultValue) {
            super(key, key, defaultValue);
        }
        
    }
    
}
