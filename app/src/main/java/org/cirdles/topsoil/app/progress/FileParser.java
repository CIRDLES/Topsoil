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

    public static String [] parseFile(File file)
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
            return parseTxt(file, ",");
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
    private static String [] parseCsv(File file) {

        ArrayList<String> content = new ArrayList<String>();

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

        // return results as an Array of Strings
        return content.toArray(new String[content.size()]);
    }

    /**
     * Parse TSV File
     * @param file TSV file to read data from
     * @return String array of values
     */
    private static String [] parseTsv(File file) {
        return null;
    }

    /**
     * Parse TXT File given a delimiter
     * @param file txt file to read
     * @param delimiter data delimiter
     * @return String array of values
     */
    private static String [] parseTxt(File file, String delimiter) {
        return null;
    }

}
