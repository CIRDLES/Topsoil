package org.cirdles.topsoil.app.tab;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.plot.VariableFormat;
import org.cirdles.topsoil.app.plot.VariableFormats;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.table.command.DeleteRowCommand;
import org.cirdles.topsoil.app.table.command.InsertRowCommand;
import org.cirdles.topsoil.app.table.command.NewRowCommand;
import org.cirdles.topsoil.app.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTable;

import java.util.*;

/**
 * A custom <tt>Tab</tt> which displays data from a <tt>TopsoilTable</tt>.
 *
 * @author sbunce
 * @see Tab
 * @see TopsoilTabPane
 * @see TopsoilTable
 */
public class TopsoilTabContent extends SplitPane {

    @FXML private GridPane labelGridPane;
    @FXML private Label xLabel, yLabel, xUncertaintyLabel, yUncertaintyLabel, corrCoefLabel;
    @FXML private ChoiceBox<String> xUncertaintyChoiceBox, yUncertaintyChoiceBox;

    @FXML private TableView<TopsoilDataEntry> tableView;
    private ObservableList<TopsoilDataEntry> data;

    @FXML private Button addRowButton;
    @FXML private Button removeRowButton;

    // TODO Supply new plot PropertiesPanel
    @FXML private ScrollPane plotPropertiesScrollPane;

    private static Map<String, VariableFormat<Number>> STRING_TO_VARIABLE_FORMAT_MAP;
    private static Map<VariableFormat<Number>, String> VARIABLE_FORMAT_TO_STRING_MAP;
    static {
        // Map VariableFormat names to the formats for displaying and selecting from the uncertainty ChoiceBoxes.
        STRING_TO_VARIABLE_FORMAT_MAP = new LinkedHashMap<>();
        VARIABLE_FORMAT_TO_STRING_MAP = new LinkedHashMap<>();
        for (VariableFormat<Number> format : VariableFormats.UNCERTAINTY_FORMATS) {
            STRING_TO_VARIABLE_FORMAT_MAP.put(format.getName(), format);
            VARIABLE_FORMAT_TO_STRING_MAP.put(format, format.getName());
        }
    }
    private ResourceExtractor resourceExtractor = new ResourceExtractor(TopsoilTabContent.class);

    @FXML
    public void initialize() {
        assert labelGridPane != null : "fx:id=\"labelGridPane\" was not injected: check your FXML file " +
                                       "'topsoil-tab.fxml'.";
        assert xLabel != null : "fx:id=\"xLabel\" was not injected: check your FXML file 'topsoil-tab.fxml'.";
        assert yLabel != null : "fx:id=\"yLabel\" was not injected: check your FXML file 'topsoil-tab.fxml'.";
        assert xUncertaintyLabel != null : "fx:id=\"xUncertaintyLabel\" was not injected: check your FXML file " +
                                           "'topsoil-tab.fxml'.";
        assert yUncertaintyLabel != null : "fx:id=\"yUncertaintyLabel\" was not injected: check your FXML file " +
                                           "'topsoil-tab.fxml'.";
        assert corrCoefLabel != null : "fx:id=\"corrCoefLabel\" was not injected: check your FXML file " +
                                       "'topsoil-tab.fxml'.";

        assert xUncertaintyChoiceBox != null : "fx:id=\"xUncertaintyChoiceBox\" was not injected: check your FXML " +
                                               "file 'topsoil-tab.fxml'.";
        assert yUncertaintyChoiceBox != null : "fx:id=\"yUncertaintyChoiceBox\" was not injected: check your FXML " +
                                               "file 'topsoil-tab.fxml'.";

        assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'topsoil-tab.fxml'.";
        assert addRowButton != null : "fx:id=\"addRowButton\" was not injected: check your FXML file 'topsoil-tab.fxml'.";

        // Add VariableFormats to uncertainty ChoiceBoxes and select TWO_SIGMA_PERCENT by default
        xUncertaintyChoiceBox.setItems(FXCollections.observableArrayList(STRING_TO_VARIABLE_FORMAT_MAP.keySet()));
        xUncertaintyChoiceBox.getSelectionModel().select(VariableFormats.TWO_SIGMA_PERCENT.getName());
        yUncertaintyChoiceBox.setItems(FXCollections.observableArrayList(STRING_TO_VARIABLE_FORMAT_MAP.keySet()));
        yUncertaintyChoiceBox.getSelectionModel().select(VariableFormats.TWO_SIGMA_PERCENT.getName());

        // Handle Keyboard Events
        tableView.setOnKeyPressed(keyEvent -> handleTableViewKeyEvent(keyEvent));

        // Set initial state of remove button.

        tableView.itemsProperty().addListener(c -> {
            if (tableView.getItems() != null) {
                tableView.getItems().addListener((ListChangeListener<TopsoilDataEntry>) d -> {
                    if (tableView.getItems().isEmpty()) {
                        removeRowButton.setDisable(true);
                    } else {
                        removeRowButton.setDisable(false);
                    }
                });
            }
        });

        configureColumns();
        resetIds();

    }

    private void handleTableViewKeyEvent(KeyEvent keyEvent) {

        List<TableColumn<TopsoilDataEntry, ?>> columns = tableView.getColumns();
        TableView.TableViewSelectionModel<TopsoilDataEntry> selectionModel = tableView.getSelectionModel();

        // Tab focuses right cell
        // Shift + Tab focuses left cell
        if (keyEvent.getCode().equals(KeyCode.TAB)) {
            if (keyEvent.isShiftDown()) {
                if (selectionModel.getSelectedCells().get(0).getColumn() == 0 &&
                    selectionModel.getSelectedIndex() != 0) {
                    selectionModel.select(selectionModel.getSelectedIndex() - 1, this.tableView.getColumns().get
                            (columns.size() - 1));
                } else {
                    selectionModel.selectLeftCell();
                }
            } else {
                if (selectionModel.getSelectedCells().get(0).getColumn() ==
                    columns.size() - 1 && selectionModel.getSelectedIndex() != tableView.getItems().size() - 1) {
                    selectionModel.select(selectionModel.getSelectedIndex() + 1, this.tableView.getColumns().get(0));
                } else {
                    selectionModel.selectRightCell();
                }
            }

            keyEvent.consume();

            // Enter moves down or creates new empty row
            // Shift + Enter moved up a row
        } else if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            if (keyEvent.isShiftDown()) {
                selectionModel.selectAboveCell();
            } else {
                // if on last row
                if (selectionModel.getSelectedIndex() == tableView.getItems().size() - 1) {
                    NewRowCommand newRowCommand = new NewRowCommand(this.tableView);
                    newRowCommand.execute();
                    ((TopsoilTabPane) this.tableView.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(newRowCommand);
                }
                selectionModel.selectBelowCell();
            }
            keyEvent.consume();
        }
    }

    private void configureColumns() {
        List<TableColumn<TopsoilDataEntry, ?>> columns = tableView.getColumns();

        TableColumn<TopsoilDataEntry, Double> newColumn;

        for (int i = 0; i < columns.size(); i++) {

            final int columnIndex = i;
            newColumn = new TableColumn<>(columns.get(i).getText());

            // override cell value factory to accept the i'th index of a data entry for the i'th column
            newColumn.setCellValueFactory(param -> {
                if (param.getValue().getProperties().size() == 0) {
                    return (ObservableValue) new SimpleDoubleProperty(0.0);
                } else {
                    // If data was incomplete i.e. length of line is too short for number of columns.
                    if (param.getValue().getProperties().size() < columnIndex + 1) {
                        SimpleDoubleProperty newProperty = new SimpleDoubleProperty(Double.NaN);
                        param.getValue().getProperties().add(newProperty);
                        return (ObservableValue) newProperty;
                    } else {
                        return (ObservableValue) param.getValue().getProperties().get(columnIndex);
                    }
                }
            });

            // override cell factory to custom editable cells
            newColumn.setCellFactory(value -> new TopsoilTableCell());

            // disable column sorting
            newColumn.setSortable(false);

            // add functional column to the array of columns
            columns.set(i, newColumn);
        }
    }

    /**
     * get the TableView object from the tab
     * @return tableview
     */
    public TableView<TopsoilDataEntry> getTableView() {
        return tableView;
    }

    public void setData(ObservableList<TopsoilDataEntry> dataEntries) {
        this.data = dataEntries;
        tableView.setItems(data);
    }

    public VariableFormat<Number> getXUncertainty() {
        return STRING_TO_VARIABLE_FORMAT_MAP.get(xUncertaintyChoiceBox.getValue());
    }

    public void setXUncertainty(VariableFormat<Number> format) {
        String o = VARIABLE_FORMAT_TO_STRING_MAP.get(format);
        // Find and select the specific item that matches the format.
        for (String s : xUncertaintyChoiceBox.getItems()) {
            if (s.equals(o)) {
                xUncertaintyChoiceBox.getSelectionModel().select(VARIABLE_FORMAT_TO_STRING_MAP.get(format));
                break;
            }
        }
    }

    public VariableFormat<Number> getYUncertainty() {
        return STRING_TO_VARIABLE_FORMAT_MAP.get(yUncertaintyChoiceBox.getValue());
    }

    public void setYUncertainty(VariableFormat<Number> format) {
        String o = VARIABLE_FORMAT_TO_STRING_MAP.get(format);
        // Find and select the specific item that matches the format.
        for (String s : yUncertaintyChoiceBox.getItems()) {
            if (s.equals(o)) {
                yUncertaintyChoiceBox.getSelectionModel().select(VARIABLE_FORMAT_TO_STRING_MAP.get(format));
                break;
            }
        }
    }

    /**
     * Resets the string ids associated with each <tt>TableColumn</tt> in the <tt>TableView</tt>.
     * <p>Each TableColumn has an associated String id assigned to it, increasing numerically from 1, left to right.
     * This is to keep track of the order of the columns before and after they are re-ordered due to clicking and
     * dragging.
     * </p>
     */
    public void resetIds() {
        int id = 0;
        for (TableColumn<TopsoilDataEntry, ?> column : this.tableView.getColumns()) {
            column.setId(Integer.toString(id));
            id++;
        }
    }

    @FXML private void addRowButtonAction() {
        if (tableView.getItems() != null) {
            InsertRowCommand insertRowCommand = new InsertRowCommand(tableView);
            insertRowCommand.execute();
            ((TopsoilTabPane) this.tableView.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo
                    (insertRowCommand);
        }
    }

    @FXML private void removeRowButtonAction() {
        if (tableView.getItems() != null && !tableView.getItems().isEmpty()) {
            DeleteRowCommand deleteRowCommand = new DeleteRowCommand(tableView);
            deleteRowCommand.execute();
            ((TopsoilTabPane) this.tableView.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo
                    (deleteRowCommand);
        }
    }
}
