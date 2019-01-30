package org.cirdles.topsoil.app.view.wizards;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.cirdles.topsoil.app.view.wizards.NewProjectWizard.Key.TITLE;
import static org.cirdles.topsoil.app.view.wizards.NewProjectWizard.Key.LOCATION;

/**
 * @author marottajb
 */
class NewProjectTitleView extends WizardPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "project-title.fxml";
    private static final int MAX_FILE_NAME_LENGTH = 60;

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private TextField titleTextField, pathTextField;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private StringProperty title = new SimpleStringProperty();
    String getTitle() {
        return title.get();
    }
    void setTitle(String str) {
        title.set(str);
    }

    private ObjectProperty<Path> projectLocation = new SimpleObjectProperty<>();
    Path getProjectLocation() {
        return projectLocation.get();
    }
    void setProjectLocation(Path path) {
        if (path != null) {
            String fileName = path.toString();

            if (fileName.length() > MAX_FILE_NAME_LENGTH) {
                fileName = "..." + fileName.substring(fileName.length() - MAX_FILE_NAME_LENGTH);
            }

            projectLocation.set(path);
            pathTextField.setText(fileName);
        }
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    NewProjectTitleView() {
        final ResourceExtractor re = new ResourceExtractor(NewProjectTitleView.class);
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
        }
    }

    @FXML
    protected void initialize() {
        projectLocation.set(Paths.get(System.getProperty("user.home")));

        for (ButtonType type : getButtonTypes()) {
            if (type.getButtonData().equals(ButtonBar.ButtonData.NEXT_FORWARD)) {
                Button nextButton = (Button) lookupButton(type);
                nextButton.setOnAction(event -> {
                    File file = new File(getProjectLocation().toString() + File.separator + getTitle() + ".topsoil");
                    if (file.exists()) {
                        TopsoilNotification.showNotification(
                                TopsoilNotification.NotificationType.YES_NO, "Existing Project",
                                "There is already a project in that location with that name. Would you like to replace it?")
                                           .filter(response -> response == ButtonType.NO)
                                           .ifPresent(response -> event.consume());
                    }
                });
                break;
            }
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void onEnteringPage(Wizard wizard) {
        wizard.setTitle("New Project: Title and Location");
        wizard.invalidProperty().bind(Bindings.createBooleanBinding(() -> {
            if (projectLocation.get() == null) {
                return true;
            } else {
                if (titleTextField.getText().equals("")) {
                    return true;
                } else {
                    return false;
                }
            }
        }, projectLocation));
    }

    public void onExitingPage(Wizard wizard) {
        wizard.invalidProperty().unbind();
        wizard.getSettings().put(TITLE, title.get());
        wizard.getSettings().put(LOCATION, projectLocation.get());
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
        if (file != null) {
            setProjectLocation(file.toPath());
        }
    }

}