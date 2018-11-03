package org.cirdles.topsoil.app.util.file;

/**
 * Common delimiters used to separate data values. This is used when attempting to determine the delimiter of a
 * body of text.
 *
 * @author  marottajb
 *
 * @see     FileParser#getDelimiter(String)
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return value;
    }
}
