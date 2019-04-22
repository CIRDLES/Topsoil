package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.variable.Variable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PlotDataEntry {

    private Map<Variable<?>, Object> values = new HashMap<>();

    public <T> T get(Variable<T> variable) {
        return (T) values.get(variable);
    }

    public void set(Variable<?> variable, Object value) {
        if (! variable.match(value)) {
            throw new IllegalArgumentException(
                    "Value \"" + value +
                            "\" must be of the same type as Variable (" + variable.getType() + ")."
            );
        }
        values.put(variable, value);
    }

    public Map<Variable<?>, Object> getAll() {
        return Collections.unmodifiableMap(values);
    }

}
