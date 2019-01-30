package org.cirdles.topsoil.app.view.wizards;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.data.ColumnTree;
import org.cirdles.topsoil.app.data.DataSegment;

import java.util.*;

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
public class DataImportDialog extends Dialog<Map<ImportKey, Object>> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataImportDialog(ColumnTree columnTree, DataSegment[] dataSegments) {
        super();
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.FINISH);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initOwner(MainWindow.getPrimaryStage());
        stage.getIcons().add(MainWindow.getController().getTopsoilLogo());

        DataImportOptionsView controller = new DataImportOptionsView(columnTree, Arrays.asList(dataSegments));
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
                Map<ImportKey, Object> selections = new HashMap<>();
                selections.put(ImportKey.COLUMN_TREE, controller.getColumnTree());
                selections.put(ImportKey.DATA_SEGMENTS, controller.getDataSegments());
                selections.put(ImportKey.UNCT_FORMAT, controller.getUncertaintyFormat());
                selections.put(ImportKey.ISO_SYSTEM, controller.getIsotopeType());
                selections.put(ImportKey.VARIABLE_MAP, controller.getVariableColumnMap());

                return selections;
            } else {
                return null;
            }
        });

    }

    /**
     * Shows a {@code Dialog} where the user can preview how their data will be imported, and assign plotting
     * variables to the columns that they wish to keep. This method returns several values as a {@code Map}, which
     * can be retrieved by their {@code ImportKey}s.
     * <p>
     * For reference:
     *
     * ImportKey.HEADERS = the {@code String} headers of the selected columns
     * ImportKey.DATA = the {@code {@literal List<TopsoilDataEntry>}} containing rows with the values for the
     * selected columns
     * ImportKey.UNCERTAINTY = the selected {@code UncertaintyFormat}
     *
     * @param   columnTree
     *          data headers as a ColumnTree
     * @param   dataSegments
     *          array of DataSegments
     *
     * @return  a Map of edited values
     */
    public static Map<ImportKey, Object> showImportDialog(ColumnTree columnTree, DataSegment[] dataSegments) {

        DataImportDialog dialog = new DataImportDialog(columnTree, dataSegments);
        dialog.setTitle("Data Import Helper");

        return dialog.showAndWait().orElse(null);
    }

}
