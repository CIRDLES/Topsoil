package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.util.file.parser.Delimiter;

/**
 * Supported file extensions for model files.
 *
 * @author  marottajb
 */
public enum TableFileExtension {

    CSV("csv", Delimiter.COMMA),
    TSV("tsv", Delimiter.TAB),
    TXT("txt", null);

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
}
