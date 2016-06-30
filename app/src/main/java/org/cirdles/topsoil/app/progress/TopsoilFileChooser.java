package org.cirdles.topsoil.app.progress;

import javafx.stage.FileChooser;

/**
 * Created by benjaminmuldrow on 6/17/16.
 */
public class TopsoilFileChooser {

    /**
     * Open a file choosing dialogue that searches for Topsoil projects
     * @return instance of FileChooser that looks for .topsoil projects
     */
    public static FileChooser getTopsoilFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Topsoil Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Topsoil Projects", "*.topsoil"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return fileChooser;
    }

    /**
     * Open a file choosing dialogue that searches for tables
     * @return instance of FileChooser that looks for .tsv/.csv/.txt files
     */
    public static FileChooser getTableFilechooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Table File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Table Files", "*.tsv", "*.csv", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return fileChooser;
    }

}
