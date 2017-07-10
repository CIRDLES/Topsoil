package org.cirdles.topsoil.app.util.dialog;

import javafx.collections.MapChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.app.util.dialog.controller.VariableChooserController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Jake Marotta
 */
public class VariableChooserDialog extends Dialog<Map<Variable<Number>, TopsoilDataColumn>> {

    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(VariableChooserDialog.class);
    private final String VARIABLE_CHOOSER_FXML = "controller/variable-chooser.fxml";

    private VariableChooserController controller;

    private VariableChooserDialog(List<TopsoilDataColumn> columns,
                                  Map<Variable<Number>, TopsoilDataColumn> selections,
                                  List<Variable<Number>> requiredVariables) {
        super();

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().add(MainWindow.getWindowIcon());
        stage.initOwner(MainWindow.getPrimaryStage());
        stage.setTitle("Variable Chooser");

        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node content;
        controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    RESOURCE_EXTRACTOR.extractResourceAsPath(VARIABLE_CHOOSER_FXML).toUri().toURL());
            content = loader.load();
            controller = loader.getController();

            // Disable OK button if not all required variables are assigned.
            this.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            controller.selectionsProperty().addListener((MapChangeListener<? super Variable<Number>, ? super TopsoilDataColumn>) c -> {
                Boolean shouldDisableOK = false;
                if (!(requiredVariables == null)) {
                    for (Variable<Number> v : requiredVariables) {
                        if (!controller.selectionsProperty().containsKey(v)) {
                            shouldDisableOK = true;
                            break;
                        }
                    }
                }
                this.getDialogPane().lookupButton(ButtonType.OK).setDisable(shouldDisableOK);
            });

            controller.setup(columns, selections, requiredVariables);

            this.getDialogPane().setContent(content);

            this.setResultConverter(result -> {
                if (result == ButtonType.OK) {
                    return controller.getSelections();
                } else {
                    return null;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Variable<Number>, TopsoilDataColumn> showDialog(TopsoilTableController tableController,
                                                                      List<Variable<Number>> requiredVariables) {

        List<TopsoilDataColumn> columns = tableController.getTable().getDataColumns();
        Map<Variable<Number>, TopsoilDataColumn> currentSelections = tableController.getTable().getVariableAssignments();
        return new VariableChooserDialog(columns, currentSelections, requiredVariables).showAndWait().orElse(null);
    }

    public static Map<Variable<Number>, TopsoilDataColumn> showDialog(List<TopsoilDataColumn> columns,
                                                                      Map<Variable<Number>, TopsoilDataColumn>
                                                                              currentSelections,
                                                                      List<Variable<Number>> requiredVariables) {
        return new VariableChooserDialog(columns, currentSelections, requiredVariables).showAndWait().orElse(null);
    }

}
