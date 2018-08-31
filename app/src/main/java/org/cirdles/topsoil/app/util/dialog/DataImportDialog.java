package org.cirdles.topsoil.app.util.dialog;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.util.dialog.controller.DataPreviewController;

import java.util.HashMap;
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
public class DataImportDialog extends Dialog<Map<ImportDataType, Object>> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataImportDialog(String[] headers, Double[][] data) {
        super();
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.FINISH);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initOwner(MainWindow.getPrimaryStage());
        stage.getIcons().add(MainWindow.getWindowIcon());

        DataPreviewController controller = new DataPreviewController(headers, data);
        this.getDialogPane().setContent(controller);

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
                Map<ImportDataType, Object> selections = new HashMap<>();
                selections.put(ImportDataType.HEADERS, controller.getHeaders());
                selections.put(ImportDataType.DATA, controller.getData());
                selections.put(ImportDataType.UNCERTAINTY, controller.getUncertaintyFormat());
                selections.put(ImportDataType.ISOTOPE_TYPE, controller.getIsotopeType());
                selections.put(ImportDataType.VARIABLE_INDEX_MAP, controller.getVariableIndexMap());

                return selections;
            } else {
                return null;
            }
        });

    }

    /**
     * Shows a {@code Dialog} where the user can preview how their data will be imported, and assign plotting
     * variables to the columns that they wish to keep. This method returns several values as a {@code Map}, which
     * can be retrieved by their {@code ImportDataType}s.
     * <p>
     * For reference:
     *
     * ImportDataType.HEADERS = the {@code String} headers of the selected columns
     * ImportDataType.DATA = the {@code {@literal List<TopsoilDataEntry>}} containing rows with the values for the
     * selected columns
     * ImportDataType.UNCERTAINTY = the selected {@code UncertaintyFormat}
     *
     * @param   headers
     *          array of String headers
     * @param   data
     *          2D array of Double data
     *
     * @return  a Map of edited values
     */
    public static Map<ImportDataType, Object> showImportDialog(String[] headers,
                                                               Double[][] data) {

        DataImportDialog dialog = new DataImportDialog(headers, data);
        dialog.setTitle("Data Import Helper");

        return dialog.showAndWait().orElse(null);
    }
}
