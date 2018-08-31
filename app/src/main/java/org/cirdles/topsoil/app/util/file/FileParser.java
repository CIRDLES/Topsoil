package org.cirdles.topsoil.app.util.file;

import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A set of utility methods for parsing data from text files.
 *
 * @author marottajb
 */
public class FileParser {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Parse data from the file at the specified {@code Path}.
     *
     * @param   path
     *          the Path to the file
     * @param   delim
     *          String delimiter
     *
     * @return  data as a List of Lists of Doubles
     *
     * @throws IOException
     *         if an I/O error occurs opening the file
     */
    public static Double[][] parseData( Path path, String delim ) throws IOException {

        return delim != null ? parseData(readLines(path), delim) : null;
    }

    /**
     * Parse data from the provided {@code String}.
     *
     * @param   content
     *          String text
     * @param   delim
     *          String delimiter
     *
     * @return  data as a List of Lists of Doubles
     *
     * @throws IOException
     *         if an I/O error occurs opening the file
     */
    public static Double[][] parseData( String content, String delim ) throws IOException {

        return delim != null ? parseData(readLines(content), delim) : null;
    }

    /**
     * Returns the delimiter used to separate data values in the file at the specified {@code Path}, if it can be
     * determined.
     *
     * @param   path
     *          a Path to a delimited-data file
     *
     * @return  the identified String delimiter
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     */
    public static String getDelimiter(Path path) throws IOException {
        String extension = getExtension(path);

        Delimiter delim = null;
        for (TableFileExtension ext : TableFileExtension.values()) {
            if (ext.toString().equals(extension)) {
                delim = ext.getDelimiter();
            }
        }

        return delim != null ? delim.toString() : getDelimiter(readLines(path));
    }

    /**
     * Returns the delimiter used to separate data values in a {@code String} of text, if it can be determined.
     *
     * @param   content
     *          a String containing delimited data lines
     *
     * @return  the identified String delimiter
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     */
    public static String getDelimiter(String content) throws IOException {
        return getDelimiter(readLines(content));
    }

    /**
     * Gets the extension of the file at the specified {@code Path}.
     *
     * @param   path
     *          the Path to the file in question
     *
     * @return  the file extension as a String
     */
    public static String getExtension(Path path) {
        if (path != null && path.toFile().isFile() && path.toFile().exists()) {
            String fileName = path.toString();
            return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        }
        return null;
    }

    /**
     * Returns the estimated header rows from the specified file {@code Path}.
     * <p>
     * If no headers are found, the method returns {@code null}.
     *
     * @param   path
     *          a Path to a file which may have headers
     * @param   delim
     *          the String delimiter used to separate values in the file
     *
     * @return  array of String headers, if found; else null
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     */
    public static String[] parseHeaders(Path path, String delim) throws IOException {
        return parseHeaders(readLines(path), delim);
    }

    /**
     * Returns the estimated header rows from the provided {@code String} content.
     * <p>
     * If no headers are found, the method returns {@code null}.
     *
     * @param   content
     *          a Path to a file which may have headers
     * @param   delim
     *          the String delimiter used to separate values in the file
     *
     * @return  array of String headers, if found; else null
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     */
    public static String[] parseHeaders(String content, String delim) throws IOException {
        return parseHeaders(readLines(content), delim);
    }

    /**
     * Checks whether a file contains any data.
     *
     * @param   path
     *          the Path to the file to check
     *
     * @return  true if file is empty
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     */
    public static boolean isFileEmpty(Path path) throws IOException {

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
    public static boolean isFileSupported(Path path) {
        try {
            TableFileExtension.valueOf(getExtension(path).toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * Determines whether the file at the specified {@code Path} contains valid data.
     * <p>
     * At time of writing, the criteria that this method checks to determine validity are:
     * <ul>
     * <li> The character encoding of the file is UTF-8.
     * </ul>
     *
     * @param   path
     *          a Path to a file
     *
     * @return  true if the file's data are valid
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     */
    public static boolean isFileValid(Path path) throws IOException {
        try {
            Files.newBufferedReader(path, StandardCharsets.UTF_8).close();
            return true;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private static String getDelimiter(String[] lines) {
        final int NUM_LINES = 5;
        String rtnval = null;

        if (lines.length > 1) {
            if (lines.length > NUM_LINES) {
                lines = Arrays.copyOfRange(lines, 0, NUM_LINES);
            }

            for (Delimiter delim : Delimiter.values()) {
                if (isDelimiter(lines, delim)) {
                    rtnval = delim.toString();
                    break;
                }
            }
        }

        return rtnval;
    }

    private static String[] parseHeaders(String[] lines, String delim) {

        if (lines != null && delim != null) {
        	String[] lineOne = lines[0].split(delim, 0);

        	if (isHeaderRow(lineOne)) {
        		String[] lineTwo = lines[1].split(delim, 0);
        		if (isHeaderRow(lineTwo)) {
			        for (int i = 0; i < lineOne.length; i++) {
				        lineOne[i] = lineOne[i].concat("\t" + lineTwo[i]);
			        }
		        }
		        return lineOne;
	        }
        }
        return null;
    }
    private static boolean isHeaderRow(String[] row) {
    	for (String str : row) {
    		try {
    			Double.parseDouble(str);
		    } catch (NumberFormatException e) {
    			return true;
		    }
	    }
	    return false;
    }

    /**
     * Guesses whether the specified {@code String} is a delimiter for the provided lines. This is done by taking a
     * subset of lines and counting the number of times the potential delimiter occurs in each line. If the number of
     * occurrences is the same for each line, then the {@code String} is likely a delimiter.
     *
     * @param   lines
     *          a String[] of lines containing data
     * @param   delim
     *          the potential String delimiter
     *
     * @return  true, if delim occurs the same number of times in each line
     */
    private static boolean isDelimiter(String[] lines, Delimiter delim) {
        final int NUM_LINES = 5;
        int numLines = Math.min(NUM_LINES, lines.length);

        int[] counts = new int[numLines];

        for (int i = 0; i < numLines; i++) {
            counts[i] = StringUtils.countOccurrencesOf(lines[i], delim.toString());
        }

        // If the number of occurrences of delim is not the same for each line, return false.
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] == 0 || (counts[i] != counts[i - 1]) ) {
                return false;
            }
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
    private static boolean isDouble(String string) {
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

    /**
     * Parses a {@code String[]} of lines given a delimiter
     *
     * @param   lines
     *          String[] of lines
     * @param   delim
     *          String delimiter
     *
     * @return  data in a Double[][]
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     */
    private static Double[][] parseData(String[] lines, String delim) throws IOException {

        // TODO Detect whether the copied data is viable.

        // Approximate the number of header rows by testing the first value of each row as a Double
        int numHeaderRows = 0;
        while ( ! isDouble(lines[numHeaderRows].substring(0, lines[numHeaderRows].indexOf(delim))) ) {
            numHeaderRows++;
        }

        if (numHeaderRows > 0) {
            // Remove header rows from lines.
            lines = Arrays.copyOfRange(lines, numHeaderRows, lines.length);
        }

        int maxRowSize = 0;
        for (String line : lines) {
            maxRowSize = Math.max(maxRowSize, line.split(delim).length);
        }

        Double[][] data = new Double[lines.length][maxRowSize];

        for (int i = 0; i < lines.length; i++) {
            String[] contentAsString = lines[i].split(delim, 0);

            // ignore lines that contains anything else than double values
            if (isDouble(contentAsString[0])) {

                for (int j = 0; j < maxRowSize; j++) {
                    if (j >= contentAsString.length) {
                        data[i][j] = 0.0;
                    } else {
                        data[i][j] = isDouble(contentAsString[j]) ? Double.parseDouble(contentAsString[j]) : Double.NaN;
                    }
                }
            }
        }

        return data;
    }

    /**
     * Gets the lines of a text file as an array of {@code String}s.
     *
     * @param   path
     *          the Path to the file to be read
     *
     * @return  array of lines as Strings
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     */
    private static String[] readLines(Path path) throws IOException {
        try ( UnicodeBOMInputStream uis = new UnicodeBOMInputStream(Files.newInputStream(path));
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
    private static String[] readLines(String content) {
        return content.split("[\\r\\n]+");
    }
}
