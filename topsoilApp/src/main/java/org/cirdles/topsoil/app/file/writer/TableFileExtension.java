package org.cirdles.topsoil.app.file.writer;

import org.cirdles.topsoil.app.file.parser.Delimiter;

import java.nio.file.Path;

/**
 * Supported file extensions for model files.
 *
 * @author  marottajb
 */
public enum TableFileExtension {

    CSV("csv", Delimiter.COMMA),
    TSV("tsv", Delimiter.TAB),
    TXT("txt", Delimiter.COMMA);

    private String extension;
    private Delimiter delim;

    TableFileExtension(String ext, Delimiter delim) {
        this.extension = ext;
        this.delim = delim;
    }

    @Override
    public String toString() {
        return extension;
    }

    public String getExtension() {
        return extension;
    }

    public Delimiter getDelimiter() {
        return delim;
    }

    public static TableFileExtension getExtensionFromPath(Path path) {
        for (TableFileExtension ext : TableFileExtension.values()) {
            if (path.toString().endsWith("." + ext.extension)) {
                return ext;
            }
        }
        return null;
    }
}
