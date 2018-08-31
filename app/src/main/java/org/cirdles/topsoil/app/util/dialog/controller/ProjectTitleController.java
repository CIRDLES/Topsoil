package org.cirdles.topsoil.app.util.dialog.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Controller that allows the user to specify the title and location of their new project.
 *
 * @author marottajb
 */
public class ProjectTitleController extends AnchorPane {

    private static final String PROJECT_TITLE_FXML = "project-title.fxml";
    private final ResourceExtractor resourceExtractor = new ResourceExtractor(ProjectTitleController.class);

    @FXML private TextField titleTextField, pathTextField;
    @FXML private Button cancelButton, nextButton;

    /**
     * The next scene in the "New Project" sequence. As of typing, this is a Scene containing a
     * {@link ProjectSourcesController}.
     */
    private Scene nextScene;
    private static final int MAX_FILE_NAME_LENGTH = 60;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

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

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ProjectTitleController() {
        super();

        try {
            FXMLLoader loader = new FXMLLoader(resourceExtractor.extractResourceAsPath(PROJECT_TITLE_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + PROJECT_TITLE_FXML, e);
        }
    }

    @FXML
    protected void initialize() {
        nextButton.setDisable(true);
        projectLocationProperty().addListener(c -> updateNextButtonDisabledProperty());
        titleTextField.textProperty().addListener(c -> updateNextButtonDisabledProperty());
        //Make home directory the default file location
        setProjectFile(new File(System.getProperty("user.home")));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns the specified title of the new project.
     *
     * @return  String title
     */
    public String getTitle() {
        return titleTextField.getText();
    }

    /**
     * Sets the next {@code Scene} in the "New Project" sequence. This scene will replace the current one when the
     * user clicks the "Next" button.
     *
     * @param   scene
     *          next Scene
     */
    public void setNextScene(Scene scene) {
        nextScene = scene;
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    /**
     * When the user presses the "..." button, a {@code DirectoryChooser} is opened where they can specify the
     * desired location for their new project file.
     */
    @FXML private void choosePathButtonAction() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose Project Location");

        File file = dirChooser.showDialog(MainWindow.getPrimaryStage());
        setProjectFile(file);
    }

    private void setProjectFile(File file) {
        if (file != null) {
            String fileName = file.getPath();

            if (fileName.length() > MAX_FILE_NAME_LENGTH) {
                fileName = "..." + fileName.substring(fileName.length() - MAX_FILE_NAME_LENGTH);
            }

            setProjectLocation(file);
            pathTextField.setText(fileName);
        }
    }

    /**
     * Closes the {@code Stage} without doing anything when the user clicks "Cancel".
     */
    @FXML private void cancelButtonAction() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    /**
     * Checks that all required information is valid, then sets the {@code Stage} to display the next {@code Scene}
     * in the "New Project" sequence.
     */
    @FXML private void nextButtonAction() {
        File file = new File(getProjectLocation().toString() + File.separator + getTitle() + ".topsoil");
        System.out.println("Target: C:\\Users\\Jake\\Desktop\\test.topsoil");
        System.out.println("Actual: " + file.getAbsolutePath());

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

    /**
     * Updates the {@link Button#disableProperty()} of {@link #nextButton} based on whether all required information
     * is present.
     */
    private void updateNextButtonDisabledProperty() {
        if (projectLocation.get() == null) {
            nextButton.setDisable(true);
        } else if (titleTextField.getText().equals("")) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
    }
}
