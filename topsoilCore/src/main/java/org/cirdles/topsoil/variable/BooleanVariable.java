package org.cirdles.topsoil.variable;

/**
 * A {@code Variable} for a {@code Boolean} data type.
 *
 * @author marottajb
 */
public enum BooleanVariable implements Variable<Boolean> {

    SELECTED("selected", "selected", true);

    private String name;
    private String abbr;
    private boolean defaultValue;

    BooleanVariable(String name, String abbreviation, boolean defaultValue) {
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
    public Boolean defaultValue() {
        return defaultValue;
    }

}
