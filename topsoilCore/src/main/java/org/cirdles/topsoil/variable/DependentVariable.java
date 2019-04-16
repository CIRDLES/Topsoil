package org.cirdles.topsoil.variable;

/**
 * A {@code Variable} for a {@code Double} data type, with some dependency on another {@code Variable}.
 *
 * @author marottajb
 */
public enum DependentVariable implements Variable<Number> {

    SIGMA_X("sigma_x", "σX", 0.0, IndependentVariable.X),
    SIGMA_Y("sigma_y", "σY", 0.0, IndependentVariable.Y);

    private String name;
    private String abbr;
    private Number defaultValue;
    private Variable<Number> dependency;

    DependentVariable(String name, String abbreviation, Number defaultValue, Variable<Number> dependency) {
        this.name = name;
        this.abbr = abbreviation;
        this.defaultValue = defaultValue;
        this.dependency = dependency;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbbreviation() {
        return abbr;
    }

    @Override
    public Number defaultValue() {
        return defaultValue;
    }

    public Variable<Number> getDependency() {
        return dependency;
    }

}
