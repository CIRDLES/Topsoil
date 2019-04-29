package org.cirdles.topsoil.app.control.dialog;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.file.Delimiter;
import org.cirdles.topsoil.app.ResourceBundles;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author marottajb
 */
public class DataImportDialog extends Dialog<Map<DataImportDialog.Key, Object>> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataImportDialog(String sourceName, Delimiter initial, Stage owner) {
        this.setTitle(ResourceBundles.DIALOGS.getString("importingData") + " " + sourceName);
        this.initOwner(owner);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().addAll(Topsoil.getLogo());
        stage.setOnShown(event -> stage.requestFocus());

        DataImportDialogController controller = new DataImportDialogController(initial);
        this.getDialogPane().setContent(controller);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        this.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(
                Bindings.isNull(controller.delimiterComboBox.getSelectionModel().selectedItemProperty())
        );

        this.setResultConverter(value -> {
            if (value == ButtonType.OK) {
                Map<Key, Object> choices = new HashMap<>();
                choices.put(Key.DELIMITER, controller.delimiterComboBox.getValue());
                choices.put(Key.TEMPLATE, controller.templateComboBox.getValue());
                return choices;
            }
            return null;
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Displays a dialog prompting the user for information about a table of data that they intend to import.
     *
     * @param sourceName    String name of data source
     * @param delimiter     Delimiter
     *
     * @return              Map of input selections
     */
    public static Map<DataImportDialog.Key, Object> showDialog(String sourceName, Delimiter delimiter) {
        return showDialog(sourceName, delimiter, Topsoil.getPrimaryStage());
    }

    /**
     * Displays a dialog prompting the user for information about a table of data that they intend to import.
     *
     * @param sourceName    String name of data source
     * @param delimiter     Delimiter
     * @param owner         the Stage owner of this dialog
     *
     * @return              Map of input selections
     */
    public static Map<DataImportDialog.Key, Object> showDialog(String sourceName, Delimiter delimiter, Stage owner) {
        DataImportDialog dialog = new DataImportDialog(sourceName, delimiter, owner);
        return dialog.showAndWait().orElse(null);
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    class DataImportDialogController extends VBox {

        private static final String CONTROLLER_FXML = "data-import-dialog.fxml";

        @FXML private Label delimiterLabel, templateLabel;
        @FXML private ComboBox<Delimiter> delimiterComboBox;
        @FXML private ComboBox<DataTemplate> templateComboBox;

        private Delimiter delimiter;

        DataImportDialogController(Delimiter initial) {
            delimiter = initial;
            try {
                FXMLUtils.loadController(CONTROLLER_FXML, DataImportDialogController.class, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @FXML
        protected void initialize() {
            delimiterLabel.setText(ResourceBundles.DIALOGS.getString("delimiterLabel"));
            templateLabel.setText(ResourceBundles.DIALOGS.getString("templateLabel"));

            delimiterComboBox.getItems().addAll(Delimiter.values());
            if (delimiter != null) {
                delimiterComboBox.getSelectionModel().select(delimiter);
                delimiterComboBox.setDisable(true);
            }

            templateComboBox.getItems().addAll(DataTemplate.values());
            templateComboBox.getSelectionModel().select(DataTemplate.DEFAULT);
        }
    }

    public enum Key {
        DELIMITER,
        TEMPLATE
    }
}
