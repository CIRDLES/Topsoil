package org.cirdles.topsoil.variable;

/**
 * A {@code Variable} for a {@code Double} data type.
 *
 * @author marottajb
 */
public enum IndependentVariable implements Variable<Number> {

    X("x", "X", 0.0),
    Y("y", "Y", 0.0),
    RHO("rho", "rho", 0.0);

    private String name;
    private String abbr;
    private Number defaultValue;

    IndependentVariable(String name, String abbreviation, Number defaultValue) {
        this.name = name;
        this.abbr = abbreviation;
        this.defaultValue = defaultValue;
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
}
