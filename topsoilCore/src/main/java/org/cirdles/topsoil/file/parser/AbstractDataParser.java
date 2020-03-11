package org.cirdles.topsoil.file.parser;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.Validate;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.data.DataTemplate;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.utils.TopsoilFileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.cirdles.topsoil.utils.TopsoilClassUtils.instantiate;

/**
 * Defines behavior for parsing value-separated data into a {@link DataTable}.
 *
 * @author marottajb
 */
public abstract class AbstractDataParser<T extends DataTable<C, R>, C extends DataColumn<?>, R extends DataRow> implements DataParser<T, C, R> {

    private static final Class[] TABLE_CONSTRUCTOR_ARG_TYPES = {
            DataTemplate.class,
            String.class,
            Uncertainty.class,
            List.class,
            List.class
    };

    private static final Class[] ROW_CONSTRUCOR_ARG_TYPES = {
            String.class
    };

    protected final DataTemplate template;
    protected final Class<T> tableClass;
    protected final Class<C> columnClass;
    protected final Class<R> rowClass;

    @SuppressWarnings("unchecked")  // this is okay because C and R are based off of the T class provided
    public AbstractDataParser(DataTemplate template, Class<T> tableClass) {
        this.template = template;
        this.tableClass = tableClass;

        Class[] tableComponentTypes = TypeResolver.resolveRawArguments(DataTable.class, tableClass);
        this.columnClass = (Class<C>) tableComponentTypes[0];
        this.rowClass = (Class<R>) tableComponentTypes[1];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final T parseDataTable(Path path, String delimiter, String label) throws IOException {
        Validate.notNull(path, "Path cannot be null.");
        Validate.notNull(delimiter, "Delimiter cannot be null.");

        String[] lines = TopsoilFileUtils.readLines(path);
        String[][] cells = TopsoilFileUtils.readCells(lines, delimiter);
        if (label == null) {
            Path fileName = path.getFileName();
            label = (fileName != null) ? fileName.toString() : path.toString();
        }
        return parseDataTable(cells, label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final T parseDataTable(String content, String delimiter, String label) {
        Validate.notNull(content, "String content cannot be null.");
        Validate.notNull(delimiter, "Delimiter cannot be null.");

        if (label == null) {
            label = "DataFromClipboard";
        }

        String[][] cells = TopsoilFileUtils.readCells(TopsoilFileUtils.readLines(content), delimiter);
        return parseDataTable(cells, label);
    }

    protected abstract List<C> parseColumns(String[][] cells, Object... args);

    protected abstract List<R> parseRows(String[][] cells, List<C> leafColumns, Object... args);

    protected T parseDataTable(String[][] cells, String label) {
        List<C> columns = parseColumns(cells);
        List<R> rows = parseRows(cells, columns);

        return instantiate(
                tableClass,
                TABLE_CONSTRUCTOR_ARG_TYPES,
                new Object[]{template, label, null, columns, rows}
        );
    }

    /**
     * Identifies the data type of a column of values in the provided data. Currently, only {@code Number} and
     * {@code String} columns are supported; this method defaults to {@code String}.
     *
     * @param rows          String[][] data
     * @param colIndex      column index
     * @param numHeaderRows number of header rows in the data
     * @return Class of column type
     */
    protected Class getColumnDataType(String[][] rows, int colIndex, int numHeaderRows) {
        final int SAMPLE_SIZE = Math.min(5, rows.length - numHeaderRows);
        boolean isDouble = true;
        int i = numHeaderRows;
        int sampled = 0;
        while (i < rows.length && sampled < SAMPLE_SIZE) {
            if (colIndex < rows[i].length && !rows[i][colIndex].trim().isEmpty()) {
                if (!isDouble(rows[i][colIndex])) {
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
     * @param label   String row label
     * @param row     String[] row values
     * @param columns List of table columns
     * @return DataRow with assigned values
     */
    protected R getTableRow(String label, String[] row, List<C> columns) {
        R newRow = instantiate(rowClass, ROW_CONSTRUCOR_ARG_TYPES, new Object[]{label});
        C col;
        String str;
        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            str = (colIndex < row.length) ? row[colIndex] : "";
            col = columns.get(colIndex);

            if (col.getType() == Number.class) {
                DataColumn<Number> doubleCol = (DataColumn<Number>) col;
                newRow.setValueForColumn(doubleCol, (!str.isEmpty()) ? Double.parseDouble(str) : 0.0);
            } else {
                DataColumn<String> stringCol = (DataColumn<String>) col;
                newRow.setValueForColumn(stringCol, str);
            }
        }
        return newRow;
    }

    protected int countHeaderRows(String[][] rows) {
        int count = 0;
        for (String[] row : rows) {
            if (isDouble(row[0])) {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * Code taken from the documentation for {@code Double.valueOf(String s)}. Checks that a given {@code Stirng} can be
     * parsed into a {@code Double}.
     *
     * @param string the String to check
     * @return true if the String can be parsed into a Double
     */
    protected final boolean isDouble(String string) {
        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp = "[eE][+-]?" + Digits;
        final String fpRegex =
                ("[\\x00-\\x20]*" +  // Optional leading "whitespace"
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
                        "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +

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
