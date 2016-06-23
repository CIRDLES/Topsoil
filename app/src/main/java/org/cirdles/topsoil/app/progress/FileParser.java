package org.cirdles.topsoil.app.progress;

import java.io.File;

import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    public static List<UPbDataEntry> parseFile(File file) throws IOException
    {
        String fileName = file.getName();
        String extension = fileName.substring(
                fileName.lastIndexOf(".") + 1,
                fileName.length());
        if (extension.equals("csv")) {
            return parseCsv(file);
        } else if (extension.equals("tsv")) {
            return parseTsv(file);
        } else if (extension.equals("txt")) {
            return parseTxt(file, "\t");
        } else {
            return null;
            // TODO throw error if invalid file extension
        }
    }

    /**
     * Parse CSV File
     * @param file CSV file to read data from
     * @return String array of values
     */
    private static List<UPbDataEntry> parseCsv(File file) throws IOException {

        return parseTxt(file, ",");

    }

    /**
     * Parse TSV File
     * @param file TSV file to read data from
     * @return String array of values
     */
    private static List<UPbDataEntry> parseTsv(File file) throws IOException {

        return parseTxt(file, "\t");

    }

    /**
     * Parse TXT File given a delimiter
     * @param file txt file to read
     * @param delimiter data delimiter
     * @return String array of values
     */
    private static List<UPbDataEntry> parseTxt(File file, String delimiter) throws IOException {

        List<UPbDataEntry> content = new ArrayList<UPbDataEntry>();
        String [] lines = readLines(file);

        for (String line : lines) {
            String[] contentAsString = line.split(delimiter);
            if (contentAsString.length == 4) {              // No Corr Coef provided
                content.add(
                        new UPbDataEntry(
                                Double.parseDouble(contentAsString[0]),
                                Double.parseDouble(contentAsString[1]),
                                Double.parseDouble(contentAsString[2]),
                                Double.parseDouble(contentAsString[3])
                        )
                );
            } else if (contentAsString.length == 5) {       // Corr Coef Provided
                content.add(
                        new UPbDataEntry(
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

    private static String [] readLines(File file) {

        String [] lines;
        ArrayList<String> content = new ArrayList<>();

        try {

            // Create relevant file readers
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
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
