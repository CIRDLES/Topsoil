package org.cirdles.topsoil.app.control.wizards;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.model.DataTable;

public class DataImportDialog extends Dialog<DataTable> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataImportDialog(DataTable table) {
        super();
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.FINISH);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initOwner(Main.getController().getPrimaryStage());
        stage.getIcons().add(Main.getController().getTopsoilLogo());

        DataTableOptionsView controller = new DataTableOptionsView(table);
        this.getDialogPane().setContent(controller);

        // User can't click "Finish" until they select an uncertainty format.
        this.getDialogPane().lookupButton(ButtonType.FINISH).setDisable(true);
        setResultConverter(value -> {
            if (value == ButtonType.FINISH) {
                return controller.getDataTable();
            } else {
                return null;
            }
        });

    }

    public static DataTable showImportDialog(DataTable table) {
        DataImportDialog dialog = new DataImportDialog(table);
        dialog.setTitle("Data Import Helper");
        return dialog.showAndWait().orElse(null);
    }

}
