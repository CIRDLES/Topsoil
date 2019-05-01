package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.variable.Variable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class defines a single datum for a plot. It maps plotting {@link Variable}s to their associated values.
 *
 * {@code PlotDataEntry} encapsulates a map instead of simply inheriting from {@link HashMap} in order to restrict the
 * values that may be set in the map. The type of each value must match the parameterized type of its associated
 * {@code Variable} key.
 */
public class PlotDataEntry {

    private Map<Variable<?>, Object> values = new HashMap<>();

    /**
     * Returns the value associated with the provided {@code Variable}.
     *
     * @param variable  provided Variable
     * @param <T>       the type of the Variable and its value
     *
     * @return          associated value of variable
     */
    public <T> T get(Variable<T> variable) {
        return variable.getType().cast(values.get(variable));
    }

    /**
     * Sets the value of the specified {@code Variable} to the provided {@code Object}.
     *
     * @param variable  specified Variable
     *
     * @param value     provided Object
     */
    public void set(Variable<?> variable, Object value) {
        if (! variable.match(value)) {
            throw new IllegalArgumentException(
                    "Value \"" + value +
                            "\" must be of the same type as Variable (" + variable.getType() + ")."
            );
        }
        values.put(variable, value);
    }

    /**
     * Returns a map of {@code Variable} keys and their associated {@code Object} values.
     *
     * @return  Map of Variable keys to Object values
     */
    public Map<Variable<?>, Object> getMap() {
        return Collections.unmodifiableMap(values);
    }

}
