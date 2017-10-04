package org.cirdles.topsoil.app.util.dialog.controller;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.table.TopsoilDataColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jake Marotta
 */
public class VariableChooserController {

    @FXML VBox columnVBox;
    @FXML ListView<Label> variableListView;

    private Map<Variable<Number>, Label> variableLabelMap;
    private List<Variable<Number>> required;

    private MapProperty<Variable<Number>, TopsoilDataColumn> selections;
    public MapProperty<Variable<Number>, TopsoilDataColumn> selectionsProperty() {
        if (selections == null) {
            selections = new SimpleMapProperty<>(FXCollections.observableHashMap());
        }
        return selections;
    }
    public Map<Variable<Number>, TopsoilDataColumn> getSelections() {
        return selections.get();
    }

    private static final String UNASSIGNED = "   ";    // Three spaces
    private static final BiMap<String, Variable<Number>> STRING_VARIABLE_BIMAP;
    static {
        STRING_VARIABLE_BIMAP = HashBiMap.create();
        for (Variable<Number> v : Variables.VARIABLE_LIST) {
            STRING_VARIABLE_BIMAP.put(v.getName(), v);
        }
    }
    
    @FXML
    public void initialize() {
        required = new ArrayList<>();

        variableLabelMap = new HashMap<>();
        for (Variable<Number> variable : Variables.VARIABLE_LIST) {
            Label variableLabel = new Label(variable.getName().toUpperCase() + ": ");

            variableListView.getItems().add(variableLabel);
            variableLabelMap.put(variable, variableLabel);
        }

        variableListView.getFixedCellSize();

        selectionsProperty();
        selections.addListener((MapChangeListener<? super Variable<Number>, ? super TopsoilDataColumn>) c -> {
            if (c.wasAdded()) {
                variableLabelMap.get(c.getKey()).setText(c.getKey().getName().toUpperCase() + ": " + c.getValueAdded().getName());
                if (required.contains(c.getKey())) {
                    variableLabelMap.get(c.getKey()).setStyle("-fx-text-fill: black");
                }
            } else if (c.wasRemoved()) {
                variableLabelMap.get(c.getKey()).setText(c.getKey().getName().toUpperCase() + ": ");
                if (required.contains(c.getKey())) {
                    variableLabelMap.get(c.getKey()).setStyle("-fx-text-fill: red");
                }
            }
        });
    }

    public void setup(List<TopsoilDataColumn> columns, Map<Variable<Number>, TopsoilDataColumn> currentSelections,
                      List<Variable<Number>> requiredVariables) {

        // Set data columns
        for (TopsoilDataColumn column : columns) {
            ColumnOption option = new ColumnOption(column);
            columnVBox.getChildren().add(option);

            option.getChoiceBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(UNASSIGNED)) {
                    checkOtherChoiceBoxes(option.getChoiceBox(), newValue);
                }
                updateSelections();
            });
        }

        // Assign variable-column selections, replacing former selections
        for (Map.Entry<Variable<Number>, TopsoilDataColumn> entry : currentSelections.entrySet()) {

            ColumnOption option = null;
            for (Node n : columnVBox.getChildren()) {
                if (((ColumnOption) n).getDataColumn() == entry.getValue()) {
                    option = (ColumnOption) n;
                    break;
                }
            }
            if (option != null) {
                option.getChoiceBox().getSelectionModel().select(STRING_VARIABLE_BIMAP.inverse().get(entry.getKey()));
            }
        }
        updateSelections();

        if (requiredVariables != null) {
            this.required = requiredVariables;
            // Highlight variables that are required
            for (Variable<Number> variable : required) {
                if (!selections.containsKey(variable)) {
                    variableLabelMap.get(variable).setStyle("-fx-text-fill: red");
                }
            }
        } else {
            this.required = new ArrayList<>();
        }
    }

    private void updateSelections() {
        selections.get().clear();

        ChoiceBox<String> choiceBox;
        for (Node n : columnVBox.getChildren()) {
            choiceBox = ((ColumnOption) n).getChoiceBox();
            if (!choiceBox.getSelectionModel().getSelectedItem().equals(UNASSIGNED)) {
                selections.get().put(STRING_VARIABLE_BIMAP.get(choiceBox.getSelectionModel().getSelectedItem()),
                               ((ColumnOption) n).getDataColumn());
            }
        }
    }

    /**
     * Checks all {@code ChoiceBox}es other than the one that triggered this method to see if they contain the value
     * that the recently changed {@code ChoiceBox} now contains. If another {@code ChoiceBox} with the same value is
     * found, that {@code ChoiceBox}'s value is set to the empty option.
     *
     * @param changed   the ChoiceBox that was changed
     * @param value the Variable value of the ChoiceBox's new selection
     */
    private void checkOtherChoiceBoxes(ChoiceBox changed, String value) {
        ChoiceBox<String> cb;
        for (Node n : columnVBox.getChildren()) {
            cb = ((ColumnOption) n).getChoiceBox();
            if (cb != changed) {
                if (cb.getValue().equals(value)) {
                    cb.getSelectionModel().select(UNASSIGNED);
                }
            }
        }
    }

    private static class ColumnOption extends HBox {

        private Label columnLabel;
        private ChoiceBox<String> choiceBox;
        private TopsoilDataColumn column;

        private ColumnOption(TopsoilDataColumn column) {
            super();
            this.setAlignment(Pos.CENTER_RIGHT);
            this.setPadding(new Insets(5.0, 10.0, 5.0, 5.0));
            this.setSpacing(5.0);

            this.columnLabel = new Label(column.getName());

            this.column = column;

            this.choiceBox = new ChoiceBox<>();
            choiceBox.getItems().add(UNASSIGNED);

            for (Variable v : Variables.VARIABLE_LIST) {
                choiceBox.getItems().add(v.getName());
            }
            choiceBox.getSelectionModel().select(UNASSIGNED);

            this.getChildren().addAll(columnLabel, choiceBox);
        }

        private ChoiceBox<String> getChoiceBox() {
            return choiceBox;
        }

        private TopsoilDataColumn getDataColumn() {
            return column;
        }
    }

}
