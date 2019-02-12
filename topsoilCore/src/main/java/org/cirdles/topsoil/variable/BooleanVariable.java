package org.cirdles.topsoil.variable;

/**
 * A {@code Variable} for a {@code Boolean} data type.
 *
 * @author marottajb
 */
public enum BooleanVariable implements Variable<Boolean> {

    SELECTED("Selected", "selected");

    private String name;
    private String abbr;

    BooleanVariable(String name, String abbreviation) {
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
