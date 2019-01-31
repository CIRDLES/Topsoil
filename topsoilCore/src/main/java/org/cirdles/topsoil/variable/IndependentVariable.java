package org.cirdles.topsoil.variable;

/**
 * @author marottajb
 */
public enum IndependentVariable implements Variable<Double> {

    X("x", "X"),
    Y("y", "Y"),
    RHO("rho", "rho");

    private String name;
    private String abbr;

    IndependentVariable(String name, String abbreviation) {
        this.name = name;
        this.abbr = abbreviation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbbreviation() {
        return abbr;
    }
}
