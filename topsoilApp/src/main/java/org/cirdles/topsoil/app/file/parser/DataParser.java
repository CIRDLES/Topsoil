package org.cirdles.topsoil.app.file.parser;

import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.file.writer.TableFileExtension;
import org.cirdles.topsoil.app.file.UnicodeBOMInputStream;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author marottajb
 */
public interface DataParser {

    ColumnRoot parseColumnTree(String content, String delimiter);

    DataTable parseDataTable(Path path, String delimiter, String label) throws IOException;
    DataTable parseDataTable(String content, String delimiter, String label);

    /**
     * Gets the lines of a text file as an array of {@code String}s.
     *
     * @param   path
     *          the Path to the file to be read
     *
     * @return  array of lines as Strings
     *
     * @throws IOException
     *          if an I/O error occurs opening the file
     */
    static String[] readLines(Path path) throws IOException {
        try (UnicodeBOMInputStream uis = new UnicodeBOMInputStream(Files.newInputStream(path));
             InputStreamReader isr = new InputStreamReader(uis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr) ) {

            uis.skipBOM();  // skips UTF Byte Order Mark, if present

            List<String> content = new ArrayList<>();
            reader.lines().forEach(content::add);

            return content.toArray(new String[]{});
        } catch (IOException e) {
            throw new IOException("Unable to read file at path: " + path.toString() + ".", e);
        }
    }

    /**
     * Gets the lines of a {@code String} as an array of {@code String}s.
     *
     * @param   content
     *          String to be read
     *
     * @return  String[] of lines
     */
    static String[] readLines(String content) {
        return content.split("[\\r\\n]+");
    }

    /**
     * Splits each string line into a {@code String[]} based on the delimiter provided.
     *
     * @param lines         String[]
     * @param delimiter     String delimiter
     *
     * @return              String[][] split lines
     */
    static String[][] readCells(String[] lines, String delimiter) {
        List<List<String>> splits = new ArrayList<>();
        List<String> split;
        for (String line : lines) {
            String[] arr = line.split(delimiter, -1);
            split = new ArrayList<>();
            for (String s : arr) {
                split.add(s.trim());
            }
            splits.add(split);
        }

        // Remove empty rows
        while (splits.get(splits.size() - 1).size() == 1 && splits.get(splits.size() - 1).get(0).equals("")) {
            splits.remove(splits.size() - 1);
        }

        String[][] rtnval = new String[splits.size()][];
        for (int index = 0; index < splits.size(); index++) {
            rtnval[index] = splits.get(index).toArray(new String[]{});
        }

        return rtnval;
    }

    /**
     * Identifies the delimiter of the data file at the specified path, or returns null.
     *
     * @param path          data file Path
     *
     * @return              Delimiter, or null
     *
     * @throws IOException  if file error
     */
    static Delimiter guessDelimiter(Path path) throws IOException {
        TableFileExtension ext = getExtension(path);
        if (ext == TableFileExtension.CSV || ext == TableFileExtension.TSV) {
            return ext.getDelimiter();
        }

        final int NUM_LINES = 5;
        String[] lines = readLines(path);

        return guessDelimiter(Arrays.copyOfRange(lines, 0, Math.min(lines.length, NUM_LINES)));
    }

    /**
     * Identifies the delimiter of the data in the provided string, or returns null.
     *
     * @param content       String data
     * @return              Delimiter, or null
     */
    static Delimiter guessDelimiter(String content) {
        final int NUM_LINES = 5;
        String[] lines = readLines(content);

        return guessDelimiter(Arrays.copyOfRange(lines, 0, NUM_LINES));
    }

    /**
     * Identifies the delimiter of the data in the provided {@code String[]} lines, or returns null.
     *
     * @param lines     String[]
     * @return          Delimiter, or null
     */
    static Delimiter guessDelimiter(String[] lines) {
        for (Delimiter delim : Delimiter.values()) {
            if (isDelimiter(lines, delim)) {
                return delim;
            }
        }
        return null;
    }

    /**
     * Tests whether the provided {@code Delimiter} is a delimiter of the {@code String[]} data.
     *
     * @param lines     String[] data lines
     * @param delim     Delimiter
     *
     * @return          true if delim is the delimiter for the data
     */
    static boolean isDelimiter(String[] lines, Delimiter delim) {
        final int NUM_LINES = 5;
        int numLines = Math.min(NUM_LINES, lines.length);
        int[] counts = new int[numLines];
        for (int i = 0; i < numLines; i++) {
            counts[i] = StringUtils.countOccurrencesOf(lines[i], delim.getValue());
        }

        // If the number of occurrences of delimiter is not the same for each line, return false.;
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] == 0 || (counts[i] != counts[i - 1]) ) {
                return false;
            }
        }
        return true;
    }

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
    static Class getColumnDataType(String[][] rows, int colIndex, int numHeaderRows) {
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
    static DataRow getDataRow(String label, String[] row, List<DataColumn<?>> columns) {
        DataRow newRow = new DataRow(label);
        String str;
        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            str = row[colIndex];
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
     * Checks whether a file contains any model.
     *
     * @param   path
     *          the Path to the file to check
     *
     * @return  true if file is empty
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     */
    static boolean isFileEmpty(Path path) throws IOException {
        BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        String line = bufferedReader.readLine();
        bufferedReader.close();

        return line == null;
    }

    /**
     * Determines whether the specified {@code Path} is to a {@code File} with a supported extension.
     *
     * @param   path
     *          a Path to a file
     *
     * @return  true, if the extension is supported
     */
    static boolean isFileSupported(Path path) {
        return getExtension(path) != null;
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
    static boolean isDouble(String string) {
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

    static TableFileExtension getExtension(Path path) {
        String ext = path.toString().substring(path.toString().lastIndexOf(".") + 1).toUpperCase();
        try {
            return TableFileExtension.valueOf(ext.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
