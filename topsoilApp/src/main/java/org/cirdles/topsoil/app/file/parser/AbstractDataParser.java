package org.cirdles.topsoil.app.file.parser;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.file.TopsoilFileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Defines behavior for parsing value-separated data into a {@link DataTable}.
 *
 * @author marottajb
 */
public abstract class AbstractDataParser implements DataParser {

    /** {@inheritDoc} */
    @Override
    public final DataTable parseDataTable(Path path, String delimiter, String label) throws IOException {
        if (path != null && delimiter != null) {
            String[] lines = TopsoilFileUtils.readLines(path);
            String[][] cells = TopsoilFileUtils.readCells(lines, delimiter);
            if (label == null) {
                Path fileName = path.getFileName();
                if (fileName != null) {
                    label = fileName.toString();
                } else {
                    label = path.toString();
                }
            }
            return parseDataTable(cells, label);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public final DataTable parseDataTable(String content, String delimiter, String label) {
        if (content != null && delimiter != null) {
            String[][] rows = TopsoilFileUtils.readCells(TopsoilFileUtils.readLines(content), delimiter);
            return parseDataTable(rows, label);
        }
        return null;
    }

    protected abstract DataTable parseDataTable(String[][] cells, String label);

    /**
     * Identifies the data type of a column of values in the provided data. Currently, only {@code Number} and
     * {@code String} columns are supported; this method defaults to {@code String}.
     *
     * @param rows          String[][] data
     * @param colIndex      column index
     * @param numHeaderRows number of header rows in the data
     *
     * @return              Class of column type
     */
    protected final Class getColumnDataType(String[][] rows, int colIndex, int numHeaderRows) {
        final int SAMPLE_SIZE = Math.min(5, rows.length - numHeaderRows);
        boolean isDouble = true;
        int i = numHeaderRows;
        int sampled = 0;
        while (i < rows.length && sampled < SAMPLE_SIZE) {
            if (colIndex < rows[i].length && !rows[i][colIndex].trim().isEmpty()) {
                if (! isDouble(rows[i][colIndex])) {
                    isDouble = false;
                    break;
                } else {
                    sampled++;
                }
            }
            i++;
        }
        return isDouble ? Number.class : String.class;
    }

    /**
     * Parses a {@code DataRow} from the provided {@code String[]} row, given the provided columns.
     *
     * @param label     String row label
     * @param row       String[] row values
     * @param columns   List of table columns
     *
     * @return          DataRow with assigned values
     */
    protected DataRow getDataRow(String label, String[] row, List<DataColumn<?>> columns) {
        DataRow newRow = new DataRow(label);
        String str;
        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            str = (colIndex < row.length) ? row[colIndex] : "";

            if (columns.get(colIndex).getType() == Number.class) {
                DataColumn<Number> doubleCol = (DataColumn<Number>) columns.get(colIndex);
                newRow.setValueForColumn(doubleCol, (! str.isEmpty()) ? Double.parseDouble(str) : 0.0);
            } else {
                DataColumn<String> stringCol = (DataColumn<String>) columns.get(colIndex);
                newRow.setValueForColumn(stringCol, row[colIndex]);
            }
        }
        return newRow;
    }

    /**
     * Code taken from the documentation for {@code Double.valueOf(String s)}. Checks that a given {@code Stirng} can be
     * parsed into a {@code Double}.
     *
     * @param   string
     *          the String to check
     *
     * @return  true if the String can be parsed into a Double
     */
    protected final boolean isDouble(String string) {
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string

                        // A decimal floating-point string representing a finite positive
                        // number without a leading sign has at most five basic pieces:
                        // Digits . Digits ExponentPart FloatTypeSuffix
                        //
                        // Since this method allows integer-only strings as input
                        // in addition to strings of floating-point literals, the
                        // two sub-patterns below are simplifications of the grammar
                        // productions from section 3.10.2 of
                        // The Java Language Specification.

                        // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                        "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|"+

                        // . Digits ExponentPart_opt FloatTypeSuffix_opt
                        "(\\." + Digits + "(" + Exp + ")?)|" +

                        // Hexadecimal strings
                        "((" +
                        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "(\\.)?)|" +

                        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        return Pattern.matches(fpRegex, string);
    }
}
