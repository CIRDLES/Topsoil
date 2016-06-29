package org.cirdles.topsoil.app.progress;

import java.io.File;

import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by benjaminmuldrow on 5/25/16.
 *
 * This is a utility file that is used for parsing data from files
 * into String Arrays which can be read as data for the TableView
 *
 */
public class FileParser {

    public static File openTableDialogue(Stage stage) {
        return TopsoilFileChooser
                .getTableFilechooser()
                .showOpenDialog(stage);
    }

    public static boolean containsHeaderDialogue() {
        boolean containsHeaders = false;
        YesNoAlert alert = new YesNoAlert(
                "Does the selection contain headers?");
        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent()) {
            if (response.get() == ButtonType.YES) {
                containsHeaders = true;
            } else { // 'NO' button or 'CANCEL'
                containsHeaders = false;
            }
        } // else containsHeaders is assumed false
        return containsHeaders;
    }

    public static List<TopsoilDataEntry> parseFile(File file, boolean containsHeaders) throws IOException {
        String extension = getExtension(file);
        if (extension.equals("csv")) {
            return parseCsv(file, containsHeaders);
        } else if (extension.equals("tsv")) {
            return parseTsv(file, containsHeaders);
        } else if (extension.equals("txt")) {
            return parseTxt(file, "\t", containsHeaders);
        } else {
            return null;
            // TODO throw error if invalid file extension
        }
    }

    private static String getExtension(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(
                fileName.lastIndexOf(".") + 1,
                fileName.length());
        return extension;
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
    private static List<TopsoilDataEntry> parseTxt(File file,
                                                   String delimiter,
                                                   boolean containsHeaders)
            throws IOException {

        List<TopsoilDataEntry> content = new ArrayList<TopsoilDataEntry>();
        String [] lines = readLines(file);

        // ignore header row if supplied
        if (containsHeaders) {
            String [] newlines = new String[lines.length - 1];
            for (int i = 1; i < lines.length; i++) {
                newlines[i - 1] = lines[i];
            }
            lines = newlines;
        }

        for (String line : lines) {
            String[] contentAsString = line.split(delimiter);
            if (contentAsString.length == 4) {              // No Corr Coef provided
                content.add(
                        new TopsoilDataEntry(
                                Double.parseDouble(contentAsString[0]),
                                Double.parseDouble(contentAsString[1]),
                                Double.parseDouble(contentAsString[2]),
                                Double.parseDouble(contentAsString[3])
                        )
                );
            } else if (contentAsString.length == 5) {       // Corr Coef Provided
                content.add(
                        new TopsoilDataEntry(
                                Double.parseDouble(contentAsString[0]),
                                Double.parseDouble(contentAsString[1]),
                                Double.parseDouble(contentAsString[2]),
                                Double.parseDouble(contentAsString[3]),
                                Double.parseDouble(contentAsString[4])
                        )
                );
            } else {
                // TODO throw exception for invalid file
                throw new IOException("invalid file");
            }
        }

        return content;
    }

    public static String [] parseHeaders(File file) {
        String [] content = readLines(file);
        String extension = getExtension(file);
        if (extension.equals("csv")) {
            return content[0].split(",");
        } else if (extension.equals("txt") || extension.equals("tsv")) {
            return  content[0].split("\t");
        } else {
            Alerter alerter = new ErrorAlerter();
            alerter.alert("Invalid File Type");
            return null;
        }
    }

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

}
