package org.cirdles.topsoil.variable;

/**
 * @author marottajb
 */
public enum DependentVariable implements Variable<Double> {

    SIGMA_X("sigma_x", "σX", IndependentVariable.X),
    SIGMA_Y("sigma_y", "σY", IndependentVariable.Y);

    private String name;
    private String abbr;
    private Variable<Double> dependency;

    DependentVariable(String name, String abbreviation, Variable<Double> dependency) {
        this.name = name;
        this.abbr = abbreviation;
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

    public Variable<Double> getDependency() {
        return dependency;
    }

}
