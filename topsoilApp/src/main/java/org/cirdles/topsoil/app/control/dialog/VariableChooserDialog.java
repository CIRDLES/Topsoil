package org.cirdles.topsoil.app.control.dialog;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.variable.Variable;

import java.util.Map;

/**
 * @author Jake Marotta
 */
public class VariableChooserDialog extends Dialog<Map<Variable<?>, DataColumn<?>>> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private VariableChooserDialog(DataTable table) {
        super();

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().add(Main.getController().getTopsoilLogo());
        stage.initOwner(Main.getController().getPrimaryStage());
        stage.setTitle("Variable Chooser");
        stage.setResizable(true);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label messageLabel = new Label("Choose which variables to associate with each column.");
        messageLabel.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        DataTableOptionsDialog.VariableChooser chooser = new DataTableOptionsDialog.VariableChooser(table);
        chooser.setMaxWidth(800.0);

        VBox container = new VBox(messageLabel, chooser);
        container.setAlignment(Pos.TOP_CENTER);

        this.getDialogPane().setContent(container);

        this.setResultConverter(result -> {
            if (result == ButtonType.OK) {
                Map<Variable<?>, DataColumn<?>> selections = chooser.getSelections();
                return selections;
            } else {
                return null;
            }
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static Map<Variable<?>, DataColumn<?>> showDialog(DataTable table) {
        return new VariableChooserDialog(table).showAndWait().orElse(null);
    }

}
