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

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns a {@code FileChooser} that chooses Topsoil projects for opening.
     *
     * @return instance of FileChooser with a .topsoil extension filter
     */
    public static FileChooser openTopsoilFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Topsoil Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Topsoil Project (.topsoil)", "*.topsoil"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        if (TopsoilSerializer.projectFileExists()) {

            File initialDir;
            if (TopsoilSerializer.getCurrentProjectFile().getParent() != null) {
                initialDir = new File(TopsoilSerializer.getCurrentProjectFile().getParent());
            } else {
                initialDir = new File(System.getProperty("user.home"));
            }
            fileChooser.setInitialDirectory(initialDir);
        }
        return fileChooser;
    }

    /**
     * Returns a {@code FileChooser} that chooses Topsoil projects for saving.
     *
     * @return instance of FileChooser with a .topsoil extension filter
     */
    public static FileChooser saveTopsoilFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Topsoil Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Topsoil Project (.topsoil)", "*.topsoil")
        );

        if (TopsoilSerializer.projectFileExists()) {

            File initialDir;
            if (TopsoilSerializer.getCurrentProjectFile().getParent() != null) {
                initialDir = new File(TopsoilSerializer.getCurrentProjectFile().getParent());
            } else {
                initialDir = new File(System.getProperty("user.home"));
            }
            fileChooser.setInitialDirectory(initialDir);
            fileChooser.setInitialFileName(TopsoilSerializer.getCurrentProjectFile().getName());
        }
        return fileChooser;
    }

    /**
     * Returns a {@code FileChooser} that chooses tab-delimited text {@code File}s for opening.
     *
     * @return  FileChooser with supported extension filters
     */
    public static FileChooser openTableFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Table File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Data Table Files (.csv, .tsv, .txt)", "*.tsv", "*.csv", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return fileChooser;
    }

    /**
     * Returns a {@code FileChooser} that chooses tab-delimited text {@code File}s for saving.
     *
     * @return  FileChooser with supported extension filters
     */
    public static FileChooser exportTableFile() {
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
