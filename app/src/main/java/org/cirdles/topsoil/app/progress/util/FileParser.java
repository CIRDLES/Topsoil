package org.cirdles.topsoil.app.progress.util;

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
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;
import org.cirdles.topsoil.app.util.YesNoAlert;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;

/**
 * Created by benjaminmuldrow on 5/25/16.
 *
 * This is a utility file that is used for parsing data from files
 * into String Arrays which can be read as data for the TableView
 *
 */
public class FileParser {

    private static final String HEADERS_KEY = "Headers";
    private static final String DATA_KEY = "Data";
    private static final String[] SUPPORTED_TABLE_FILE_EXTENSIONS = {"csv", "tsv", "txt"};
    private static final HashMap<String, String> COMMON_DELIMITERS; // Checked against when guessing a delimiter
    static {
        COMMON_DELIMITERS = new HashMap<>();
        COMMON_DELIMITERS.put("Commas", ",");
        COMMON_DELIMITERS.put("Tabs", "\t");
        COMMON_DELIMITERS.put("Colons", ":");
        COMMON_DELIMITERS.put("Semicolons", ";");
    }

    public static File openTableDialogue(Stage stage) {
        return TopsoilFileChooser
                .getTableFileChooser()
                .showOpenDialog(stage);
    }
    
    public static File openExampleTable(IsotopeType isotopeType) {
        if(isotopeType.equals(IsotopeType.UPb)) {
            return new File("src/main/resources/org/cirdles/topsoil/app/sampledata/UPb-Example-Data.csv");
        }
        else if(isotopeType.equals(IsotopeType.UTh)) {
            return new File("src/main/resources/org/cirdles/topsoil/app/sampledata/UTh-Example-Data.csv");
        }
        return new File("src/main/resources/org/cirdles/topsoil/app/sampledata/Generic-Example-Data.csv");
    }

    public static Boolean containsHeaderDialogue() {
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
     * Determines whether the specified table file has a supported file extension.
     *
     * @param file  the selected table File
     * @return  true, if the file extension is supported
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

    private static String getExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(
                fileName.lastIndexOf(".") + 1,
                fileName.length());
    }

    /**
     * Parse data contained as a String from the system Clipboard.
     *
     * @param containsHeaders   whether or not the data has headers
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
     * Parse CSV File
     * @param file CSV file to read data from
     * @return String array of values
     */
    private static List<TopsoilDataEntry> parseCsv(File file, boolean containsHeaders) throws IOException {

        return parseTxt(file, ",", containsHeaders);

    }

    /**
     * Parse TSV File
     * @param file TSV file to read data from
     * @return String array of values
     */
    private static List<TopsoilDataEntry> parseTsv(File file, boolean containsHeaders) throws IOException {

        return parseTxt(file, "\t", containsHeaders);

    }

    /**
     * Parse TXT File given a delimiter
     * @param file txt file to read
     * @param delimiter data delimiter
     * @return String array of values
     */
    private static List<TopsoilDataEntry> parseTxt(File file, String delimiter, boolean containsHeaders) throws IOException {
        String [] lines = readLines(file);
        return parseTxt(lines, delimiter, containsHeaders);
    }

    /**
     * Parse String[] of lines given a delimiter
     *
     * @param lines String[] of lines
     * @param delimiter delimiter
     * @param containsHeaders   boolean; whether or not headers are present
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
     * Code taken from the documentation for Double.valueOF(String s). Checks that a given <tt>String</tt> can be
     * parsed into a <tt>Double</tt>.
     *
     * @param string    the String to check
     * @return          true, if the String can be parsed into a Double
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
     * Read the supplied headers of a file
     * @param file file to be read
     * @return array of headers as Strings
     */
    public static String [] parseHeaders(File file) {
        String [] content = readLines(file);
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
            Alerter alerter = new ErrorAlerter();
            alerter.alert("Invalid File Type");
            return null;
        }
    }

    /**
     * Read the supplied data headers of String content.
     *
     * @param content   String content
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
     * Tries to automatically guess the delimiter of a String[] of delimited data from a set of common delimiters (see
     * COMMON_DELIMITERS). Returns null if unable to determine a delimiter.
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
     * Tries to automatically guess the delimiter of a String of delimited data from a set of common delimiters (see
     * COMMON_DELIMITERS). Returns null if unable to determine a delimiter.
     *
     * @param content a String of delimited data
     * @return  the identified String delimiter
     */
    public static String getDelimiter(String content) {
        return getDelimiter(readLines(content));
    }

    /**
     * Displays a Dialog requesting the delimiter for the data from the user.
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
     * Guesses whether the specified char is a delimiter for the provided lines. This is done by taking a subset of
     * lines, and counting the number of times the potential delimiter occurs in each line. If the number of
     * occurrences is the same for each line, then the char is likely a delimiter.
     *
     * @param lines a String[] of lines containing data
     * @param delim the potential char delimiter
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
     * Get file as an array of lines
     * @param file to be read
     * @return array of lines as Strings
     */
    private static String [] readLines(File file) {

        String [] lines;
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
     * Get String content as an array of lines
     *
     * @param content   String to be read
     * @return String[] of lines
     */
    private static String[] readLines(String content) {
        return content.split("[\\r\\n]+");
    }

}
