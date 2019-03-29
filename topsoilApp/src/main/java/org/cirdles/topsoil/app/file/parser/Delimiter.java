package org.cirdles.topsoil.app.file.parser;

/**
 * Common delimiters used to separate model values. This is used when attempting to determine the delimiter of a
 * body of text.
 *
 * @author  marottajb
 */
public enum Delimiter {
    COMMA("Comma", ","),
    TAB("Tab", "\t"),
    COLON("Colon", ":"),
    SEMICOLON("Semicolon", ";");

    private String name;
    private String value;

    Delimiter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getReadableName() {
        return name;
    }

    /**
     * Returns the delimiter {@code String}.
     *
     * @return  String delimiter value
     */
    public String getValue() {
        return value;
    }
}
