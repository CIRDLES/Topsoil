package org.cirdles.topsoil.app.util.serialization;

/**
 * A collection of keys used for storing table information in a {@link SerializableTopsoilSession}.
 *
 * @author marottajb
 */
public enum TableDataType {

    TITLE("Title"),
    HEADERS("Headers"),
    ISOTOPE_TYPE("Isotope Type"),
    UNCERTANTY_FORMAT("Uncertainty Format"),
    DATA("Data"),
    VARIABLE_ASSIGNMENTS("Variable Assignments"),
    PLOTS("Plots");

    private String key;

    TableDataType(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
