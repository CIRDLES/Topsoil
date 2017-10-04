package org.cirdles.topsoil.app.util.dialog;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Jake Marotta
 */
public class DelimiterRequestDialog extends Dialog<String> {

    /**
     * A key for referencing commas in {@link #COMMON_DELIMITERS}.
     */
    private static final String COMMA = "Commas";

    /**
     * A key for referencing tabs in {@link #COMMON_DELIMITERS}.
     */
    private static final String TAB = "Tabs";

    /**
     * A key for referencing colons in {@link #COMMON_DELIMITERS}.
     */
    private static final String COLON = "Colons";

    /**
     * A key for referencing semicolons in {@link #COMMON_DELIMITERS}.
     */
    private static final String SEMICOLON = "Semicolons";

    /**
     * A {@code HashMap} populated with common delimiters.
     *
     * <p>This is referenced when trying to guess the delimiter of a
     * .txt file, or another form of input where the delimiter isn't clear.
     *
     * <p>Currently supported: commas, tabs, colons, semicolons
     *
     */
    private static final HashMap<String, String> COMMON_DELIMITERS; // Checked against when guessing a delimiter
    static {
        COMMON_DELIMITERS = new LinkedHashMap<>();
        COMMON_DELIMITERS.put(COMMA, ",");
        COMMON_DELIMITERS.put(TAB, "\t");
        COMMON_DELIMITERS.put(COLON, ":");
        COMMON_DELIMITERS.put(SEMICOLON, ";");
    }

    private DelimiterRequestDialog(String windowTitle, String message, Boolean isImport) {
        super();
        String otherDelimiterOption = "Other";
        String unknownDelimiterOption = "Unknown";

        this.setTitle(windowTitle);
        ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(
                MainWindow.getWindowIcon());
        this.initOwner(MainWindow.getPrimaryStage());

        /*
            CONTENT NODES
         */
        VBox vBox = new VBox(10.0);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_RIGHT);

        Label requestLabel = new Label(message);
        ChoiceBox<String> delimiterChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(COMMON_DELIMITERS
                                                                                                         .keySet()));
        delimiterChoiceBox.getItems().addAll(otherDelimiterOption, unknownDelimiterOption);

        Label otherLabel = new Label("Other: ");
        TextField otherTextField = new TextField();
        otherTextField.setDisable(true);

        delimiterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(otherDelimiterOption)) {
                otherTextField.setDisable(false);
            } else {
                otherTextField.setDisable(true);
            }
        });

        grid.add(requestLabel, 0, 0);
        grid.add(delimiterChoiceBox, 1, 0);
        grid.add(otherLabel, 0, 1);
        grid.add(otherTextField, 1, 1);

        GridPane.setMargin(delimiterChoiceBox, new Insets(5.0, 5.0, 5.0, 5.0));

        vBox.getChildren().add(grid);

        if (isImport) {
            Label adviceLabel = new Label("*(If copying from spreadsheet, select \"Tabs\".)");
            adviceLabel.setTextFill(Color.DARKRED);

            vBox.getChildren().add(adviceLabel);
        }

        this.getDialogPane().setContent(vBox);

        /*
            BUTTONS AND RETURN
         */
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        this.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        delimiterChoiceBox.getSelectionModel().selectedItemProperty().addListener(c -> {
            if (delimiterChoiceBox.getSelectionModel().getSelectedItem() == null) {
                this.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            } else {
                this.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
            }
        });

        this.setResultConverter(value -> {
            if (value == ButtonType.OK) {
                if (delimiterChoiceBox.getSelectionModel().getSelectedItem().equals(otherDelimiterOption)) {
                    return otherTextField.getText().trim();
                } else if (delimiterChoiceBox.getSelectionModel().getSelectedItem().equals(unknownDelimiterOption)) {
                    return null;
                } else {
                    return COMMON_DELIMITERS.get(delimiterChoiceBox.getValue());
                }
            } else {
                return null;
            }
        });
    }

    /**
     * Presents the user with a {@code Dialog} requesting them for the delimiter of some delimited data.
     *
     * @param windowTitle   String title of the Dialog
     * @param message   String informational message or request
     * @param isImport  Boolean value, true if importing data
     * @return  String delimiter
     */
    public static String showDialog(String windowTitle, String message, Boolean isImport) {
        return new DelimiterRequestDialog(windowTitle, message, isImport).showAndWait().orElse(null);
    }

}
