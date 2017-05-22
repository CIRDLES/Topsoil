package org.cirdles.topsoil.app.util.file;

import java.io.File;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.util.dialog.Alerter;
import org.cirdles.topsoil.app.util.dialog.ErrorAlerter;
import org.cirdles.topsoil.app.util.dialog.YesNoAlert;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import org.cirdles.topsoil.app.isotope.IsotopeType;

/**
 * A utility file used for parsing data from files into arrays of {@code String}s, which can then be put into a table.
 *
 * @author Benjamin Muldrow
 */
public class FileParser {

    //***********************
    // Attributes
    //***********************

    /**
     * A key for referencing commas in {@link #COMMON_DELIMITERS}.
     */
    private static final String COMMA = "Commas";

    /**
     * A key for referencing tabs in {@link #COMMON_DELIMITERS}.
     */
    private static final String TAB = "Tabs";

    /**
     * A key for referencing colons in {@link #COMMON_DELIMITERS}.
     */
    private static final String COLON = "Colons";

    /**
     * A key for referencing semicolons in {@link #COMMON_DELIMITERS}.
     */
    private static final String SEMICOLON = "Semicolons";

    /**
     * An array of supported file extensions.
     * <p>
     * Currently supported: .csv, .tsv, .txt
     */
    private static final String[] SUPPORTED_TABLE_FILE_EXTENSIONS = {"csv", "tsv", "txt"};

    /**
     * A {@code HashMap} populated with common delimiters.
     *
     * <p>This is referenced when trying to guess the delimiter of a
     * .txt file, or another form of input where the delimiter isn't clear.
     *
     * <p>Currently supported: commas, tabs, colons, semicolons
     *
     */
    private static final HashMap<String, String> COMMON_DELIMITERS; // Checked against when guessing a delimiter
    static {
        COMMON_DELIMITERS = new LinkedHashMap<>();
        COMMON_DELIMITERS.put(COMMA, ",");
        COMMON_DELIMITERS.put(TAB, "\t");
        COMMON_DELIMITERS.put(COLON, ":");
        COMMON_DELIMITERS.put(SEMICOLON, ";");
    }

    //***********************
    // Methods
    //***********************

    /**
     * Opens a {@code FileChooser} for opening table files (e.g. .csv, .txt). A {@code Stage} must be provided
     * to show ownership over the file chooser, so the user can't change focus to another window before
     * specifying a file.
     *
     * @param stage the parent Stage
     * @return  the selected File
     */
    public static File openTableDialog(Stage stage) {
        return TopsoilFileChooser
                .getTableFileChooser()
                .showOpenDialog(stage);
    }
    
    /**
     * Opens a {@code File} containing example data for a given isotopeType.
     *
     * @param isotopeType the isotope system to get a relevant set of data
     * @return  the ressource File located in project resources
     */
    public static File openExampleTable(IsotopeType isotopeType) {
        File file = null;
        Alerter alerter = new ErrorAlerter();

        String UPB_DATA_FILE_PATH;
        String UTH_DATA_FILE_PATH;
        String GEN_DATA_FILE_PATH;

        if (System.getProperty("os.name").lastIndexOf("Windows") != -1) {
            UPB_DATA_FILE_PATH = "app/src/main/resources/org/cirdles/topsoil/app/util/file/sampledata/UPb-Example-Data.csv";
            UTH_DATA_FILE_PATH = "app/src/main/resources/org/cirdles/topsoil/app/util/file/sampledata/UTh-Example-Data.csv";
            GEN_DATA_FILE_PATH = "app/src/main/resources/org/cirdles/topsoil/app/util/file/sampledata/Generic-Example-Data.csv";
        } else {
            UPB_DATA_FILE_PATH = "src/main/resources/org/cirdles/topsoil/app/util/file/sampledata/UPb-Example-Data.csv";
            UTH_DATA_FILE_PATH = "src/main/resources/org/cirdles/topsoil/app/util/file/sampledata/UTh-Example-Data.csv";
            GEN_DATA_FILE_PATH = "src/main/resources/org/cirdles/topsoil/app/util/file/sampledata/Generic-Example-Data.csv";

        }

        if(isotopeType.equals(IsotopeType.UPb)) {
            file = new File(UPB_DATA_FILE_PATH);
            if(file.exists()) {
                return file;
            } else {
                alerter.alert("UPb sample data table not found. Please check the resource directory.");
            }
        } else if(isotopeType.equals(IsotopeType.UTh)) {
            file = new File(UTH_DATA_FILE_PATH);
            if(file.exists()) {
                return file;
            } else {
                alerter.alert("UTh sample data table not found. Please check the resource directory.");
            }
        } else {
            file = new File(GEN_DATA_FILE_PATH);
            if (!(file.exists())) {
                alerter.alert("No sample data table found. Please check the resource directory.");
            }
        }
        return file;
    }

    /**
     * Opens a {@link YesNoAlert} asking the user whether or not a table file contains a header row(s).
     *
     * @return  true if user presses Yes, false if No
     */
    public static Boolean containsHeaderDialog() {
        Boolean containsHeaders = null;
        YesNoAlert alert = new YesNoAlert(
                "Does the selection contain headers?");
        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent()) {
            if (response.get() == ButtonType.YES) {
                containsHeaders = true;
            } else if (response.get() == ButtonType.NO) {
                containsHeaders = false;
            }
        } // else containsHeaders is assumed false
        return containsHeaders;
    }

    public static Boolean detectHeader(String content) {
        Boolean containsHeaders = true;

        String[] lines = readLines(content);

        String[] firstLine = lines[0].split(getDelimiter(content));

        if(isDouble(firstLine[0])){
            containsHeaders = false;
        }

        return containsHeaders;
    }

    public static Boolean detectHeader(File file) {
        Boolean containsHeaders = true;

        String[] lines = readLines(file);

        String[] firstLine = lines[0].split(getDelimiter(lines));

        if(isDouble(firstLine[0])){
            containsHeaders = false;
        }

        return containsHeaders;
    }

    /**
     * Determines whether the specified table {@code File} has a supported extension.
     *
     * @param file  the selected table File
     * @return  true, if the extension is supported
     */
    public static boolean isSupportedTableFile(File file) {
        boolean isSupported = false;
        String extension = getExtension(file);
        for (String supportedExtension : SUPPORTED_TABLE_FILE_EXTENSIONS) {
            if (extension.equals(supportedExtension)) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }

    /**
     * Parses data from a {@code File} into a {@code List} of {@code TopsoilDataEntry}s, which can then be put into a
     * table.
     *
     * @param file  the File containing table data
     * @param containsHeaders   true if file contains headers
     * @return  List of TopsoilDataEntry
     * @throws IOException  if File is of an incorrect format
     */
    public static List<TopsoilDataEntry> parseFile(File file, boolean containsHeaders) throws IOException {
        String extension = getExtension(file);
        if (extension.equals("csv")) {
            return parseCsv(file, containsHeaders);
        } else if (extension.equals("tsv")) {
            return parseTsv(file, containsHeaders);
        } else if (extension.equals("txt")) {
            return parseTxt(file, getDelimiter(readLines(file)), containsHeaders);
        } else {
            throw new IOException("Unsupported table file.");
        }
    }

    /**
     * Gets the extension of a specified {@code File}.
     *
     * @param file  the File in question
     * @return  a String representing the File's extension
     */
    private static String getExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(
                fileName.lastIndexOf(".") + 1,
                fileName.length());
    }

    /**
     * Parse data obtained as a {@code String} from the system {@code Clipboard}.
     *
     * @param containsHeaders   true if the data has headers
     * @param delim String delimiter
     * @return  data as a List of TopsoilDataEntries
     */
    public static List<TopsoilDataEntry> parseClipboard(boolean containsHeaders, String delim) {
        String content = Clipboard.getSystemClipboard().getString();

        if (delim == null) {
            return null;
        }

        return parseTxt(readLines(content), delim, containsHeaders);
    }

    /**
     * Parses a .csv {@code File}.
     *
     * @param file  .csv File to read data from
     * @param containsHeaders   true if the data has headers
     * @return  String array of values
     * @throws IOException  if file is invalid
     */
    private static List<TopsoilDataEntry> parseCsv(File file, boolean containsHeaders) throws IOException {
        return parseTxt(file, ",", containsHeaders);
    }

    /**
     * Parses a .tsv {@code File}.
     * @param file  .tsv File to read data from
     * @param containsHeaders   true if the data has headers
     * @return  String array of values
     * @throws IOException  if file is invalid
     */
    private static List<TopsoilDataEntry> parseTsv(File file, boolean containsHeaders) throws IOException {
        return parseTxt(file, "\t", containsHeaders);
    }

    /**
     * Parses a .txt {@code File} using the provided delimiter.
     * @param file  .txt File to read
     * @param delimiter data delimiter
     * @param containsHeaders   true if the data has headers
     * @return  String array of values
     * @throws IOException  if file is invalid
     */
    private static List<TopsoilDataEntry> parseTxt(File file, String delimiter, boolean containsHeaders) throws IOException {
        String[] lines = readLines(file);
        return parseTxt(lines, delimiter, containsHeaders);
    }

    /**
     * Parses a {@code String[]} of lines given a delimiter
     *
     * @param lines String[] of lines
     * @param delimiter delimiter
     * @param containsHeaders   true if headers are present
     * @return  data as a List of TopsoilDataEntries
     */
    private static List<TopsoilDataEntry> parseTxt(String[] lines, String delimiter, boolean containsHeaders) {

        Alerter alerter = new ErrorAlerter();

        // TODO Detect whether the copied data is viable.

        List<TopsoilDataEntry> content = new ArrayList<>();

        // ignore header row if supplied
        if (containsHeaders) {
            String[] newlines = new String[lines.length - 1];
            for (int i = 1; i < lines.length; i++) {
                newlines[i - 1] = lines[i];
            }
            lines = newlines;
        }

        for (String line : lines) {
            String[] contentAsString = line.split(delimiter, -1);

            // ignore lines that contains anything else than double values
            if (isDouble(contentAsString[0])) {
                TopsoilDataEntry entry = new TopsoilDataEntry();
                // TODO Allow more or less than five columns
                for (int i = 0; i < contentAsString.length && i < 5; i++) {
                    entry.getProperties().add(isDouble(contentAsString[i]) ? new SimpleDoubleProperty(Double.parseDouble(contentAsString[i])) : new SimpleDoubleProperty(Double.NaN));
                }
                content.add(entry);
                // TODO throw exception for invalid file
//                throw new IOException("invalid file");
//            }
            }

        }

        return content;
    }

    /**
     * Code taken from the documentation for {@code Double.valueOf(String s)}. Checks that a given {@code Stirng} can be
     * parsed into a {@code Double}.
     *
     * @param string    the String to check
     * @return          true if the String can be parsed into a Double
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
                 "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                 // . Digits ExponentPart_opt FloatTypeSuffix_opt
                 "(\\.("+Digits+")("+Exp+")?)|"+

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
     * Reads the header row(s) of a {@code File}.
     *
     * @param file  File to be read
     * @return  array of headers as Strings
     */
    public static String[] parseHeaders(File file) {
        String[] content = readLines(file);
        String extension = getExtension(file);
        if (extension.equals("csv")) {

            // Check if the second line of file also has headers, and return a concatenation of these if present
            String[] secondLine = content[1].split(",");
            if(!(isDouble(secondLine[0]))) {
                String[] firstLine = content[0].split(",");
                for(int i = 0; i < firstLine.length ; i++) {
                    firstLine[i] = firstLine[i].concat("\t"+secondLine[i]);
                }
                return firstLine;
            }

            return content[0].split(",");

        } else if (extension.equals("txt") || extension.equals("tsv")) {

            // Check if the second line of file also has headers, and return a concatenation of these if present
            String[] secondLine = content[1].split("\t");
            if(!(isDouble(secondLine[0]))) {
                String[] firstLine = content[0].split("\t");
                for(int i = 0; i < firstLine.length ; i++) {
                    firstLine[i] = firstLine[i].concat("\t"+secondLine[i]);
                }
                return firstLine;
            }

            return  content[0].split("\t");

        } else {
            return null;
        }
    }

    /**
     * Reads the header row(s) of {@code String} content.
     *
     * @param content   String content
     * @param delim a String delimiter
     * @return  String[] of data headers
     */
    public static String[] parseHeaders(String content, String delim) {
        String[] lines = readLines(content);
        String[] rtnval = null;

        if (delim == null) {
            Alerter alerter = new ErrorAlerter();
            alerter.alert("Could not read input.");
        } else {

            // Check if the second line of content also has headers, and return a concatenation of these if present
            String[] secondLine = lines[1].split(delim);
            if(!(isDouble(secondLine[0]))) {
                String[] firstLine = lines[0].split(delim);
                for(int i = 0; i < firstLine.length ; i++) {
                    firstLine[i] = firstLine[i].concat("\t"+secondLine[i]);
                }
                return firstLine;
            }

            rtnval = lines[0].split(delim);
        }

        return rtnval;
    }

    /**
     * Tries to automatically guess the delimiter of a {@code String[]} of delimited data from a set of
     * common delimiters (see {@link FileParser#COMMON_DELIMITERS}). Returns {@code null} if unable to determine a
     * delimiter.
     *
     * @param lines a String[] of delimited data lines
     * @return  the identified String delimiter
     */
    private static String getDelimiter(String[] lines) {
        final int NUM_LINES = 5;
        String rtnval = null;

        // A pattern can't be established with only one line; ask the user if they know the delimiter.
        if (lines.length > 1) {
            if (lines.length > NUM_LINES) {
                lines = Arrays.copyOfRange(lines, 0, NUM_LINES);
            }

            for (String delim : COMMON_DELIMITERS.values()) {
                if (isDelimiter(lines, delim)) {
                    rtnval = delim;
                }
            }
        }

        if (rtnval == null) {
            rtnval = requestDelimiter();
        }

        return rtnval;
    }

    /**
     * Tries to automatically guess the delimiter of a {@code String} of delimited data from a set of common delimiters
     * (see {@link FileParser#COMMON_DELIMITERS}). Returns {@code null} if unable to determine a delimiter.
     *
     * @param content a String of delimited data
     * @return  the identified String delimiter
     */
    public static String getDelimiter(String content) {
        return getDelimiter(readLines(content));
    }

    /**
     * Displays a {@code Dialog} requesting the delimiter for the data from the user.
     *
     * @return  the user-specified delimiter
     */
    private static String requestDelimiter() {
        String otherDelimiterOption = "Other";
        String unknownDelimiterOption = "Unknown";
        String noDelimiterMessage = "Topsoil is unable to read the data as-is. Make sure the data is complete, or try" +
                                    " putting the data into a .csv or .tsv file.";

        Dialog<String> delimiterRequestDialog = new Dialog<>();
        delimiterRequestDialog.setTitle("Delimiter Request");

        /*
            CONTENT NODES
         */
        VBox vBox = new VBox(10.0);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_RIGHT);

        Label requestLabel = new Label("How are the values in your data separated?");
        ChoiceBox<String> delimiterChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(COMMON_DELIMITERS
                                                                                                         .keySet()));
        delimiterChoiceBox.getItems().addAll(otherDelimiterOption, unknownDelimiterOption);

        Label otherLabel = new Label("Other: ");
        TextField otherTextField = new TextField();
        otherTextField.setDisable(true);

        delimiterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(otherDelimiterOption)) {
                otherTextField.setDisable(false);
            } else {
                otherTextField.setDisable(true);
            }
        });

        grid.add(requestLabel, 0, 0);
        grid.add(delimiterChoiceBox, 1, 0);
        grid.add(otherLabel, 0, 1);
        grid.add(otherTextField, 1, 1);

        Label adviceLabel = new Label("*(If copying from spreadsheet, select \"Tabs\".)");
        adviceLabel.setTextFill(Color.DARKRED);

        vBox.getChildren().addAll(grid, adviceLabel);

        delimiterRequestDialog.getDialogPane().setContent(vBox);

        /*
            BUTTONS AND RETURN
         */
        delimiterRequestDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        delimiterRequestDialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        delimiterChoiceBox.getSelectionModel().selectedItemProperty().addListener(c -> {
            if (delimiterChoiceBox.getSelectionModel().getSelectedItem() == null) {
                delimiterRequestDialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            } else {
                delimiterRequestDialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
            }
        });

        delimiterRequestDialog.setResultConverter(value -> {
            if (value == ButtonType.OK) {
                if (delimiterChoiceBox.getSelectionModel().getSelectedItem().equals(otherDelimiterOption)) {
                    return otherTextField.getText().trim();
                } else if (delimiterChoiceBox.getSelectionModel().getSelectedItem().equals(unknownDelimiterOption)) {
                    Alert noDelimiterAlert = new Alert(Alert.AlertType.INFORMATION, noDelimiterMessage);
                    noDelimiterAlert.show();
                    return null;
                } else {
                    return COMMON_DELIMITERS.get(delimiterChoiceBox.getValue());
                }
            } else {
                return null;
            }
        });

        Optional<String> result = delimiterRequestDialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Guesses whether the specified {@code String} is a delimiter for the provided lines. This is done by taking a
     * subset of lines and counting the number of times the potential delimiter occurs in each line. If the number of
     * occurrences is the same for each line, then the {@code String} is likely a delimiter.
     *
     * @param lines a String[] of lines containing data
     * @param delim the potential String delimiter
     * @return  true, if delim occurs the same number of times in each line
     */
    private static boolean isDelimiter(String[] lines, String delim) {
        boolean result = true;
        int NUM_LINES = 5;
        int delimCount;
        int[] counts = new int[lines.length];

        // Takes a maximum of five lines
        if (lines.length > NUM_LINES) {
            lines = Arrays.copyOfRange(lines, 0, NUM_LINES);
        }

        // For each line
        for (int lineNumber = 0; lineNumber < lines.length; lineNumber++) {

            delimCount = 0;

            // Count the number of occurrences of delim
            for (int charPos = 0; charPos < lines[lineNumber].length(); charPos++) {
                if (String.valueOf(lines[lineNumber].charAt(charPos)).equals(delim)) {
                    delimCount++;
                }
            }

            counts[lineNumber] = delimCount;
        }

        // If the number of occurrences of delim is not the same for each line, return false.
        for (int lineNumber = 1; lineNumber < counts.length; lineNumber++) {
            if (counts[lineNumber] == 0 || counts[lineNumber] != counts[lineNumber - 1]) {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * Gets the lines of a text {@code File} as an array of {@code String}s.
     * @param file File to be read
     * @return array of lines as Strings
     */
    private static String[] readLines(File file) {

        String[] lines;
        ArrayList<String> content = new ArrayList<>();

        try {

            // Create relevant file readers
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Parse file content to arrayList
            String line = reader.readLine();
            while (line != null) {
                content.add(line);
                line = reader.readLine();
            }

            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toArray(new String[content.size()]);

    }

    /**
     * Gets the lines of a {@code String} as an array of {@code String}s.
     *
     * @param content   String to be read
     * @return String[] of lines
     */
    private static String[] readLines(String content) {
        return content.split("[\\r\\n]+");
    }

}