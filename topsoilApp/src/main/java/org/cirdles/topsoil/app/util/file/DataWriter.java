package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.model.DataTable;

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
        // TODO
        return completed;
    }

    public enum FileType {

        CSV("csv"),
        TSV("tsv"),
        TXT("txt");

        private String extension;

        FileType(String ext) {
            this.extension = ext;
        }

        public String getExtension() {
            return extension;
        }

    }

}
