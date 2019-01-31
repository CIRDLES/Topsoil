package org.cirdles.topsoil.app.view.wizards;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.file.DataParser;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.cirdles.topsoil.app.view.wizards.NewProjectWizard.Key.SOURCES;

/**
 * @author marottajb
 */
class NewProjectSourcesView extends WizardPane {

    private static final String CONTROLLER_FXML = "project-sources.fxml";

    @FXML private Button addFilesButton, removeFileButton;
    @FXML private ListView<ProjectSource> sourceFileListView;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private ListProperty<ProjectSource> sources = new SimpleListProperty<>(FXCollections.observableArrayList());

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    NewProjectSourcesView() {
        final ResourceExtractor re = new ResourceExtractor(NewProjectSourcesView.class);
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
        sourceFileListView.itemsProperty().bind(sources);
        sourceFileListView.itemsProperty().get().addListener((ListChangeListener<? super ProjectSource>) c -> {
            c.next();
            if (c.wasAdded()) {
                for (Object object : c.getAddedSubList()) {
                    if (object instanceof ProjectSource) {
                        ProjectSource source = (ProjectSource) object;
                        ComboBox<DataParser.DataTemplate> templateComboBox =
                                new ComboBox<>(FXCollections.observableArrayList(DataParser.DataTemplate.values()));
                        templateComboBox.getSelectionModel().select(source.getTemplate());
                        templateComboBox.getSelectionModel().selectedItemProperty().addListener(
                                (observable, oldValue, newValue) -> source.setTemplate(newValue));
                    }
                }
            }
        });
        // Disable file selection controls
        addFilesButton.setDisable(true);
        removeFileButton.setDisable(true);
        sourceFileListView.setDisable(true);

        sourceFileListView.getSelectionModel().selectedItemProperty().addListener(c -> {
            if ( sourceFileListView.getSelectionModel().getSelectedItem() == null ) {
                removeFileButton.setDisable(true);
            } else {
                removeFileButton.setDisable(false);
            }
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public void onEnteringPage(Wizard wizard) {
        wizard.setTitle("New Project: Sources");
        wizard.invalidProperty().bind(sources.emptyProperty());
        if (wizard.getSettings().containsKey(SOURCES)) {
            List<ProjectSource> projectSources = (List<ProjectSource>) wizard.getSettings().get("SOURCES");
            sources.setAll(projectSources);
        }
    }

    @Override
    public void onExitingPage(Wizard wizard) {
        wizard.invalidProperty().unbind();
        wizard.getSettings().put(SOURCES, sources.get());
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Upon pressing "Add Files...", the user is presented with a {@code FileChooser} where they can select multiple
     * table files to import into their new project.
     */
    @FXML
    private void addFilesButtonAction() {
        List<File> files = TopsoilFileChooser.openTableFile().showOpenMultipleDialog(Main.getPrimaryStage());
        if ( files != null && !files.isEmpty() ) {
            List<File> selectedFiles = new ArrayList<>(files);
            List<File> rejectedFiles = new ArrayList<>();

            // Check all of the files for compatibility.
            Iterator<File> iterator = selectedFiles.iterator();
            Path path;
            while ( iterator.hasNext() ) {
                File file = iterator.next();
                path = Paths.get(file.toURI());
                if ( !isFileValid(file) ) {
                    iterator.remove();
                    rejectedFiles.add(file);
                } else {
                    if ( alreadyReadPath(path)) {
                        iterator.remove();
                        rejectedFiles.add(file);
                    } else {
                        sources.add(new ProjectSource(path, DataParser.DataTemplate.DEFAULT, null, null));
                    }
                }
            }

            if ( rejectedFiles.size() > 0 ) {
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
    }

    /**
     * Upon pressing "Remove File", the currently selected item in {@link #sourceFileListView} is removed.
     */
    @FXML
    private void removeFileButtonAction() {
        // Remove from paths
        sources.remove(sourceFileListView.getSelectionModel().getSelectedItem());
    }

    private boolean isFileValid(File file) {
        Path path = Paths.get(file.toURI());
        boolean valid;
        try {
            valid = DataParser.isFileSupported(path) && !DataParser.isFileEmpty(path);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
        return valid;
    }

    private boolean alreadyReadPath(Path path) {
        for (ProjectSource source : sources) {
            if (source.getPath().equals(path)) {
                return true;
            }
        }
        return false;
    }

}
