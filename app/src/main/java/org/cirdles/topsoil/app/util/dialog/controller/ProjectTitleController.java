package org.cirdles.topsoil.app.util.dialog.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;

import java.io.File;
import java.nio.file.Path;

/**
 * Controller for a window that allows the user to specify the title and location of their new project.
 *
 * @author Jake Marotta
 */
public class ProjectTitleController {

    private ObjectProperty<Path> projectLocation;
    private ObjectProperty<Path> projectLocationProperty() {
        if (projectLocation == null) {
            projectLocation = new SimpleObjectProperty<>(null);
        }
        return projectLocation;
    }

    /**
     * Returns the selected project file location.
     *
     * @return  File location
     */
    public Path getProjectLocation() {
        return projectLocationProperty().get();
    }
    private void setProjectLocation(File file) {
        projectLocationProperty().set(file.toPath());
    }

    @FXML private TextField titleTextField;
    @FXML private TextField pathTextField;

    @FXML private Button cancelButton;
    @FXML private Button nextButton;

    private Scene nextScene;
    private final int MAX_FILE_NAME_LENGTH = 60;

    @FXML
    public void initialize() {
        nextButton.setDisable(true);
        projectLocationProperty().addListener(c -> {
            updateNextButtonDisabledProperty();
        });

        titleTextField.textProperty().addListener(c -> {
            updateNextButtonDisabledProperty();
        });
    }

    @FXML private void choosePathButtonAction() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose Project Location");

        File file = dirChooser.showDialog(MainWindow.getPrimaryStage());

        if (file != null) {
            String fileName = file.getPath();


            if (fileName.length() > MAX_FILE_NAME_LENGTH) {
                fileName = "..." + fileName.substring(fileName.length() - MAX_FILE_NAME_LENGTH);
            }

            setProjectLocation(file);
            pathTextField.setText(fileName);
        }
    }

    @FXML private void cancelButtonAction() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML private void nextButtonAction() {
        File file = new File(getProjectLocation().toString() + "\\" + getTitle() + ".topsoil");
        System.out.println(file.getPath());
        if (file.exists()) {
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.YES_NO,
                    "Existing Project",
                    "There is already a project in that location with that name. Would you like to replace it?"
            )
                .filter(response -> response == ButtonType.YES)
                .ifPresent(response -> {
                    if (nextScene != null) {
                        ((Stage) nextButton.getScene().getWindow()).setScene(nextScene);
                    } else {
                        ((Stage) nextButton.getScene().getWindow()).close();
                    }
                });
        } else {
            if (nextScene != null) {
                ((Stage) nextButton.getScene().getWindow()).setScene(nextScene);
            } else {
                ((Stage) nextButton.getScene().getWindow()).close();
            }
        }
    }

    private void updateNextButtonDisabledProperty() {
        if (projectLocation == null) {
            nextButton.setDisable(true);
        } else if (titleTextField.getText().equals("")) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
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
     * Returns the specified title of the new project.
     *
     * @return  String title
     */
    public String getTitle() {
        return titleTextField.getText();
    }

}
