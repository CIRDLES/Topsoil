package org.cirdles.topsoil.app.progress.util;

import javafx.stage.FileChooser;
import org.cirdles.topsoil.app.progress.util.serialization.TopsoilSerializer;

import java.io.File;

/**
 * A class containing a set of methods for obtaining custom file choosers for
 * table files or .topsoil project files.
 *
 * @author benjaminmuldrow
 */
public class TopsoilFileChooser {

    /**
     * Open a file choosing dialog that opens Topsoil projects
     *
     * @return instance of FileChooser that looks for .topsoil projects
     */
    public static FileChooser getTopsoilOpenFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Topsoil Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Topsoil Project (.topsoil)", "*.topsoil"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        if (TopsoilSerializer.projectFileExists() && TopsoilSerializer
                .getCurrentProjectFile().getParent() != null) {
            fileChooser.setInitialDirectory(new File(TopsoilSerializer
                    .getCurrentProjectFile().getParent()));
        }
        return fileChooser;
    }

    /**
     * Open a file choosing dialog that saves Topsoil projects
     *
     * @return instance of FileChooser that looks for .topsoil projects
     */
    public static FileChooser getTopsoilSaveFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Topsoil Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Topsoil Project (.topsoil)", "*.topsoil")
        );
        if (TopsoilSerializer.projectFileExists() && TopsoilSerializer
                .getCurrentProjectFile().getParent() != null) {
            fileChooser.setInitialDirectory(new File(TopsoilSerializer
                    .getCurrentProjectFile().getParent()));
        }
        return fileChooser;
    }

    /**
     * Open a file choosing dialogue that searches for tables
     *
     * @return instance of FileChooser that looks for .tsv/.csv/.txt files
     */
    public static FileChooser getTableFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Table File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Table Files", "*.tsv", "*.csv", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return fileChooser;
    }

}
