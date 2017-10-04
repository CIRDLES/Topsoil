package org.cirdles.topsoil.app.util.dialog.controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.TopsoilException;
import org.cirdles.topsoil.app.util.dialog.DataImportKey;
import org.cirdles.topsoil.app.util.dialog.controller.ProjectSourcesController.PathDelimiterPair;
import org.cirdles.topsoil.app.util.file.FileParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for a screen that allows the user to preview their imported source files, as well as choose an {@link
 * UncertaintyFormat} and {@link IsotopeType} for each table.
 *
 * @author Jake Marotta
 */
public class ProjectPreviewController {

    @FXML private TabPane fileTabs;

    @FXML private Button cancelButton;
    @FXML private Button finishButton;

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

    private Boolean didFinish;

    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(ProjectPreviewController.class);
    private static final String DATA_PREVIEW_FXML = "data-preview.fxml";
    private static final String WARNING_ICON_PATH = "warning.png";
    private final ImageView WARNING_ICON = new ImageView(RESOURCE_EXTRACTOR.extractResourceAsPath(WARNING_ICON_PATH)
                                                                           .toUri().toString());

    /**
     * The previous scene in the "New Project" sequence. As of typing, this should be a Scene containing a
     * {@link ProjectSourcesController}.
     */
    private Scene previousScene;

    @FXML
    public void initialize() {
        pathDelimiterListProperty();
        didFinish = false;
        finishButton.setDisable(true);

        fileTabs.setStyle("-fx-open-tab-animation: NONE; -fx-close-tab-animation: NONE;");

        WARNING_ICON.setPreserveRatio(true);
        WARNING_ICON.setFitHeight(20.0);

        for (Tab tab : fileTabs.getTabs()) {
            ((ControllerTab) tab).getController().uncertaintyFormatProperty().addListener(c -> {
                boolean incomplete = false;
                for (Tab t : fileTabs.getTabs()) {
                    if (((ControllerTab) t).getController().getUncertaintyFormat() == null) {
                        incomplete = true;
                        break;
                    }
                }
                if (incomplete) {
                    finishButton.setDisable(true);
                } else {
                    finishButton.setDisable(false);
                }
            });
        }

        pathDelimiterListProperty().addListener((ListChangeListener<PathDelimiterPair>) c -> {
            c.next();

            if (c.wasAdded()) {
                PathDelimiterPair pair = pathDelimiterListProperty().get(c.getTo() - 1);

                finishButton.setDisable(true);
                ControllerTab controllerTab = new ControllerTab(pair.getPath(), pair.getDelimiter());
                controllerTab.getController().uncertaintyFormatProperty().addListener(ch -> {
                    updateFinishButtonDisabledProperty();
                });

                fileTabs.getTabs().add(controllerTab);
            } else if (c.wasRemoved()) {
                fileTabs.getTabs().remove(c.getFrom());
            }

        });
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
            ((Stage) finishButton.getScene().getWindow()).setScene(previousScene);
        } else {
            ((Stage) finishButton.getScene().getWindow()).close();
        }
    }

    /**
     * Finishes the "New Project" sequence.
     */
    @FXML private void finishButtonAction() {
        didFinish = true;
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    public Boolean didFinish() {
        return didFinish;
    }

    /**
     * Updates the {@link Button#disableProperty()} of {@link #finishButton} based on whether all required information
     * is present.
     */
    private void updateFinishButtonDisabledProperty() {
        boolean incomplete = false;
        for (Tab t : fileTabs.getTabs()) {
            if (((ControllerTab) t).getController().getUncertaintyFormat() == null) {
                incomplete = true;
                break;
            }
        }
        if (incomplete) {
            finishButton.setDisable(true);
        } else {
            finishButton.setDisable(false);
        }
    }

    /**
     * Returns a {@code List} of {@code Map}s containing information from each of the {@link DataPreviewController}s
     * from the specified source files.
     *
     * @return  List of Maps of DataImportKeys to Objects
     */
    public List<Map<DataImportKey, Object>> getSelections() {
        // TODO
        List<Map<DataImportKey, Object>> allSelections = new ArrayList<>();

        DataPreviewController controller;
        for (Tab tab : fileTabs.getTabs()) {
            controller = ((ControllerTab) tab).getController();
            Map<DataImportKey, Object> selections = new HashMap<>();

            selections.put(DataImportKey.TITLE, tab.getText());
            selections.put(DataImportKey.HEADERS, controller.getHeaders());
            selections.put(DataImportKey.DATA, controller.getData());
            selections.put(DataImportKey.UNCERTAINTY, controller.getUncertaintyFormat());
            selections.put(DataImportKey.ISOTOPE_TYPE, controller.getIsotopeType());
            selections.put(DataImportKey.VARIABLE_INDEX_MAP, controller.getVariableIndexMap());

            allSelections.add(selections);
        }

        return allSelections;
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
     * A custom {@code Tab} which contains a {@link DataPreviewController}.
     */
    private class ControllerTab extends Tab {

        private DataPreviewController controller;

        ControllerTab(Path filePath, String delim) {
            super();
            try {
                FXMLLoader loader = new FXMLLoader(RESOURCE_EXTRACTOR.extractResourceAsPath(DATA_PREVIEW_FXML).toUri().toURL());

                this.setContent(loader.load());
                this.controller = loader.getController();

                File file = filePath.toFile();

                this.setText(file.getName());

                ImageView warningIconCopy = new ImageView(WARNING_ICON.getImage());
                warningIconCopy.setPreserveRatio(true);
                warningIconCopy.setFitHeight(20.0);
                this.setGraphic(warningIconCopy);
                controller.uncertaintyFormatProperty().addListener(c -> {
                    if (controller.getUncertaintyFormat() == null) {
                        this.setGraphic(warningIconCopy);
                    } else {
                        this.setGraphic(null);
                    }
                    /*
                        The TabPane overflow menu doesn't automatically update the tab's graphic when it's changed,
                        so the only way to get it to do so is to remove the tab, then add it again. The animations
                        have been disabled to make this a smoother process.
                     */
                    TabPane tabPane = this.getTabPane();
                    int index = tabPane.getTabs().indexOf(this);
                    tabPane.getTabs().remove(this);
                    tabPane.getTabs().add(index, this);
                    tabPane.getSelectionModel().select(this);
                });

                String[] headers = null;
                Boolean containsHeaders = FileParser.detectHeader(file, delim);
                List<TopsoilDataEntry> data;

                if (containsHeaders) {
                    headers = FileParser.parseHeaders(file, delim);
                }

                data = FileParser.parseFile(file, delim, containsHeaders);

                controller.setData(headers, data);

            } catch (IOException | TopsoilException e) {
                e.printStackTrace();
            }
        }

        private DataPreviewController getController() {
            return controller;
        }
    }
}
