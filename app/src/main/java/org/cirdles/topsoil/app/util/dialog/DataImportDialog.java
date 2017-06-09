package org.cirdles.topsoil.app.util.dialog;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.dialog.DataImportDialog.DataImportKey;

import javax.annotation.Nullable;
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
public class DataImportDialog extends Dialog<Map<DataImportKey, Object>> {

    public enum DataImportKey {
        HEADERS("Headers"),
        DATA("Data"),
        UNCERTAINTY("UNCERTAINTY");

        private String key;

        DataImportKey(String key) {
            this.key = key;
        }
    }

    private static final Map<String, Variable<Number>> STRING_VARIABLE_MAP;
    private static final Map<String, UncertaintyFormat> STRING_UNCERTAINTY_FORMAT_MAP;
    static {
        STRING_VARIABLE_MAP = new LinkedHashMap<>();
        for (Variable<Number> v : Variables.VARIABLE_LIST) {
            STRING_VARIABLE_MAP.put(v.getName(), v);
        }

        STRING_UNCERTAINTY_FORMAT_MAP = new LinkedHashMap<>();
        for (UncertaintyFormat uf : UncertaintyFormat.ALL) {
            STRING_UNCERTAINTY_FORMAT_MAP.put(uf.getName(), uf);
        }
    }

    private ArrayList<ChoiceBox<String>> columnChoiceBoxes;
    private ChoiceBox<String> uncertaintyChoiceBox;

    private DataImportDialog(String[] headers, List<TopsoilDataEntry> data) {
        super();
        this.columnChoiceBoxes = new ArrayList<>();
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.FINISH);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initOwner(MainWindow.getPrimaryStage());
        stage.getIcons().add(MainWindow.getWindowIcon());

        constructContent(headers, data);

        // User can't click "Finish" until they select an uncertainty format.
        this.getDialogPane().lookupButton(ButtonType.FINISH).setDisable(true);
        uncertaintyChoiceBox.valueProperty().addListener(c -> {
            if (uncertaintyChoiceBox.getValue() != null) {
                this.getDialogPane().lookupButton(ButtonType.FINISH).setDisable(false);

                // This should never happen after an initial selection, but just in case.
            } else {
                this.getDialogPane().lookupButton(ButtonType.FINISH).setDisable(true);
            }
        });

        Map<Variable<Number>, Integer> variableIndexMap = new LinkedHashMap<>(Variables.VARIABLE_LIST.size());

        setResultConverter(value -> {
            if (value == ButtonType.FINISH) {
                for (int i = 0; i < columnChoiceBoxes.size(); i++) {
                    Variable<Number> variable = STRING_VARIABLE_MAP.get(columnChoiceBoxes.get(i).getValue());
                    if (variable != null) {
                        variableIndexMap.put(variable, i);
                    }
                }

                boolean selectedX = variableIndexMap.containsKey(Variables.X);
                boolean selectedY = variableIndexMap.containsKey(Variables.Y);
                boolean selectedSX = variableIndexMap.containsKey(Variables.SIGMA_X);
                boolean selectedSY = variableIndexMap.containsKey(Variables.SIGMA_Y);
                boolean selectedR = variableIndexMap.containsKey(Variables.RHO);

                String[] selectedHeaders = new String[]{
                        selectedX ? headers[variableIndexMap.get(Variables.X)] : "X Column",
                        selectedY ? headers[variableIndexMap.get(Variables.Y)] : "Y Column",
                        selectedSX ? headers[variableIndexMap.get(Variables.SIGMA_X)] : "σX Column",
                        selectedSY ? headers[variableIndexMap.get(Variables.SIGMA_Y)] : "σY Column",
                        selectedR ? headers[variableIndexMap.get(Variables.RHO)] : "corr coef Column"
                };

                List<TopsoilDataEntry> selectedColumns = new ArrayList<>(data.size());

                // Construct new set of entries based on the selected columns.
                for (int i = 0; i < data.size(); i++) {

                    // Add a TopsoilDataEntry to selection if it doesn't have one at this index.
                    if (selectedColumns.size() <= i) {
                        while(selectedColumns.size() <= i) {
                            selectedColumns.add(new TopsoilDataEntry());
                        }
                    }

                    DoubleProperty xValue = selectedX ? data.get(i).getProperties().get(variableIndexMap.get(Variables.X))
                            : new SimpleDoubleProperty(0.0);
                    selectedColumns.get(i).add(xValue);

                    DoubleProperty yValue = selectedY ? data.get(i).getProperties().get(variableIndexMap.get(Variables.Y))
                            : new SimpleDoubleProperty(0.0);
                    selectedColumns.get(i).add(yValue);

                    DoubleProperty sxValue = selectedSX ? data.get(i).getProperties().get(variableIndexMap.get(Variables.SIGMA_X))
                            : new SimpleDoubleProperty(0.0);
                    selectedColumns.get(i).add(sxValue);

                    DoubleProperty syValue = selectedSY ? data.get(i).getProperties().get(variableIndexMap.get(Variables.SIGMA_Y))
                            : new SimpleDoubleProperty(0.0);
                    selectedColumns.get(i).add(syValue);

                    DoubleProperty rValue = selectedR ? data.get(i).getProperties().get(variableIndexMap.get(Variables.RHO))
                            : new SimpleDoubleProperty(0.0);
                    selectedColumns.get(i).add(rValue);
                }

                Map<DataImportKey, Object> selections = new HashMap<>();
                selections.put(DataImportKey.HEADERS, selectedHeaders);
                selections.put(DataImportKey.DATA, selectedColumns);
                selections.put(DataImportKey.UNCERTAINTY, STRING_UNCERTAINTY_FORMAT_MAP.get(uncertaintyChoiceBox.getSelectionModel().getSelectedItem()));

                return selections;
            } else {
                return null;
            }
        });

    }

    /**
     * Shows a {@code Dialog} where the user can preview how their data will be imported, and assign plotting
     * variables to the columns that they wish to keep. This method returns several values as a {@code Map}, which
     * can be retrieved by their {@code DataImportKey}s.
     * <p>
     * For reference:
     *
     * DataImportKey.HEADERS = the {@code String} headers of the selected columns
     * DataImportKey.DATA = the {@code {@literal List<TopsoilDataEntry>}} containing rows with the values for the
     * selected columns
     * DataImportKey.UNCERTAINTY = the selected {@code UncertaintyFormat}
     *
     * @param headers   array of String column headers
     * @param data  List of TopsoilDataEntry rows
     * @return  a Map of values
     */
    public static Map<DataImportKey, Object> showImportDialog(@Nullable String[] headers, List<TopsoilDataEntry> data) {

        if (headers == null) {
            int maxRowSize = 0;
            for (TopsoilDataEntry row : data) {
                maxRowSize = Math.max(maxRowSize, row.getProperties().size());
            }
            headers = new String[maxRowSize];
            for (int i = 1; i <= headers.length; i++) {
                headers[i - 1] = "Column " + i;
            }
        }

        DataImportDialog dialog = new DataImportDialog(headers, data);
        dialog.setTitle("Data Import Helper");

        return dialog.showAndWait().orElse(null);
    }

    /**
     * Constructs the nodes for this {@code Dialog}'s content, with respect to the supplied headers and data. The
     * headers and data are used to populate a preview of the imported data.
     *
     * @param headers   the headers of the data
     * @param data  the imported data
     */
    private void constructContent(String[] headers, List<TopsoilDataEntry> data) {

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20.0, 20.0, 20.0, 20.0));

        Label descLabel = new Label("Below is a preview of your imported data:");
        container.getChildren().add(descLabel);
        VBox.setMargin(descLabel, new Insets(10.0, 10.0, 10.0, 10.0));

        // Sample is <= 5
        List<TopsoilDataEntry> sampleLines = data.subList(0, Math.min(data.size() - 1, 5));

        // The GridPane holds the data preview as well as a set of ChoiceBoxes for each column
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setMinSize(110.0 * headers.length, 202.0);
        grid.setMaxSize(125.0 * headers.length, 250.0);

        // Create each column of the data preview.
        Label label;
        for (int i = 0; i < headers.length; i++) {

            // ChoiceBox for the user to select which variable the column represents.
            ChoiceBox<String> choiceBox = new ChoiceBox<>();
            choiceBox.getItems().add("  ");
            choiceBox.getItems().addAll(STRING_VARIABLE_MAP.keySet());
            choiceBox.getSelectionModel().select("  ");
            choiceBox.setMinSize(100.0, 30.0);
            choiceBox.setMaxSize(100.0, 30.0);
            choiceBox.valueProperty().addListener(c -> {
                if (!choiceBox.getValue().equals("  ")) {
                    checkOtherChoiceBoxes(choiceBox,
                                          STRING_VARIABLE_MAP.get(choiceBox.getSelectionModel().getSelectedItem()));
                }
            });
            grid.add(choiceBox, i, 0);
            GridPane.setConstraints(choiceBox, i, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority
                    .NEVER, new Insets(5.0, 5.0, 5.0, 5.0));
            this.columnChoiceBoxes.add(choiceBox);

            label = new Label(headers[i]);
            label.setStyle("-fx-font-weight: bold");
            label.setMinSize(100.0, 17.0);
            label.setMaxSize(100.0, 17.0);
            grid.add(label, i, 1);
            GridPane.setConstraints(label, i, 1, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER, new Insets(5.0, 5.0, 5.0, 5.0));

            for (int j = 0; j < sampleLines.size(); j++) {
                label = new Label(sampleLines.get(j).getProperties().get(i).getValue().toString());
                label.setFont(Font.font("Monospaced"));
                label.setMinSize(100.0, 17.0);
                label.setMaxSize(100.0, 17.0);
                grid.add(label, i, j + 2);
                GridPane.setConstraints(label, i, j + 2, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority
                        .NEVER, new Insets(5.0, 5.0, 5.0, 5.0));
            }
        }

        for (int i = 0; i < Math.min(columnChoiceBoxes.size(), 5); i++) {
            columnChoiceBoxes.get(i).setValue(Variables.VARIABLE_LIST.get(i).getName());
        }

        // The AnchorPane forces the GridPane to fill the ScrollPane
        AnchorPane anchor = new AnchorPane(grid);
        AnchorPane.setLeftAnchor(grid, 0.0);
        AnchorPane.setRightAnchor(grid, 0.0);
        AnchorPane.setTopAnchor(grid, 0.0);
        AnchorPane.setBottomAnchor(grid, 0.0);

        ScrollPane scrollPane = new ScrollPane(anchor);
        scrollPane.setMaxWidth(500.0);                                  // constrict width
        scrollPane.setFitToHeight(true);                                // fit to height of AnchorPane
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);    // always show horizontal ScrollBar
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);     // never show vertical ScrollBar
        container.getChildren().add(scrollPane);

        // Creates a sub-container for other nodes. Probably unnecessary, but I think it looks nice. Change if you want.
        VBox subContainer = new VBox();
        subContainer.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        container.getChildren().add(subContainer);

        Label instrLabel = new Label("Using the drop-down lists, select which of your columns correspond to the\n" +
                                     "following five plotting variables: X values, Y values, X uncertainty values,\n" +
                                     "Y uncertatinty values, rho (correlation coefficient) values.");
        subContainer.getChildren().add(instrLabel);
        Label noteLabel = new Label("**NOTE: Only the columns with associated variables will be imported.");
        noteLabel.setStyle("-fx-text-fill: red");
        subContainer.getChildren().add(noteLabel);

        // Creates a horizontal layout for the uncertainty option.
        HBox uncContainer = new HBox();
        uncContainer.setAlignment(Pos.CENTER_LEFT);

        Label uncLabel = new Label("What format are your uncertainty values in?");
        uncContainer.getChildren().add(uncLabel);

        ChoiceBox<String> uncChoiceBox = new ChoiceBox<>();
        uncChoiceBox.getItems().addAll(STRING_UNCERTAINTY_FORMAT_MAP.keySet());
        uncContainer.getChildren().add(uncChoiceBox);
        HBox.setMargin(uncChoiceBox, new Insets(5.0, 5.0, 5.0, 5.0));
        this.uncertaintyChoiceBox = uncChoiceBox;

        subContainer.getChildren().add(uncContainer);

        this.getDialogPane().setContent(container);
    }

    /**
     * Checks all {@code ChoiceBox}es other than the one that triggered this method to see if they contain the value
     * that the recently changed {@code ChoiceBox} now contains. If another {@code ChoiceBox} with the same value is
     * found, that {@code ChoiceBox}'s value is set to the empty option.
     *
     * @param changed   the ChoiceBox that was changed
     * @param value the Variable value of the ChoiceBox's new selection
     */
    private void checkOtherChoiceBoxes(ChoiceBox changed, Variable<Number> value) {
        for (ChoiceBox<String> cb : columnChoiceBoxes) {
            if (cb != changed) {
                if (STRING_VARIABLE_MAP.get(cb.getValue()) == value) {
                    cb.getSelectionModel().select("  ");
                }
            }
        }
    }
}


