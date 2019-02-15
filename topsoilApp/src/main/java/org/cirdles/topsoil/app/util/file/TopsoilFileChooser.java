package org.cirdles.topsoil.app.util.file;

import javafx.stage.FileChooser;
import org.cirdles.topsoil.app.util.serialization.ProjectSerializer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class containing a setValue of methods for obtaining custom {@link FileChooser}s for
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

        Path path = ProjectSerializer.getCurrentProjectPath();
        if (path != null) {

            if (path.getParent() == null) {
                path = Paths.get(System.getProperty("user.home"));
            }
            fileChooser.setInitialDirectory(path.toFile());
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

        Path path = ProjectSerializer.getCurrentProjectPath();
        if (path != null) {

            if (path.getParent() == null) {
                path = Paths.get(System.getProperty("user.home"));
            }
            fileChooser.setInitialDirectory(path.toFile());
            fileChooser.setInitialFileName(path.getFileName().toString());
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
