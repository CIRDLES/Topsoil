package org.cirdles.topsoil.app.table;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.plot.variable.DependentVariable;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.tab.TopsoilTabContent;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.command.TableColumnReorderCommand;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.dialog.VariableChooserDialog;
import org.cirdles.topsoil.app.util.listener.ListenerHandle;
import org.cirdles.topsoil.app.util.listener.ListenerHandles;
import org.cirdles.topsoil.app.util.serialization.PlotInformation;
import org.cirdles.topsoil.app.util.undo.UndoManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A class which oversees connections between the {@link TopsoilDataTable} and the {@link TopsoilTabContent} for a
 * particular set of data. This includes handling the binding of properties, returning complete sets of data, and
 * ensuring that the two are consistent with one another.
 *
 * @author Jake Marotta
 * @see TopsoilDataTable
 * @see TopsoilTabContent
 */
public class TopsoilTableController {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TopsoilDataTable} that is managed by this table controller.
     */
    private TopsoilDataTable table;

    /**
     * The {@code TopsoilTabContent} that is managed by this table controller.
     */
    private TopsoilTabContent tabContent;

    /**
     * A copy of the data in {@link TopsoilDataTable}, which is loaded into the {@code TableView} of the
     * {@link TopsoilTabContent}.
     */
    private ObservableList<TopsoilDataEntry> dataEntries;

    private List<List<ListenerHandle>> cellListenerHandles;

    /**
     * A {@code Map} of columns to their indices in the {@code TableView}. This is used for determining where a
     * column was moved to and from when a user clicks and drags a column.
     */
    private Map<TableColumn, Integer> columnsToIndices;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code TopsoilTableController} for the specified {@code TopsoilDataTable} and
     * {@code TopsoilTabContent}.
     *
     * @param dataTable the TopsoilDataTable that contains the data
     * @param content   the TopsoilTabContent that displays the data
     */
    public TopsoilTableController(TopsoilDataTable dataTable, TopsoilTabContent content) {
        this.table = dataTable;
        this.tabContent = content;

        // Get data from the TopsoilDataTable and put it into the TableView in TabContent.
        this.dataEntries = table.getDataEntries();
        tabContent.setData(dataEntries);
        tabContent.getTableView().setFixedCellSize(27);

        // Create cell listeners for table cells
        updateColumnListeners();

        ObservableList<TopsoilDataColumn> dataColumns = table.getDataColumns();
        Double uncertaintyFormatValue = table.getUncertaintyFormat().getValue();

        // Listen for changes in the TableView rows
        dataEntries.addListener((ListChangeListener<TopsoilDataEntry>) c -> {
            c.next();

            if (c.wasAdded()) {

                int rowIndex = c.getFrom();
                table.addRow(rowIndex, dataEntries.get(rowIndex).cloneEntry());

                for (int colIndex = 0; colIndex < dataEntries.get(rowIndex).getProperties().size(); colIndex++) {
                    final int col = colIndex;
                    final int row = rowIndex;
                    dataEntries.get(rowIndex).getProperties().get(colIndex).addListener(ch -> {
                        Double dataValue = dataEntries.get(row).getProperties().get(col).get();

                        if (Variables.UNCERTAINTY_VARIABLES.contains(dataColumns.get(col).getVariable())) {
                            dataColumns.get(col).get(row).set(dataValue / uncertaintyFormatValue);
                        } else {
                            dataColumns.get(col).get(row).set(dataValue);
                        }

                        if (!table.getOpenPlots().isEmpty()) {
                            for (PlotInformation plotInfo : table.getOpenPlots()) {
                                plotInfo.getPlot().setData(getPlotData());
                            }
                        }
                    });

                    ChangeListener<Number> cellChangedListener = (observable, oldValue, newValue) -> {

                        if (Variables.UNCERTAINTY_VARIABLES.contains(dataColumns.get(col).getVariable())) {
                            dataColumns.get(col).get(row).set(newValue.doubleValue() / uncertaintyFormatValue);
                        } else {
                            dataColumns.get(col).get(row).set(newValue.doubleValue());
                        }

                        for (PlotInformation plotInfo : table.getOpenPlots()) {
                            plotInfo.getPlot().setData(getPlotData());
                        }
                    };

                    for (List<ListenerHandle> listenerColumn : cellListenerHandles) {
                        ListenerHandle handle = ListenerHandles.createAttached(
                                dataEntries.get(rowIndex).getProperties().get(colIndex), cellChangedListener);
                        listenerColumn.add(rowIndex, handle);
                    }
                }
            }

            if (c.wasRemoved()) {
                int rowIndex = c.getFrom();
                table.removeRow(rowIndex);
                for (List<ListenerHandle> listenerColumn : cellListenerHandles) {
                    listenerColumn.remove(listenerColumn.size() - 1);
                }
            }

            if (!table.getOpenPlots().isEmpty()) {
                for (PlotInformation plotInfo : table.getOpenPlots()) {
                    plotInfo.getPlot().setData(getPlotData());
                }
            }
        });

        // Bind column names in the TableView to those headers specified in TopsoilDataTable
        columnsToIndices = new LinkedHashMap<>();
        List<TableColumn<TopsoilDataEntry, ?>> columns = tabContent.getTableView().getColumns();
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).textProperty().bindBidirectional(dataColumns.get(i).columnHeaderProperty());
            columnsToIndices.put(columns.get(i), i);
        }

        // Listen for column reordering.
        tabContent.getTableView().getColumns().addListener((ListChangeListener<TableColumn>) c -> {
            c.next();
            //   if (c.wasRemoved() && c.wasAdded())
            if (c.wasReplaced()) {
                handleColumnReorder();
            }
            resetColumnIndices();
        });

        // Bind isotope type
        tabContent.getPlotPropertiesPanelController().setIsotopeType(table.getIsotopeType());
        tabContent.getPlotPropertiesPanelController().isotopeTypeObjectProperty().bindBidirectional(table.isotopeTypeObjectProperty());
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the {@code TopsoilDataTable} managed by this {@code TopsoilTableController}.
     *
     * @return  TopsoilDataTable
     */
    public TopsoilDataTable getTable() {
        return table;
    }

    /**
     * Returns the {@code TopsoilTabContent} managed by this {@code TopsoilTableController}.
     *
     * @return  TopsoilTabContent
     */
    public TopsoilTabContent getTabContent() {
        return tabContent;
    }

    /**
     * Returns a {@code Collection} of the {@code Variable}s that have been assigned to columns.
     *
     * @return  Collection of Variables
     */
    public Collection<Variable<Number>> getAssignedVariables() {
        return table.getVariableAssignments().keySet();
    }

    /**
     * Returns the data as a {@code List} of {@code Map}s of {@code String}s to {@code Object}s. The data that is
     * returned it only data from columns which have been mapped to a variable.
     *
     * @return  plotting data as {@literal List<Map<String, Object>>}
     */
    public List<Map<String, Object>> getPlotData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<Variable<Number>, TopsoilDataColumn> variablesToColumns = table.getVariableAssignments();

        for (int rowIndex = 0; rowIndex < dataEntries.size(); rowIndex++) {
            Map<String, Object> entry = new HashMap<>();

            // TODO Customize entry selection.
            entry.put("Selected", true);

            for (Variable<Number> variable : Variables.VARIABLE_LIST) {
                Double value;
                if (variablesToColumns.containsKey(variable)) {
                    if (variable.getClass() == DependentVariable.class && UncertaintyFormat.PERCENT_FORMATS.contains(table.getUncertaintyFormat())) {
                        value = variablesToColumns.get(variable).get(rowIndex).get() * table.getVariableAssignments().get(
                                ((DependentVariable<Number>) variable).getDependency()).get(rowIndex).get();
                    } else {
                        value = variablesToColumns.get(variable).get(rowIndex).get();
                    }
                } else {
                    // Variable is unassigned, use zero value
                    value = 0.0;
                }
                entry.put(variable.getName(), value);
            }

            data.add(entry);
        }
        return data;
    }

    public void showVariableChooserDialog(@Nullable List<Variable<Number>> required) {
        Map<Variable<Number>, TopsoilDataColumn> selections = VariableChooserDialog.showDialog(this, required);
        List<TopsoilDataColumn> columns = table.getDataColumns();
        Double uncertaintyFormatValue = table.getUncertaintyFormat().getValue();
        if (selections != null) {

            // Apply selections to columns
            for (Map.Entry<Variable<Number>, TopsoilDataColumn> entry : selections.entrySet()) {
                if (entry.getValue().getVariable() != entry.getKey()) {
                    if (Variables.UNCERTAINTY_VARIABLES.contains(entry.getKey())) {

                        // If the column was NOT an uncertainty variable, but is now
                        if (!Variables.UNCERTAINTY_VARIABLES.contains(entry.getValue().getVariable())) {
                            for (DoubleProperty property : entry.getValue().get()) {
                                property.set(property.get() / uncertaintyFormatValue);
                            }
                        }

                    } else {
                        // If the column was an uncertainty variable, but isn't anymore
                        if (Variables.UNCERTAINTY_VARIABLES.contains(entry.getValue().getVariable())) {
                            for (DoubleProperty property : entry.getValue().get()) {
                                property.set(property.get() * uncertaintyFormatValue);
                            }
                        }
                    }
                    entry.getValue().setVariable(entry.getKey());
                }
            }

            // Set other columns' variable properties to null
            for (TopsoilDataColumn column : columns) {
                if (!selections.containsValue(column)) {
                    // If the column was an uncertainty variable, but isn't anymore
                    if (column.hasVariable()) {
                        if (Variables.UNCERTAINTY_VARIABLES.contains(column.getVariable())) {
                            for (DoubleProperty property : column) {
                                property.set(property.get() * uncertaintyFormatValue);
                            }
                        }
                        column.setVariable(null);
                    }
                }
            }

            table.setVariableAssignments(selections);
            for (PlotInformation plotInfo : table.getOpenPlots()) {
                plotInfo.getPlot().setData(getPlotData());
            }

            updateColumnListeners();

            // Re-name x and y axis titles
            if (selections.containsKey(Variables.X)) {
                tabContent.getPlotPropertiesPanelController().setxAxisTitle(selections.get(Variables.X).getName());
            }
            if (selections.containsKey(Variables.Y)) {
                tabContent.getPlotPropertiesPanelController().setyAxisTitle(selections.get(Variables.Y).getName());
            }
        }
    }

    /**
     * Updates the listeners on each of the data values.
     * <p>This has to be done if the data columns' variable assignments are changed to or from an uncertainty
     * variable.
     */
    private void updateColumnListeners() {
        List<TopsoilDataColumn> columns = table.getDataColumns();

        ListenerHandle handle;

        if (cellListenerHandles == null || cellListenerHandles.isEmpty()) {
            cellListenerHandles = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) {
                cellListenerHandles.add(new ArrayList<>());
            }
        } else {
            // Detach all ListenerHandles
            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                for (int rowIndex = 0; rowIndex < cellListenerHandles.get(colIndex).size(); rowIndex++) {
                    handle = cellListenerHandles.get(colIndex).get(rowIndex);
                    handle.detach();
                }
            }
        }

        // Apply uncertainty format to uncertainty columns in TableView
        Double uncertaintyFormatValue = table.getUncertaintyFormat().getValue();
        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            final int col = colIndex;

            for (int rowIndex = 0; rowIndex < dataEntries.size(); rowIndex++) {
                final int row = rowIndex;

                ChangeListener<Number> cellChangedListener = (observable, oldValue, newValue) -> {

                    if (Variables.UNCERTAINTY_VARIABLES.contains(columns.get(col).getVariable())) {
                        columns.get(col).get(row).set(newValue.doubleValue() / uncertaintyFormatValue);
                    } else {
                        columns.get(col).get(row).set(newValue.doubleValue());
                    }

                    for (PlotInformation plotInfo : table.getOpenPlots()) {
                        plotInfo.getPlot().setData(getPlotData());
                    }
                };

                // Re-attach listener
                handle = ListenerHandles.createAttached(dataEntries.get(rowIndex).getProperties().get
                        (colIndex), cellChangedListener);
                if (cellListenerHandles.get(colIndex).size() > rowIndex) {
                    cellListenerHandles.get(colIndex).remove(rowIndex);
                }
                cellListenerHandles.get(colIndex).add(rowIndex, handle);
            }
        }

    }

    /**
     * Handles the reordering of columns in the {@code TableView}.
     * <p>
     * When the columns in the {@link TopsoilTabContent}'s {@code TableView} are clicked-and-dragged by the user, this
     * method is called to make sure that the data reflects the change made, and that the reordering can be undone by
     * the {@link UndoManager}.</p>
     */
    private void handleColumnReorder() {

        TableView<TopsoilDataEntry> tableView = tabContent.getTableView();
        int numColumns = tableView.getColumns().size();
        int fromIndex = -1;
        int toIndex = -1;

        Integer[] newOrder = new Integer[numColumns];
        for (int i = 0; i < numColumns; i++) {
            newOrder[i] = columnsToIndices.get(tableView.getColumns().get(i));
        }

        if (newOrder[0] != 0) {
            // if a column was dragged to the beginning
            if (newOrder[0] > 1) {
                toIndex = 0;
                fromIndex = newOrder[0];
                // if the first column was dragged somewhere else
            } else {
                fromIndex = 0;
                toIndex = 0;
                while (newOrder[toIndex] != fromIndex) {
                    toIndex++;
                }
            }
        } else if (newOrder[numColumns - 1] != numColumns - 1) {
            // if a column was dragged to the end
            if (newOrder[numColumns - 1] < numColumns - 2) {
                toIndex = numColumns - 1;
                fromIndex = newOrder[numColumns - 1];
                // if the last column was dragged somewhere else
            } else {
                fromIndex = numColumns - 1;
                toIndex = newOrder[numColumns - 1];
                while (newOrder[toIndex] != fromIndex) {
                    toIndex--;
                }
            }
            // any other drag
        } else {
            for (int j = 1; j <= newOrder.length - 2; j++) {
                if ((newOrder[j - 1] != newOrder[j] - 1) && (newOrder[j + 1] != newOrder[j] + 1)) {
                    fromIndex = newOrder[j];
                    toIndex = j;
                    break;
                }
            }
        }

        if (fromIndex >= 0 && toIndex >= 0) {

            // Reorder data in TopsoilDataTable
            TopsoilDataColumn tempColumn = table.getDataColumns().remove(fromIndex);
            table.getDataColumns().add(toIndex, tempColumn);

            if (!table.getOpenPlots().isEmpty()) {
                for (PlotInformation plotInfo : table.getOpenPlots()) {
                    plotInfo.getPlot().setData(getPlotData());
                }
            }

            updateColumnListeners();

            TableColumnReorderCommand reorderCommand = new TableColumnReorderCommand(this, fromIndex, toIndex);
            ((TopsoilTabPane) tabContent.getTableView().getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(reorderCommand);
        }
    }

    public void addColumn(int index, TopsoilDataColumn column) {
        table.addColumn(index, column);
        dataEntries = table.getDataEntries();
        tabContent.setData(dataEntries);

        columnsToIndices.clear();
        List<TableColumn<TopsoilDataEntry, ?>> columns = tabContent.getTableView().getColumns();
        for (int i = 0; i < columns.size(); i++) {
            table.getDataColumns().get(i).columnHeaderProperty().unbind();
            columns.get(i).textProperty().bindBidirectional(table.getDataColumns().get(i).columnHeaderProperty());
            columnsToIndices.put(columns.get(i), i);
        }

        cellListenerHandles.add(index, new ArrayList<>());
        updateColumnListeners();

        tabContent.getTableView().getColumns().get(index).textProperty().bindBidirectional(column.columnHeaderProperty());

        for (PlotInformation plotInfo : table.getOpenPlots()) {
            plotInfo.getPlot().setData(getPlotData());
        }
    }

    public void removeColumn(int index) {
        tabContent.removeColumn(index);
        table.removeColumn(index);

        for (PlotInformation plotInfo : table.getOpenPlots()) {
            plotInfo.getPlot().setData(getPlotData());
        }
    }

    public int getColumnIndex(TableColumn column) {
        Integer index = columnsToIndices.get(column);
        if (index == null) {
            index = -1;
        }
        return index;
    }

    /**
     * Resets the indices associated with each {@code TableColumn} in the {@code TableView}. This is done after the
     * order has been changed so that the former and new positions of the moved column can be determined.
     */
    public void resetColumnIndices() {
        int index = 0;
        for (TableColumn<TopsoilDataEntry, ?> column : tabContent.getTableView().getColumns()) {
            columnsToIndices.put(column, index);
            index++;
        }

    }
}
