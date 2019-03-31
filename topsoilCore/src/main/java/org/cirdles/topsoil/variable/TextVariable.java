package org.cirdles.topsoil.variable;

/**
 * A {@code Variable} for a {@code String} data type.
 *
 * @author marottajb
 */
public enum TextVariable implements Variable<String> {

    LABEL("label", "label"),
    ALIQUOT("aliquot", "alqt.");

    private String name;
    private String abbr;

    TextVariable(String name, String abbreviation) {
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
