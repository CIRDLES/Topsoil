package org.cirdles.topsoil.variable;

/**
 * A {@code Variable} for a {@code String} data type.
 *
 * @author marottajb
 */
public enum TextVariable implements Variable<String> {

    LABEL("label", "label", "row"),
    ALIQUOT("aliquot", "alqt.", "aliquot");

    private String name;
    private String abbr;
    private String defaultValue;

    TextVariable(String name, String abbreviation, String defaultValue) {
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
    public String defaultValue() {
        return defaultValue;
    }
}
