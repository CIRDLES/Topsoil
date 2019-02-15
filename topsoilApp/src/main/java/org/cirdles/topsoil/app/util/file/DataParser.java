package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.model.*;
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

    ColumnTree parseColumnTree(Path path, String delimiter) throws IOException;
    ColumnTree parseColumnTree(String content, String delimiter);

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
             InputStreamReader isr = new InputStreamReader(uis);
             BufferedReader reader = new BufferedReader(isr) ) {

            uis.skipBOM();  // skips UTF Byte Order Mark, if present

            List<String> content = new ArrayList<>();
            reader.lines().forEach(content::add);

            return content.toArray(new String[content.size()]);
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

    static String[][] readCells(String[] lines, String delimiter) {
        List<List<String>> splits = new ArrayList<>();
        for (String line : lines) {
            List<String> split = new ArrayList<>(Arrays.asList(line.split(delimiter, -1)));
            for (int index = 0; index < split.size(); index++) {
                // @TODO Get rid of BOM characters
                split.set(index, split.get(index).trim());
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

    static Delimiter guessDelimiter(Path path) throws IOException {

        String ext = getExtension(path);
        for (TopsoilFileChooser.TableFileExtension extension : TopsoilFileChooser.TableFileExtension.values()) {
            if (extension.getExtension().equals(ext)) {
                return extension.getDelimiter();
            }
        }

        final int NUM_LINES = 5;
        String[] lines = readLines(path);

        return guessDelimiter(Arrays.copyOfRange(lines, 0, NUM_LINES));
    }

    static Delimiter guessDelimiter(String content) {
        final int NUM_LINES = 5;
        String[] lines = readLines(content);

        return guessDelimiter(Arrays.copyOfRange(lines, 0, NUM_LINES));
    }

    static Delimiter guessDelimiter(String[] lines) {
        for (Delimiter delim : Delimiter.values()) {
            if (isDelimiter(lines, delim)) {
                return delim;
            }
        }
        return null;
    }

    /**
     * Guesses whether the specified {@code String} is a delimiter for the provided lines. This is done by taking a
     * subset of lines and counting the number of times the potential delimiter occurs in each line. If the number of
     * occurrences is the same for each line, then the {@code String} is likely a delimiter.
     *
     * @param   lines
     *          a String[] of lines containing model
     * @param   delim
     *          the potential String delimiter
     *
     * @return  true, if delimiter occurs the same number of times in each line
     */
    static boolean isDelimiter(String[] lines, Delimiter delim) {
        final int NUM_LINES = 5;
        int numLines = Math.min(NUM_LINES, lines.length);

        int[] counts = new int[numLines];

        for (int i = 0; i < numLines; i++) {
            counts[i] = StringUtils.countOccurrencesOf(lines[i], delim.toString());
        }

        // If the number of occurrences of delimiter is not the same for each line, return false.
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] == 0 || (counts[i] != counts[i - 1]) ) {
                return false;
            }
        }

        return true;
    }

    static Class getColumnDataType(String[][] rows, int colIndex, int numHeaderRows) {
        final int SAMPLE_SIZE = 5;
        boolean isDouble = true;
        for (int i = numHeaderRows; i < numHeaderRows + SAMPLE_SIZE - 1; i++) {
            try {
                Double.parseDouble(rows[i][colIndex]);
            } catch (NumberFormatException e) {
                isDouble = false;
                break;
            }
        }
        return isDouble ? Double.class : String.class;
    }

    static List<DataValue<?>> getValuesForRow(String[] row, List<DataColumn<?>> columns) {
        List<DataValue<?>> values = new ArrayList<>();
        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            if (columns.get(colIndex).getType() == Double.class) {
                DataColumn<Double> doubleCol = (DataColumn<Double>) columns.get(colIndex);
                values.add(new DoubleValue(doubleCol, Double.parseDouble(row[colIndex])));
            } else {
                DataColumn<String> stringCol = (DataColumn<String>) columns.get(colIndex);
                values.add(new StringValue(stringCol, row[colIndex]));
            }
        }
        return values;
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
    static boolean isFileSupported(Path path) throws IOException {
        try {
            TopsoilFileChooser.TableFileExtension.valueOf(getExtension(path).toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
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

    static String getExtension(Path path) {
        String fileName = path.getFileName().toString();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        return ext;
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    /**
     * Common delimiters used to separate model values. This is used when attempting to determine the delimiter of a
     * body of text.
     *
     * @author  marottajb
     */
    enum Delimiter {

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

        public String getValue() {
            return value;
        }
    }

}
