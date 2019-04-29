package org.cirdles.topsoil.app.control.dialog.wizards;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.dialog.DataImportDialog;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.file.TopsoilFileUtils;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.Delimiter;
import org.cirdles.topsoil.app.file.FileChoosers;
import org.cirdles.topsoil.app.ResourceBundles;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.cirdles.topsoil.app.control.dialog.wizards.MultipleImportWizard.INIT_HEIGHT;
import static org.cirdles.topsoil.app.control.dialog.wizards.MultipleImportWizard.INIT_WIDTH;
import static org.cirdles.topsoil.app.control.dialog.wizards.MultipleImportWizard.Key.TABLES;

/**
 * @author marottajb
 */
class MultipleImportSourcesView extends WizardPane {

    private static final String CONTROLLER_FXML = "project-sources.fxml";

    @FXML private Label selectFilesLabel;
    @FXML private Button addFilesButton, removeFileButton;
    @FXML private ListView<DataTable> filesListView;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private ListProperty<DataTable> tables = new SimpleListProperty<>(FXCollections.observableArrayList());
    private BiMap<DataTable, Path> tablePathMap = HashBiMap.create();
    private ResourceBundle resources = ResourceBundles.DIALOGS.getBundle();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    MultipleImportSourcesView() {
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, MultipleImportSourcesView.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setPrefSize(INIT_WIDTH, INIT_HEIGHT);
    }

    @FXML
    protected void initialize() {
        selectFilesLabel.setText(resources.getString("selectFilesLabel"));
        addFilesButton.setText(resources.getString("addFilesButton"));
        removeFileButton.setText(resources.getString("removeFileButton"));

        filesListView.itemsProperty().bind(tables);
        removeFileButton.disableProperty().bind(
                Bindings.isNull(filesListView.getSelectionModel().selectedItemProperty())
        );
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public void onEnteringPage(Wizard wizard) {
        wizard.invalidProperty().bind(tables.emptyProperty());
        wizard.setTitle(resources.getString("importMultiTitle"));
        if (wizard.getSettings().containsKey(TABLES)) {
            List<DataTable> tableList = (List<DataTable>) wizard.getSettings().get(TABLES);
            updateTables(tableList);
        }
    }

    @Override
    public void onExitingPage(Wizard wizard) {
        wizard.invalidProperty().unbind();
        wizard.getSettings().put(TABLES, tables.get());
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void updateTables(List<DataTable> newList) {
        for (DataTable table : tables) {
            if (! newList.contains(table)) {
                tables.remove(table);
                tablePathMap.remove(table);
            }
        }
    }

    /**
     * Upon pressing "Add Files...", the user is presented with a {@code FileChooser} where they can select multiple
     * table files to import into their new project.
     */
    @FXML
    private void addFilesButtonAction() {
        List<File> files = FileChoosers.openTableFile().showOpenMultipleDialog(Topsoil.getPrimaryStage());
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
                } else if (tablePathMap.containsValue(path)) {
                    iterator.remove();  // don't read in duplicates
                } else {
                    try {
                        Delimiter guess = TopsoilFileUtils.guessDelimiter(path);
                        Map<DataImportDialog.Key, Object> fileSettings =
                                DataImportDialog.showDialog(path.getFileName().toString(), guess, (Stage) this.getScene().getWindow());
                        if (guess == null) {
                            guess = (Delimiter) fileSettings.get(DataImportDialog.Key.DELIMITER);
                        }
                        String delimiter = (guess != null) ? guess.asString() :
                                ((Delimiter) fileSettings.get(DataImportDialog.Key.DELIMITER)).asString();
                        DataTemplate template = (DataTemplate) fileSettings.get(DataImportDialog.Key.TEMPLATE);
                        DataParser parser = template.getParser();
                        DataTable table = parser.parseDataTable(path, delimiter, path.getFileName().toString());
                        tables.add(table);
                        tablePathMap.put(table, path);
                    } catch (IOException e) {
                        iterator.remove();
                        rejectedFiles.add(file);
                    }
                }
            }

            if ( rejectedFiles.size() > 0 ) {
                StringJoiner badFileNames = new StringJoiner("\n");
                for (File file : rejectedFiles) {
                    badFileNames.add(file.getName());
                }

                TopsoilNotification.error(
                        resources.getString("fileErrorTitle"),
                        resources.getString("fileErrorMessage") + " \n\n" + badFileNames.toString()
                );
            }
        }
    }

    /**
     * Upon pressing "Remove File", the currently selected item in {@link #filesListView} is removed.
     */
    @FXML
    private void removeFilesButtonAction() {
        // Remove from paths
        DataTable selected = filesListView.getSelectionModel().getSelectedItem();
        tables.remove(selected);
        tablePathMap.remove(selected);
    }

    private boolean isFileValid(File file) {
        Path path = Paths.get(file.toURI());
        boolean valid;
        try {
            valid = TopsoilFileUtils.isFileSupported(path) && ! TopsoilFileUtils.isFileEmpty(path);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
        return valid;
    }

}
