package org.cirdles.topsoil.app.util.dialog.controller;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.util.TopsoilException;
import org.cirdles.topsoil.app.util.dialog.DelimiterRequestDialog;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.file.FileParser;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Controller for a window that allows the user to specify whether they want to import existing files into Topsoil,
 * and which files they want to import.
 *
 * @author Jake Marotta
 */
public class ProjectSourcesController {

    private ToggleGroup toggle;
    @FXML private RadioButton emptyProjectButton;
    @FXML private RadioButton fromFilesButton;

    @FXML private Button addFilesButton;
    @FXML private Button removeFileButton;

    @FXML private ListView<Label> sourceFileListView;

    @FXML private Button cancelButton;
    @FXML private Button nextButton;

    /**
     * The previous scene in the "New Project" sequence. As of typing, this should be a Scene containing a
     * {@link ProjectSourcesController}.
     */
    private Scene previousScene;

    /**
     * The next scene in the "New Project" sequence. As of typing, this should be a Scene containing a
     * {@link ProjectPreviewController}.
     */
    private Scene nextScene;

    private Boolean didFinish;

    private BiMap<Path, Label> pathLabelBiMap;

    /**
     * A {@code ListProperty} containing a list of type {@code PathDelimiterPair}, which keeps track of the
     * {@code Path}s of source files as well as the appropriate {@code String} delimiter for each of them.
     */
    private ListProperty<PathDelimiterPair> pathDelimiterList;
    public ListProperty<PathDelimiterPair> pathDelimiterListProperty() {
        if (pathDelimiterList == null) {
            pathDelimiterList = new SimpleListProperty<>(FXCollections.observableArrayList());
        }
        return pathDelimiterList;
    }

    @FXML
    public void initialize() {
        didFinish = false;
        pathLabelBiMap = HashBiMap.create();

        toggle = new ToggleGroup();
        toggle.getToggles().addAll(emptyProjectButton, fromFilesButton);

        // Disable file selection controls
        addFilesButton.setDisable(true);
        removeFileButton.setDisable(true);
        sourceFileListView.setDisable(true);

        nextButton.setDisable(true);
        toggle.selectedToggleProperty().addListener(c -> {
            if (toggle.getSelectedToggle() != null) {
                if (toggle.getSelectedToggle() == emptyProjectButton) {
                    nextButton.setText("Finish");
                    disableFileSelection();

                } else if (toggle.getSelectedToggle() == fromFilesButton) {
                    nextButton.setText("Next");
                    enableFileSelection();
                }
            } else {
                disableFileSelection();
            }
            updateNextButtonDisabledProperty();
        });

        sourceFileListView.getSelectionModel().selectedItemProperty().addListener(c -> {
            if (sourceFileListView.getSelectionModel().getSelectedItem() == null) {
                removeFileButton.setDisable(true);
            } else {
                removeFileButton.setDisable(false);
            }
        });

    }

    /**
     * Upon pressing "Add Files...", the user is presented with a {@code FileChooser} where they can select multiple
     * table files to import into their new project.
     */
    @FXML private void addFilesButtonAction() {
        List<File> files = TopsoilFileChooser.getTableFileChooser().showOpenMultipleDialog(MainWindow.getPrimaryStage());

        if (files != null) {

            List<File> selectedFiles = new ArrayList<>(files);
            List<File> rejectedFiles = new ArrayList<>();

            if (!selectedFiles.isEmpty()) {

                // Check all of the files for compatibility.
                Iterator<File> iterator = selectedFiles.iterator();
                while (iterator.hasNext()) {
                    File file = iterator.next();
                    String delim = FileParser.getDelimiter(file);

                    if (delim == null) {
                        delim = DelimiterRequestDialog.showDialog(
                                "Delimiter Request",
                                file.getName() + ": Please select the separator for this file.",
                                false
                        );
                    }

                    if (!FileParser.isSupportedTableFile(file) || FileParser.isEmptyFile(file) || delim == null) {
                        iterator.remove();
                        rejectedFiles.add(file);
                    } else {
                        try {
                            if (FileParser.parseFile(file, delim, false).size() <= 0) {
                                throw new TopsoilException("Invalid delimiter.");
                            }
                            if (pathLabelBiMap.containsKey(file.toPath())) {
                                iterator.remove();
                            } else {
                                Label pathLabel = new Label(file.getName());
                                sourceFileListView.getItems().add(pathLabel);
                                pathLabelBiMap.put(file.toPath(), pathLabel);
                                pathDelimiterListProperty().add(new PathDelimiterPair(file.toPath(), delim));
                            }
                        } catch (TopsoilException e) {
                            iterator.remove();
                            rejectedFiles.add(file);
                        }
                    }
                }

                if (rejectedFiles.size() > 0) {
                    StringBuilder badFileNames = new StringBuilder("");
                    for (File file : rejectedFiles) {
                        badFileNames.append(file.getName());
                        badFileNames.append("\n");
                    }

                    TopsoilNotification.showNotification(
                            TopsoilNotification.NotificationType.ERROR,
                            "Incompatible Files",
                            "Topsoil could not read the following files: \n\n" + badFileNames.toString()
                    );
                }
                updateNextButtonDisabledProperty();
            }
        }
    }

    /**
     * Upon pressing "Remove File", the currently selected item in {@link #sourceFileListView} is removed.
     */
    @FXML private void removeFileButtonAction() {
        // Remove from paths
        for (PathDelimiterPair pair : pathDelimiterList) {
            if (pair.getPath().equals(pathLabelBiMap.inverse().get(sourceFileListView.getSelectionModel().getSelectedItem()))) {
                pathDelimiterListProperty().remove(pair);
                break;
            }
        }

        // Remove from map
        pathLabelBiMap.inverse().remove(sourceFileListView.getSelectionModel().getSelectedItem());

        // Remove from ListView
        sourceFileListView.getItems().remove(sourceFileListView.getSelectionModel().getSelectedItem());

        updateNextButtonDisabledProperty();
    }

    /**
     * Upon pressing "Cancel", the {@code Stage} is closed without doing anything.
     */
    @FXML private void cancelButtonAction() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    /**
     * Sets the {@code Stage} to display the previous {@code Scene} in the "New Project" sequence.
     */
    @FXML private void previousButtonAction() {
        if (previousScene != null) {
            ((Stage) nextButton.getScene().getWindow()).setScene(previousScene);
        } else {
            ((Stage) nextButton.getScene().getWindow()).close();
        }
    }

    /**
     * Sets the {@code Stage} to display the next {@code Scene} in the "New Project" sequence.
     */
    @FXML private void nextButtonAction() {
        if (toggle.getSelectedToggle() == fromFilesButton && nextScene != null) {
            ((Stage) nextButton.getScene().getWindow()).setScene(nextScene);
        } else {
            didFinish = true;
            ((Stage) nextButton.getScene().getWindow()).close();
        }
    }

    /**
     * Updates the {@link Button#disableProperty()} of {@link #nextButton} based on whether all required information
     * is present.
     */
    private void updateNextButtonDisabledProperty() {
        if (toggle.getSelectedToggle() == null) {
            nextButton.setDisable(true);
        } else if (toggle.getSelectedToggle() == fromFilesButton && sourceFileListView.getItems().isEmpty()) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
    }

    /**
     * Returns true if the {@code Stage} was closed on this {@code Scene}.
     *
     * @return  true if Stage was closed here
     */
    public Boolean didFinish() {
        return didFinish;
    }

    /**
     * Enables controls that allow the user to select source files.
     */
    private void enableFileSelection() {
        addFilesButton.setDisable(false);
        if (sourceFileListView.getItems().size() > 0) {
            removeFileButton.setDisable(false);
        }
        sourceFileListView.setDisable(false);
    }

    /**
     * Disables controls that allow the user to select source files.
     */
    private void disableFileSelection() {
        addFilesButton.setDisable(true);
        removeFileButton.setDisable(true);
        sourceFileListView.setDisable(true);
    }

    /**
     * Sets the previous {@code Scene} in the "New Project" sequence. This scene will replace the current one when the
     * user clicks the "Previous" button.
     *
     * @param scene previous Scene
     */
    public void setPreviousScene(Scene scene) {
        previousScene = scene;
    }

    /**
     * Sets the next {@code Scene} in the "New Project" sequence. This scene will replace the current one when the
     * user clicks the "Next" button.
     *
     * @param scene next Scene
     */
    public void setNextScene(Scene scene) {
        nextScene = scene;
    }

    /**
     * A class that keeps track of the {@code Path} of a source files as well as the appropriate {@code String}
     * delimiter for parsing it.
     */
    static class PathDelimiterPair {

        private Path path;
        private String delim;

        private PathDelimiterPair(Path path, String delim) {
            this.path = path;
            this.delim = delim;
        }

        /**
         * Returns the associated {@code Path}.
         *
         * @return  Path
         */
        public Path getPath() {
            return path;
        }

        /**
         * Returns the {@code String} delimiter for the associated {@code Path}.
         *
         * @return  String delimiter
         */
        public String getDelimiter() {
            return delim;
        }
    }

}
