package org.cirdles.topsoil.app.file;

import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopsoilFileUtils {

    private TopsoilFileUtils() {
        // Prevents instantiation by default constructor
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

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
    public static String[] readLines(Path path) throws IOException {
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
    public static String[] readLines(String content) {
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
    public static String[][] readCells(String[] lines, String delimiter) {
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

    public static void writeLines(Path path, String[] lines) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(path.toFile());
        OutputStreamWriter out = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8);

        for (String line : lines) {
            out.write(line + System.lineSeparator());
        }

        out.close();
        fileOut.close();
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
    public static Delimiter guessDelimiter(Path path) throws IOException {
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
    public static Delimiter guessDelimiter(String content) {
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
    public static Delimiter guessDelimiter(String[] lines) {
        for (Delimiter delim : Delimiter.values()) {
            if (isDelimiter(lines, delim)) {
                return delim;
            }
        }
        return null;
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
        return getExtension(path) != null;
    }

    public static TableFileExtension getExtension(Path path) {
        String ext = path.toString().substring(path.toString().lastIndexOf(".") + 1).toUpperCase();
        try {
            return TableFileExtension.valueOf(ext.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
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
            counts[i] = StringUtils.countOccurrencesOf(lines[i], delim.asString());
        }

        // If the number of occurrences of delimiter is not the same for each line, return false.;
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] == 0 || (counts[i] != counts[i - 1]) ) {
                return false;
            }
        }
        return true;
    }

}
