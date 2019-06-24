package org.cirdles.topsoil.symbols;

import org.json.JSONString;

import java.util.List;
import java.util.Map;

public interface SymbolKey<T> extends JSONString {

    String getTitle();

    T getDefaultValue();

    Class<T> getType();

    /**
     * Accepts a value and determines whether or not it is an appropriate value for this {@code SymbolKey}. Can be
     * overridden to support more restrictive matching.
     *
     * @param value     Object value
     * @return          true if value is an instance of T
     */
    default boolean match(Object value) {
        Class<T> type = getType();
        if (type != null) {
            return type.isInstance(value);
        }
        return value == null;
    }

    /**
     * Accepts a value and converts it into a format that is convertible to JSON. Can be overridden to support more
     * complex object types.
     *
     * @param obj   Object value
     * @return      JSON-friendly Object
     */
    default Object getJSONCompatibleValue(Object obj) {
        if (! match(obj)) {
            throw new IllegalArgumentException(
                    "Value type must match parameterized type of symbol \"" + getTitle() + "\" (" + getType() + ")."
            );
        }

        // If value is parseable by simple-json
        if (obj instanceof String ||
                obj instanceof Number ||
                obj instanceof  Boolean ||
                obj instanceof Map ||
                obj instanceof List) {
            return obj;
        }

        throw new UnsupportedOperationException("Type of SymbolKey is not convertible to JSON.");
    }

}
