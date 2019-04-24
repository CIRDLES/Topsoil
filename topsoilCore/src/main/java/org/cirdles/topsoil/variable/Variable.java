package org.cirdles.topsoil.variable;

import org.cirdles.topsoil.constant.ParameterizedConstant;

/**
 * Represents a plotting variable that has associated values of type {@code T}.
 *
 * For example, the variable {@link Variables#LABEL} has a type of {@code String}, so it may be associated with
 * {@code String} values.
 *
 * @param <T>   type of values for this Variable
 */
public class Variable<T> extends ParameterizedConstant<T> {

    private String abbreviation;

    Variable(String name, String abbreviation, String key, T defaultValue) {
        super(name, key, defaultValue);
        this.abbreviation = abbreviation;
    }

    /**
     * Returns the abbreviation of this variable.
     *
     * @return  String abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }
}
