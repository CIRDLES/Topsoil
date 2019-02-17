package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.DataTable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * @author marottajb
 */
public class DataWriter {

    public static final String CSV_EXT = "csv";
    public static final String TSV_EXT = "tsv";
    public static final String TXT_EXT = "txt";

    public static boolean writeTableToFile(DataTable table, Path path) {
        boolean completed = false;

        try (OutputStreamWriter out =
                     new OutputStreamWriter(new FileOutputStream(path.toFile()), StandardCharsets.UTF_8)) {
            // TODO
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return completed;
    }

}
