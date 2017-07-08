package org.cirdles.topsoil.app.util.dialog;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;

/**
 * Provides a {@link Dialog} for
 */
public class TopsoilTextInputDialog extends TextInputDialog {

    private TopsoilTextInputDialog(String stageTitle, String contentText, String defaultValue) {
        super(defaultValue);

        setContentText(contentText);
        setHeaderText("");
        setGraphic(null);

        Stage dialogStage = (Stage) getDialogPane().getScene().getWindow();
        dialogStage.setTitle(stageTitle);
        dialogStage.getIcons().add(MainWindow.getWindowIcon());
        dialogStage.initOwner(MainWindow.getPrimaryStage());

        if (defaultValue.equals("")) {
            getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        }

        getEditor().textProperty().addListener(c -> {
            if (getEditor().getText() == null || getEditor().getText().equals("")) {
                getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            } else {
                getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
            }
        });
    }

    public static String showDialog(String stageTitle, String contentText) {
        return new TopsoilTextInputDialog(stageTitle, contentText, "").showAndWait().orElse(null);
    }

    public static String showDialog(String stageTitle, String contentText, String defaultValue) {
        return new TopsoilTextInputDialog(stageTitle, contentText, defaultValue).showAndWait().orElse(null);
    }
}
