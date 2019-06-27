package org.cirdles.topsoil;

import org.cirdles.topsoil.symbols.SimpleSymbolKey;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Represents a plotting variable that has associated values of type {@code T}.
 *
 * For example, the variable {@link Variable#LABEL} has a type of {@code String}, so it may be associated with
 * {@code String} values.
 *
 * @param <T>   type of values for this Variable
 */
public class Variable<T> extends SimpleSymbolKey<T> {

    public static final Variable<Number> X = new Variable<>("x", "X", "x", 0.0, Number.class);
    public static final DependentVariable SIGMA_X = new DependentVariable("sigma_x", "σX", "sigma_x", 0.0, X);
    public static final Variable<Number> Y = new Variable<>("y", "Y", "y", 0.0, Number.class);
    public static final DependentVariable SIGMA_Y = new DependentVariable("sigma_y", "σY", "sigma_y", 0.0, Y);
    public static final Variable<Number> RHO = new Variable<>("rho", "rho", "rho", 0.0, Number.class);

    public static final Variable<String> LABEL = new Variable<>("label", "label", "label", "row", String.class);
    public static final Variable<String> ALIQUOT = new Variable<>("aliquot", "alqt.", "aliquot", "aliquot", String.class);

    public static final Variable<Boolean> SELECTED = new Variable<>("selected", "selected", "selected", true, Boolean.class);
    public static final Variable<Boolean> VISIBLE = new Variable<>("visible", "visible", "visible", true, Boolean.class);

    public static final List<Variable<?>> ALL = Collections.unmodifiableList(asList(
            X,
            SIGMA_X,
            Y,
            SIGMA_Y,
            RHO,
            LABEL,
            ALIQUOT,
            SELECTED,
            VISIBLE
    ));

    public static final List<Variable<?>> CLASSIC = Collections.unmodifiableList(asList(
            X, SIGMA_X, Y, SIGMA_Y, RHO
    ));

    private String abbreviation;

    protected Variable(String name, String abbreviation, String key, T defaultValue, Class<T> valueType) {
        super(name, key, defaultValue, valueType);
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

    /**
     * Returns the {@code Variable} object with the specified {@code String} key.
     *
     * @param key   String key
     * @return      Variable with key
     */
    public static Variable<?> variableForKey(String key) {
        for (Variable<?> variable : ALL) {
            if (variable.toJSONString().equals(key)) {
                return variable;
            }
        }
        return null;
    }

    /**
     * Returns the {@code Variable} object with the specified {@code String} abbreviation.
     *
     * @param abbreviation   String abbreviation
     * @return      Variable with abbreviation
     */
    public static Variable<?> variableForAbbreviation(String abbreviation) {
        for (Variable<?> variable : ALL) {
            if (variable.getAbbreviation().equals(abbreviation)) {
                return variable;
            }
        }
        return null;
    }


}
