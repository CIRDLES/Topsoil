package org.cirdles.topsoil.app.control.dialog;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.util.file.parser.Delimiter;

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
        this.setTitle("Importing: " + sourceName);
        this.initOwner(owner);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().addAll(Main.getController().getTopsoilLogo());
        stage.setResizable(false);

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

    public static Map<DataImportDialog.Key, Object> showDialog(String sourceName) {
        return showDialog(sourceName, null);
    }

    public static Map<DataImportDialog.Key, Object> showDialog(String sourceName, Delimiter initial) {
        return showDialog(sourceName, initial, Main.getController().getPrimaryStage());
    }

    public static Map<DataImportDialog.Key, Object> showDialog(String sourceName, Delimiter initial,
                                                               Stage owner) {
        return new DataImportDialog(sourceName, initial, owner).showAndWait().orElse(null);
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    class DataImportDialogController extends VBox {

        private static final String CONTROLLER_FXML = "data-import-dialog.fxml";

        @FXML private ComboBox<Delimiter> delimiterComboBox;
        @FXML private ComboBox<DataTemplate> templateComboBox;

        private Delimiter delimiter;

        DataImportDialogController(Delimiter initial) {
            delimiter = initial;
            final ResourceExtractor re = new ResourceExtractor(DataImportDialogController.class);

            FXMLLoader loader;
            try {
                loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
                loader.setRoot(this);
                loader.setController(this);
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
            }
        }

        @FXML
        protected void initialize() {
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
