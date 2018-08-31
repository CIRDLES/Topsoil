package org.cirdles.topsoil.app.table;

/**
 * @author marottajb
 */
public final class SpreadsheetConstants {

    // Regex
    public static final String DEFAULT_REGULAR_DOUBLE_PATTERN = "0.#########";
    public static final String DEFAULT_SCI_NOTATION_PATTERN = "0.#########E0";
    public static final String REGULAR_PLUS_SCI_NOTATION_REGEX = "^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$";

    // Sizes
    public static final double INIT_COLUMN_WIDTH = 160.0;

    private SpreadsheetConstants() {
        throw new AssertionError();
    }

}
