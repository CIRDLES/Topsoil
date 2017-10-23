package org.cirdles.topsoil.app.util.dialog;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.app.util.dialog.controller.VariableColumnChooser;

import java.util.List;
import java.util.Map;

/**
 * @author Jake Marotta
 */
public class VariableChooserDialog extends Dialog<Map<Variable<Number>, TopsoilDataColumn>> {

    private VariableChooserDialog(List<TopsoilDataColumn> columns,
                                  List<Variable<Number>> variables,
                                  Map<Variable<Number>, TopsoilDataColumn> selections,
                                  List<Variable<Number>> required) {
        super();

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().add(MainWindow.getWindowIcon());
        stage.initOwner(MainWindow.getPrimaryStage());
        stage.setTitle("Variable Chooser");
        stage.setResizable(true);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label messageLabel = new Label("Choose which variables to associate with each column.");
        messageLabel.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        VariableColumnChooser chooser = new VariableColumnChooser(columns, variables, selections, required);

        VBox container = new VBox(messageLabel, chooser);
        container.setAlignment(Pos.TOP_CENTER);

        // Disable OK button if not all required variables are assigned.
        this.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        chooser.selectionsProperty().addListener((MapChangeListener<? super Variable<Number>, ? super TopsoilDataColumn>) c -> {
            Boolean shouldDisableOK = false;
            if (!(required == null)) {
                for (Variable<Number> v : required) {
                    if (!chooser.selectionsProperty().containsKey(v)) {
                        shouldDisableOK = true;
                        break;
                    }
                }
            }
            this.getDialogPane().lookupButton(ButtonType.OK).setDisable(shouldDisableOK);
        });
        this.getDialogPane().setContent(container);

        // The Scene doesn't seem to be completely done laying out its Nodes by the time this event is fired. Since
        // Platform.runLater() isn't being used extensively elsewhere, this works fine. If that changes, this may
        // have to be changed, as well.
        this.setOnShown(event -> Platform.runLater(() ->  {
            chooser.callAfterVisible();
            if (stage.getWidth() > 800.0) {
                stage.setWidth(800.0);
            }
            if (stage.getHeight() > 600.0) {
                stage.setHeight(600.0);
            }
        }));

        this.setResultConverter(result -> {
            if (result == ButtonType.OK) {
                return chooser.getSelections();
            } else {
                return null;
            }
        });
    }

    public static Map<Variable<Number>, TopsoilDataColumn> showDialog(TopsoilTableController tableController,
                                                                      List<Variable<Number>> requiredVariables) {

        List<TopsoilDataColumn> columns = tableController.getTable().getDataColumns();
        List<Variable<Number>> variables = Variables.VARIABLE_LIST;
        Map<Variable<Number>, TopsoilDataColumn> currentSelections = tableController.getTable().getVariableAssignments();

        return new VariableChooserDialog(columns, variables, currentSelections, requiredVariables).showAndWait().orElse(null);
    }
}
