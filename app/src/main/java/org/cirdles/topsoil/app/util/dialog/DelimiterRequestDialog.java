package org.cirdles.topsoil.app.util.dialog;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.util.file.Delimiter;

/**
 * @author marottajb
 */
public class DelimiterRequestDialog extends Dialog<String> {

    private DelimiterRequestDialog(String windowTitle, String message, Boolean isImport) {
        super();
        String otherDelimiterOption = "Other";
        String unknownDelimiterOption = "Unknown";

        this.setTitle(windowTitle);
        ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(
                MainWindow.getWindowIcon());
        this.initOwner(MainWindow.getPrimaryStage());

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_RIGHT);

        Label requestLabel = new Label(message);
        ChoiceBox delimiterChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(Delimiter.values()));
        delimiterChoiceBox.getItems().addAll(new Separator(), otherDelimiterOption, unknownDelimiterOption);
        delimiterChoiceBox.getItems().addAll(otherDelimiterOption, unknownDelimiterOption);
        delimiterChoiceBox.setConverter(new StringConverter() {
            @Override
            public String toString(Object object) {
                if (object instanceof Delimiter) {
                    return ((Delimiter) object).getName();
                }
                return object.toString();
            }

            @Override
            public Object fromString(String string) {
                try {
                    return Delimiter.valueOf(string.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return string;
                }
            }
        });

        Label otherLabel = new Label("Other: ");
        TextField otherTextField = new TextField();
        otherTextField.disableProperty().bind(
                Bindings.not(
                        Bindings.equal(otherDelimiterOption, delimiterChoiceBox.getSelectionModel().selectedItemProperty())
                )
        );

        grid.add(requestLabel, 0, 0);
        grid.add(delimiterChoiceBox, 1, 0);
        grid.add(otherLabel, 0, 1);
        grid.add(otherTextField, 1, 1);

        GridPane.setMargin(delimiterChoiceBox, new Insets(5.0, 5.0, 5.0, 5.0));

        VBox vBox = new VBox(10.0, grid);
        if (isImport) {
            Label adviceLabel = new Label("*(If copying from spreadsheet, select \"Tabs\".)");
            adviceLabel.setTextFill(Color.DARKRED);

            vBox.getChildren().add(adviceLabel);
        }
        this.getDialogPane().setContent(vBox);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        this.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(
                Bindings.isNotNull(delimiterChoiceBox.getSelectionModel().selectedItemProperty())
        );

        this.setResultConverter(value -> {
            if (value == ButtonType.OK) {
                if (delimiterChoiceBox.getValue().equals(otherDelimiterOption)) {
                    return otherTextField.getText().trim();
                } else if (delimiterChoiceBox.getValue().equals(unknownDelimiterOption)) {
                    return null;
                } else {
                    return delimiterChoiceBox.getValue().toString();
                }
            }

            return null;
        });
    }

    /**
     * Presents the user with a {@code Dialog} requesting them for the delimiter of some delimited data.
     *
     * @param   windowTitle
     *          String title of the Dialog
     * @param   message
     *          String informational message or request
     * @param   isImport
     *          Boolean value, true if importing data
     *
     * @return  String delimiter
     */
    public static String showDialog(String windowTitle, String message, Boolean isImport) {
        return new DelimiterRequestDialog(windowTitle, message, isImport).showAndWait().orElse(null);
    }

}
