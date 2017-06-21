package org.cirdles.topsoil.app.util.dialog;

/**
 * Keys for obtaining information from the result of a {@link DataImportDialog}.
 *
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