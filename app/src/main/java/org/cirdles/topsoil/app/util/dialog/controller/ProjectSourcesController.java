package org.cirdles.topsoil.app.util.dialog.controller;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.file.FileParser;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

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

    private Scene previousScene;
    private Scene nextScene;

    private Boolean didFinish;

    private BiMap<Path, Label> pathLabelBiMap;

    private ListProperty<Path> paths;
    public ListProperty<Path> pathsProperty() {
        if (paths == null) {
            paths = new SimpleListProperty<>(FXCollections.observableArrayList());
        }
        return paths;
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

    @FXML private void addFilesButtonAction() {
        List<File> selectedFiles = new ArrayList<>(TopsoilFileChooser.getTableFileChooser().showOpenMultipleDialog(
                MainWindow.getPrimaryStage()));
        List<File> rejectedFiles = new ArrayList<>();

        // Check all of the files for compatibility.
        Iterator<File> iterator = selectedFiles.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (!FileParser.isSupportedTableFile(file) || FileParser.isEmptyFile(file)) {
                selectedFiles.remove(file);
                rejectedFiles.add(file);
            } else {
                if (pathLabelBiMap.containsKey(file.toPath())) {
                    iterator.remove();
                }
            }
        }

        for (File file : selectedFiles) {
                Label pathLabel = new Label(file.getName());
                sourceFileListView.getItems().add(pathLabel);
                pathLabelBiMap.put(file.toPath(), pathLabel);
                pathsProperty().add(file.toPath());
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
    }

    @FXML private void removeFileButtonAction() {
        // Remove from paths
        pathsProperty().remove(pathLabelBiMap.inverse().get(sourceFileListView.getSelectionModel().getSelectedItem()));

        // Remove from map
        pathLabelBiMap.inverse().remove(sourceFileListView.getSelectionModel().getSelectedItem());

        // Remove from ListView
        sourceFileListView.getItems().remove(sourceFileListView.getSelectionModel().getSelectedItem());

    }

    @FXML private void cancelButtonAction() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML private void previousButtonAction() {
        if (previousScene != null) {
            ((Stage) nextButton.getScene().getWindow()).setScene(previousScene);
        } else {
            ((Stage) nextButton.getScene().getWindow()).close();
        }
    }

    @FXML private void nextButtonAction() {
        if (toggle.getSelectedToggle() == fromFilesButton && nextScene != null) {
            ((Stage) nextButton.getScene().getWindow()).setScene(nextScene);
        } else {
            didFinish = true;
            ((Stage) nextButton.getScene().getWindow()).close();
        }
    }

    private void updateNextButtonDisabledProperty() {
        if (toggle.getSelectedToggle() == null) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
    }

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



}
