package org.cirdles.topsoil.app.util.file;

import javafx.stage.FileChooser;
import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;

import java.io.File;

/**
 * A class containing a set of methods for obtaining custom {@link FileChooser}s for
 * table files or .topsoil project files.
 *
 * @author Benjamin Muldrow
 */
public class TopsoilFileChooser {

    //***********************
    // Methods
    //***********************

    /**
     * Returns a {@code FileChooser} that opens Topsoil projects.
     *
     * @return instance of FileChooser with a .topsoil ExtensionFilter
     */
    public static FileChooser getTopsoilOpenFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Topsoil Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Topsoil Project (.topsoil)", "*.topsoil"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        if (TopsoilSerializer.projectFileExists()) {
            try {
                fileChooser.setInitialDirectory(new File(TopsoilSerializer.getCurrentProjectFile().getParent()));
            } catch (NullPointerException e) {
                // FileChooser opens to default directory
            }
        }
        return fileChooser;
    }

    /**
     * Returns a {@code FileChooser} that saves Topsoil projects
     *
     * @return instance of FileChooser with a .topsoil ExtensionFilter
     */
    public static FileChooser getTopsoilSaveFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Topsoil Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Topsoil Project (.topsoil)", "*.topsoil")
        );

        if (TopsoilSerializer.projectFileExists()) {
            try {
                fileChooser.setInitialDirectory(new File(TopsoilSerializer.getCurrentProjectFile().getParent()));
            } catch (NullPointerException e) {
                // FileChooser opens to default directory
            }
        }
        return fileChooser;
    }

    /**
     * Returns a {@code FileChooser} that opens tab-delimited text files.
     *
     * @return instance of FileChooser with .tsv/.csv/.txt ExtensionFilters
     */
    public static FileChooser getTableFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Table File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Data Table Files (.csv, .tsv, .txt)", "*.tsv", "*.csv", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return fileChooser;
    }

    public static FileChooser getExportTableFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Table File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("csv File (.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("tsv File (.tsv)", "*.tsv"),
                new FileChooser.ExtensionFilter("txt File (.txt)", "*.txt")
        );
        return fileChooser;
    }

}
