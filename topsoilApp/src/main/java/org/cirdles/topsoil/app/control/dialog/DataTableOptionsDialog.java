package org.cirdles.topsoil.app.control.dialog;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.tree.ColumnTreeView;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.util.ResourceBundles;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author marottajb
 */
public class DataTableOptionsDialog extends Dialog<Boolean> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final double INIT_WIDTH = 900.0;
    private static final double INIT_HEIGHT = 400.0;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataTableOptionsDialog(DataTable table, Stage owner) {
        this.setTitle(ResourceBundles.DIALOGS.getString("optionsTitle") + " " + table.getLabel());
        this.initOwner(owner);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().addAll(Topsoil.getLogo());
        stage.setOnShown(event -> stage.requestFocus());

        DataTableOptionsView controller = new DataTableOptionsView(table);
        this.getDialogPane().setContent(controller);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        this.setResultConverter(value -> {
            if (value == ButtonType.OK) {
                for (Map.Entry<DataComponent, Boolean> entry : controller.getColumnSelections().entrySet()) {
                    entry.getKey().setSelected(entry.getValue());
                }
                table.setColumnsForAllVariables(controller.getVariableAssignments());
                table.setIsotopeSystem(controller.getIsotopeSystem());
                table.setUncertainty(controller.getUncertainty());
                return true;
            }
            return false;
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Displays a dialog with controls for the user to modify current data table settings.
     *
     * @param table     DataTable
     * @param owner     Stage owner of this dialog
     *
     * @return          true if changes saved
     */
    public static Boolean showDialog(DataTable table, Stage owner) {
        return new DataTableOptionsDialog(table, owner).showAndWait().orElse(null);
    }

    /**
     * Controller for a screen that allows the user to preview their imported model, as well as choose an {@link
     * Uncertainty} and {@link IsotopeSystem} for each table.
     *
     * @author marottajb
     */
    public static class DataTableOptionsView extends GridPane {

        //**********************************************//
        //                  CONSTANTS                   //
        //**********************************************//

        private static final String CONTROLLER_FXML = "data-table-options.fxml";

        //**********************************************//
        //                   CONTROLS                   //
        //**********************************************//

        @FXML private Label columnTreeViewLabel;
        @FXML private VBox columnTreeViewPane;
        ColumnTreeView columnTreeView;

        @FXML private Label variableChooserLabel;
        @FXML private VBox variableChooserPane;
        VariableChooser variableChooser;

        @FXML private Label uncertaintyLabel, isotopeSystemLabel;
        @FXML ComboBox<Uncertainty> unctComboBox;
        @FXML ComboBox<IsotopeSystem> isoComboBox;

        //**********************************************//
        //                  ATTRIBUTES                  //
        //**********************************************//

        private DataTable table;

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        public DataTableOptionsView(DataTable table) {
            super();
            this.table = table;
            try {
                FXMLUtils.loadController(CONTROLLER_FXML, DataTableOptionsView.class, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @FXML
        protected void initialize() {
            ResourceBundle resources = ResourceBundles.DIALOGS.getBundle();
            columnTreeViewLabel.setText(resources.getString("columnTreeLabel"));
            variableChooserLabel.setText(resources.getString("variableChooserLabel"));
            uncertaintyLabel.setText(resources.getString("uncertaintyLabel"));
            isotopeSystemLabel.setText(resources.getString("isotopeSystemLabel"));

            this.columnTreeView = new ColumnTreeView(table.getColumnRoot());
            columnTreeViewPane.getChildren().add(columnTreeView);

            this.variableChooser = new VariableChooser(table);
            variableChooserPane.getChildren().add(variableChooser);

            listenToTreeItemChildren(columnTreeView.getRoot(), variableChooser);

            unctComboBox.getItems().addAll(Uncertainty.values());
            unctComboBox.getSelectionModel().select(table.getUncertainty());
            isoComboBox.getItems().addAll(IsotopeSystem.values());
            isoComboBox.getSelectionModel().select(table.getIsotopeSystem());
        }

        //**********************************************//
        //                PUBLIC METHODS                //
        //**********************************************//

        /**
         * Returns the column visibility selections for the table.
         *
         * @return  Map of DataComponent to Boolean values, true if column should be visible
         */
        public Map<DataComponent, Boolean> getColumnSelections() {
            return columnTreeView.getColumnSelections();
        }

        /**
         * Returns the variable/column associations for the table.
         *
         * @return  Map of Variable to DataColumn
         */
        public Map<Variable<?>, DataColumn<?>> getVariableAssignments() {
            return variableChooser.getSelections();
        }

        public IsotopeSystem getIsotopeSystem() {
            return isoComboBox.getValue();
        }

        public Uncertainty getUncertainty() {
            return unctComboBox.getValue();
        }

        //**********************************************//
        //                PRIVATE METHODS               //
        //**********************************************//

        private void listenToTreeItemChildren(TreeItem<DataComponent> parent, VariableChooser chooser) {
            for (TreeItem<DataComponent> treeItem : parent.getChildren()) {
                CheckBoxTreeItem<DataComponent> cBTreeItem = (CheckBoxTreeItem<DataComponent>) treeItem;
                cBTreeItem.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), event -> {
                    if (cBTreeItem.getValue() instanceof DataColumn) {
                        chooser.tableColumnMap.get(cBTreeItem.getValue()).setVisible(cBTreeItem.isSelected());
                    }
                });
                if (cBTreeItem.getValue() instanceof DataCategory) {
                    listenToTreeItemChildren(cBTreeItem, chooser);
                }
            }
        }

    }

    /**
     * A custom control for selecting associations between {@link Variable}s and {@link DataColumn}s.
     */
    public static class VariableChooser extends HBox {

        private static final String CONTROLLER_FXML = "variable-chooser.fxml";
        private static final double COL_WIDTH = 100.0;
        private static final double ROW_HEIGHT = 30.0;
        private static final double HEADER_ROW_HEIGHT = 25.01;  // .01 prevents tableView's vertical scrollbar from showing

        @FXML VBox variableLabelBox;
        @FXML TableView<VariableRow<?>> tableView;
        @FXML ScrollPane scrollPane;

        private DataTable table;
        private Map<DataComponent, TableColumn<VariableRow<?>, ?>> tableColumnMap = new HashMap<>();

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
            Label label;
            for (Variable<?> variable : Variables.ALL) {
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
                            .add(colDepth * HEADER_ROW_HEIGHT)
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

            if (tableView.getVisibleLeafColumns().size() > 5) {
                this.setPrefWidth((COL_WIDTH * 5));
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

        private ObservableList<VariableRow<?>> makeTableRows(DataTable table) {
            ObservableList<VariableRow<?>> rows = FXCollections.observableArrayList();
            VariableRow<?> row;
            for (Variable<?> variable : Variables.ALL) {
                row = new VariableRow<>(variable);
                for (DataColumn<?> column : table.getDataColumns()) {
                    BooleanProperty property = new SimpleBooleanProperty(table.getVariableColumnMap().get(variable) == column);
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
            newColumn.setVisible(dataColumn.isSelected());
            newColumn.setPrefWidth(COL_WIDTH);
            newColumn.setResizable(false);
            newColumn.setEditable(true);
            newColumn.setSortable(false);
            tableColumnMap.put(dataColumn, newColumn);
            return newColumn;
        }

        private TableColumn<VariableRow<?>, Boolean> makeTableColumn(DataCategory dataCategory) {
            TableColumn<VariableRow<?>, Boolean> newColumn = new TableColumn<>(dataCategory.getLabel());
            newColumn.getColumns().addAll(makeTableColumns(dataCategory));
            newColumn.setVisible(dataCategory.isSelected());
            tableColumnMap.put(dataCategory, newColumn);
            return newColumn;
        }

        static class VariableRow<T> extends HashMap<DataColumn<?>, BooleanProperty> {

            Variable<T> variable;

            VariableRow(Variable<T> variable) {
                super();
                this.variable = variable;
            }

            public Variable<?> getVariable() {
                return variable;
            }

        }

    }
}
