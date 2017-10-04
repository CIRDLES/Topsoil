package org.cirdles.topsoil.app.util.dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.util.dialog.controller.DataPreviewController;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a custom {@code Dialog} that allows a user to preview their data before it is fully loaded into a table.
 * This is important because the order of the columns in the data may not match the way that Topsoil organizes its
 * columns.
 * <p>
 * Due to current limitations in the way Topsoil handles its data, this dialog is necessary for the initial data
 * organization, so that the table can apply the {@code UncertaintyFormat} to the uncertainty values properly. Once a
 * mechanism is in place for the user to freely assign variables to their data columns, this dialog may become
 * unnecessary.
 *
 * @author Jake Marotta
 */
public class DataImportDialog extends Dialog<Map<DataImportKey, Object>> {

    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(DataImportDialog.class);
    private static final String DATA_PREVIEW_FXML = "controller/data-preview.fxml";
    private DataPreviewController controller;

    /**
     * Constructs a new {@code DataImportDialog} with the specified data and column names.
     *
     * @param headers   array of String names for data columns
     * @param data  List of TopsoilDataEntries of data
     */
    private DataImportDialog(String[] headers, List<TopsoilDataEntry> data) {
        super();
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.FINISH);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initOwner(MainWindow.getPrimaryStage());
        stage.getIcons().add(MainWindow.getWindowIcon());

        try {
            // Load a DataPreviewController
            FXMLLoader loader = new FXMLLoader(RESOURCE_EXTRACTOR.extractResourceAsPath(DATA_PREVIEW_FXML)
                                                                 .toUri().toURL());
            this.getDialogPane().setContent(loader.load());
            controller = loader.getController();
            controller.setData(headers, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // User can't click "Finish" until they select an uncertainty format.
        this.getDialogPane().lookupButton(ButtonType.FINISH).setDisable(true);
        controller.uncertaintyFormatProperty().addListener(c -> {
            if (controller.getUncertaintyFormat() != null) {
                this.getDialogPane().lookupButton(ButtonType.FINISH).setDisable(false);

                // This should never happen after an initial selection, but just in case.
            } else {
                this.getDialogPane().lookupButton(ButtonType.FINISH).setDisable(true);
            }
        });

        setResultConverter(value -> {
            if (value == ButtonType.FINISH) {
                Map<DataImportKey, Object> selections = new HashMap<>();
                selections.put(DataImportKey.HEADERS, controller.getHeaders());
                selections.put(DataImportKey.DATA, controller.getData());
                selections.put(DataImportKey.UNCERTAINTY, controller.getUncertaintyFormat());
                selections.put(DataImportKey.ISOTOPE_TYPE, controller.getIsotopeType());
                selections.put(DataImportKey.VARIABLE_INDEX_MAP, controller.getVariableIndexMap());

                return selections;
            } else {
                return null;
            }
        });

    }

    /**
     * Shows a {@code Dialog} where the user can preview how their data will be imported, and assign plotting
     * variables to the columns that they wish to keep. This method returns several values as a {@code Map}, which
     * can be retrieved by their {@code DataImportKey}s.
     * <p>
     * For reference:
     *
     * DataImportKey.HEADERS = the {@code String} headers of the selected columns
     * DataImportKey.DATA = the {@code {@literal List<TopsoilDataEntry>}} containing rows with the values for the
     * selected columns
     * DataImportKey.UNCERTAINTY = the selected {@code UncertaintyFormat}
     *
     * @param headers   array of String column headers
     * @param data  List of TopsoilDataEntry rows
     * @return  a Map of values
     */
    public static Map<DataImportKey, Object> showImportDialog(@Nullable String[] headers, List<TopsoilDataEntry> data) {

        DataImportDialog dialog = new DataImportDialog(headers, data);
        dialog.setTitle("Data Import Helper");

        return dialog.showAndWait().orElse(null);
    }
}
