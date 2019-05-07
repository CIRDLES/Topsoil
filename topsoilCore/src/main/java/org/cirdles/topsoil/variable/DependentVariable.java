package org.cirdles.topsoil.variable;

/**
 * A {@code Variable} for a {@code Double} data type, with some dependency on another {@code Variable}.
 *
 * @author marottajb
 */
public class DependentVariable extends Variable<Number> {

    private Variable<Number> dependency;

    DependentVariable(String name, String abbreviation, String key, Number defaultValue, Variable<Number> dependency) {
        super(name, abbreviation, key, defaultValue);
        this.dependency = dependency;
    }

    /**
     * Returns the {@code Variable} that this {@code DependentVariable} is dependent on.
     *
     * @return  Variable of type Number
     */
    public Variable<Number> getDependency() {
        return dependency;
    }

}
