package org.cirdles.topsoil.app.control.dialog;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom control for selecting associations between {@link Variable}s and {@link DataColumn}s.
 */
public class VariableChooser extends HBox {

    private static final String CONTROLLER_FXML = "variable-chooser.fxml";
    private static final double COL_WIDTH = 100.0;
    private static final double ROW_HEIGHT = 30.0;
    private static final double HEADER_ROW_HEIGHT = 25.01;  // .01 prevents tableView's vertical scrollbar from showing

    @FXML private VBox variableLabelBox;
    @FXML private TableView<VariableRow<?>> tableView;
    @FXML private ScrollPane scrollPane;

    private DataTable table;

    public VariableChooser(DataTable table) {
        this.table = table;
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, VariableChooser.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        // Add left-side variable labels
        Label label;
        for (Variable<?> variable : Variables.NUMBER_TYPE) {
            label = new Label(variable.getName());
            label.setMinHeight(ROW_HEIGHT);
            label.setMaxHeight(ROW_HEIGHT);
            variableLabelBox.getChildren().add(label);
        }

        tableView.setEditable(true);
        tableView.setItems(makeTableRows(table));
        tableView.getColumns().addAll(makeTableColumns(table.getColumnRoot()));

        int colDepth = table.getColumnRoot().getDepth();

        // Forces the TableView to resize based on the number of rows/columns
        tableView.setFixedCellSize(ROW_HEIGHT);
        tableView.prefHeightProperty().bind(
                tableView.fixedCellSizeProperty()
                        .multiply(Bindings.size(tableView.getItems()))
                        .add(colDepth * (HEADER_ROW_HEIGHT + 1))
        );
        tableView.prefWidthProperty().bind(
                Bindings.createDoubleBinding(() -> {
                    int colCount = tableView.getVisibleLeafColumns().size();
                    if (colCount == 0) {
                        return 400.0;
                    } else {
                        return (COL_WIDTH * colCount) + 2;
                    }
                }, tableView.getVisibleLeafColumns())
        );

        // Prevents the user from re-ordering columns.
        tableView.widthProperty().addListener(((observable, oldValue, newValue) -> {
            TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener(obs -> {
                header.setReordering(false);
            });
        }));

        // Restricts the table size if there are a lot of columns
        if (tableView.getVisibleLeafColumns().size() > 5) {
            this.setPrefWidth(5 * (COL_WIDTH + 1));
        }

        // Prevents mouse-wheel scrolling
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            event.consume();
        });
    }

    public Map<Variable<?>, DataColumn<?>> getSelections() {
        Map<Variable<?>, DataColumn<?>> selections = new HashMap<>();
        for (VariableRow<?> row : tableView.getItems()) {
            for (Map.Entry<DataColumn<?>, BooleanProperty> entry : row.entrySet()) {
                if (entry.getValue().get()) {
                    selections.put(row.getVariable(), entry.getKey());
                    break;
                }
            }
        }
        return selections;
    }

    public List<TableColumn<VariableRow<?>,?>> getLeafTableColumns() {
        List<TableColumn<VariableRow<?>,?>> columns = new ArrayList<>();
        for (TableColumn<VariableRow<?>, ?> column : tableView.getColumns()) {
            columns.addAll(getLeafTableColumns(column));
        }
        return columns;
    }

    private List<TableColumn<VariableRow<?>,?>> getLeafTableColumns(TableColumn<VariableRow<?>,?> parent) {
        List<TableColumn<VariableRow<?>,?>> columns = new ArrayList<>();
        if (parent.getColumns().size() == 0) {
            columns.add(parent);
        } else {
            for (TableColumn<VariableRow<?>,?> child : parent.getColumns()) {
                columns.addAll(getLeafTableColumns(child));
            }
        }
        return columns;
    }

    private ObservableList<VariableRow<?>> makeTableRows(DataTable table) {
        ObservableList<VariableRow<?>> rows = FXCollections.observableArrayList();
        VariableRow<?> row;
        for (Variable<?> variable : Variables.NUMBER_TYPE) {
            row = new VariableRow<>(variable);
            for (DataColumn<?> column : table.getDataColumns()) {
                BooleanProperty property = new SimpleBooleanProperty(table.getColumnForVariable(variable) == column);
                property.addListener(((observable, oldValue, newValue) -> {
                    if (newValue) {
                        deselectOthers(column, variable);
                    }
                }));
                row.put(column, property);
            }
            rows.add(row);
        }
        return rows;
    }

    private void deselectOthers(DataColumn<?> column, Variable<?> variable) {
        for (VariableRow<?> row : tableView.getItems()) {
            if (row.getVariable() == variable) {
                for (Map.Entry<DataColumn<?>, BooleanProperty> entry : row.entrySet()) {
                    if (entry.getKey() != column) {
                        entry.getValue().set(false);
                    }
                }
            } else {
                row.get(column).set(false);
            }
        }
    }

    private ObservableList<TableColumn<VariableRow<?>, Boolean>> makeTableColumns(DataComposite<DataComponent> composite) {
        ObservableList<TableColumn<VariableRow<?>, Boolean>> columns = FXCollections.observableArrayList();
        for (DataComponent node : composite.getChildren()) {
            TableColumn<VariableRow<?>, Boolean> newColumn;
            if (node instanceof DataColumn) {
                newColumn = makeTableColumn((DataColumn<?>) node);
            } else {
                newColumn = makeTableColumn((DataCategory) node);
            }
            columns.add(newColumn);
        }
        return columns;
    }

    private TableColumn<VariableRow<?>, Boolean> makeTableColumn(DataColumn<?> dataColumn) {
        TableColumn<VariableRow<?>, Boolean> newColumn = new TableColumn<>(dataColumn.getLabel());
        newColumn.setCellFactory(CheckBoxTableCell.forTableColumn(param -> tableView.getItems().get(param).get(dataColumn)));
        newColumn.setCellValueFactory(param -> param.getValue().get(dataColumn));
        newColumn.setVisible(dataColumn.isSelected());
        newColumn.setPrefWidth(COL_WIDTH);
        newColumn.setEditable(true);
        newColumn.setResizable(false);
        newColumn.setSortable(false);
        return newColumn;
    }

    private TableColumn<VariableRow<?>, Boolean> makeTableColumn(DataCategory dataCategory) {
        TableColumn<VariableRow<?>, Boolean> newColumn = new TableColumn<>(dataCategory.getLabel());
        newColumn.getColumns().addAll(makeTableColumns(dataCategory));
        return newColumn;
    }

    private static class VariableRow<T> extends HashMap<DataColumn<?>, BooleanProperty> {

        private Variable<T> variable;

        VariableRow(Variable<T> variable) {
            super();
            this.variable = variable;
        }

        public Variable<?> getVariable() {
            return variable;
        }

    }

}
