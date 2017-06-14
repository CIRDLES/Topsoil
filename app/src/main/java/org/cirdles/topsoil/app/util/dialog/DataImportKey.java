package org.cirdles.topsoil.app.util.dialog;

/**
 * @author Jake Marotta
 */
public enum DataImportKey {
    TITLE("Title"),
    HEADERS("Headers"),
    DATA("Data"),
    UNCERTAINTY("Uncertainty"),
    ISOTOPE_TYPE("Isotope Type");

    private String key;

    DataImportKey(String key) {
        this.key = key;
    }
}