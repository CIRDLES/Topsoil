package org.cirdles.topsoil.app.table;

import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.dataset.field.NumberField;
import org.cirdles.topsoil.app.dataset.TopsoilRawData;
import org.cirdles.topsoil.app.dataset.NumberDataset;
import org.cirdles.topsoil.app.plot.variable.DependentVariable;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.tab.TopsoilTabContent;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.command.TableColumnReorderCommand;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.dataset.entry.TopsoilPlotEntry;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.serialization.PlotInformation;
import org.cirdles.topsoil.app.util.undo.UndoManager;

import java.util.*;

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
        tabContent.getTableView().setItems(dataEntries);

        // Bind values in Controller's dataEntries and TopsoilDataTable's data.
        ObservableList<TopsoilDataColumn> dataColumns = table.getDataColumns();

        // Apply uncertainty format to uncertainty columns, and bind DoubleProperties.
        Double uncertaintyFormatValue = table.getUncertaintyFormat().getValue();
        for (int colIndex = 0; colIndex < dataColumns.size(); colIndex++) {
            final int col = colIndex;

            for (int rowIndex = 0; rowIndex < dataEntries.size(); rowIndex++) {
                final int row = rowIndex;

                // TODO Detect the column's variable in a different way than its index.
//                if (Variables.UNCERTAINTY_VARIABLES.contains(dataColumns.get(colIndex).getVariable())) {
                if (col == 2 || col == 3) {
                    dataEntries.get(rowIndex).getProperties().get(colIndex).set(
                            dataEntries.get(rowIndex).getProperties().get(colIndex).get() * uncertaintyFormatValue);
                }

                // Apply changes in TableView to data columns.
                dataEntries.get(rowIndex).getProperties().get(colIndex).addListener(c -> {
                    Double dataValue = dataEntries.get(row).getProperties().get(col).get();

                    // If the column is representing uncertainty values, apply the uncertainty format to it.
                    // TODO Detect the column's variable in a different way than its index.
//                    if (Variables.UNCERTAINTY_VARIABLES.contains(dataColumns.get(col).getVariable())) {
                    if (col == 2 || col == 3) {
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
            }
        }

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

                        // TODO Detect the column's variable in a different way than its index.
                        if (col == 2 || col == 3) {
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
                }
            }

            if (c.wasRemoved()) {
                int rowIndex = c.getFrom();
                table.removeRow(rowIndex);
            }

            if (!table.getOpenPlots().isEmpty()) {
                for (PlotInformation plotInfo : table.getOpenPlots()) {
                    plotInfo.getPlot().setData(getPlotData());
                }
            }
        });

        // Bind column names in the TableView to those headers specified in TopsoilDataTable
        columnsToIndices = new LinkedHashMap<>();
        List<StringProperty> headers = table.getColumnNameProperties();
        List<TableColumn<TopsoilDataEntry, ?>> columns = tabContent.getTableView().getColumns();
        for (int i = 0; i < headers.size(); i++) {
            columns.get(i).textProperty().bindBidirectional(headers.get(i));
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
     * Gets raw data from the {@link TopsoilDataTable} as a {@code TopsoilRawData}.
     *
     * @return  a TopsoilRawData of type Number
     */
    private TopsoilRawData<Number> getRawData() {
        // Initialize fields
        List<Field<Number>> fields = new ArrayList<>();

        for (String header : table.getColumnNames()) {
            Field<Number> field = new NumberField(header);
            fields.add(field);
        }

        // Initialize entries
        List<Entry> entries = new ArrayList<>();

        // put relevant entries into entry list
        List<TopsoilDataEntry> tableEntries = table.getDataEntries();
        // TODO Take data from TopsoilDataTable.data
        for (int i = 0; i < tableEntries.size(); i ++) {
            TopsoilPlotEntry entry = new TopsoilPlotEntry();
            for (int j = 0; j < table.getColumnNames().length; j++) {
                entry.set(fields.get(j), tableEntries.get(i).getProperties().get(j).getValue());
            }
            entries.add(entry);
        }
        return new TopsoilRawData<>(fields, entries);
    }

    /**
     * Returns a {@code Collection} of the {@code Variable}s that have been assigned to columns.
     *
     * @return  Collection of Variables
     */
    public Collection<Variable<Number>> getAssignedVariables() {
        return table.getColumnBindings().keySet();
    }

    /**
     * Returns the data as a {@code List} of {@code Map}s of {@code String}s to {@code Object}s. The data that is
     * returned it only data from columns which have been mapped to a variable.
     *
     * @return  plotting data as {@literal List<Map<String, Object>>}
     */
    public List<Map<String, Object>> getPlotData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<Variable<Number>, TopsoilDataColumn> variablesToColumns = table.getColumnBindings();

        for (int rowIndex = 0; rowIndex < dataEntries.size(); rowIndex++) {
            Map<String, Object> entry = new HashMap<>();

            // TODO Customize entry selection.
            entry.put("Selected", true);

            for (Map.Entry<Variable<Number>, TopsoilDataColumn> variableColumn : variablesToColumns.entrySet()) {
                Object value;
                if (variableColumn.getKey().getClass() == DependentVariable.class
                        && UncertaintyFormat.PERCENT_FORMATS.contains(table.getUncertaintyFormat())) {
                    value = variableColumn.getValue().get(rowIndex).get() * table.getColumnBindings().get
                            (((DependentVariable<Number>) variableColumn.getKey()).getDependency()).get(rowIndex).get();
                } else {
                    value = variableColumn.getValue().get(rowIndex).get();
                }
                entry.put(variableColumn.getKey().getName(), value);
            }
            data.add(entry);
        }
        return data;
    }

    /**
     * Gets the data in the {@link TopsoilDataTable} as a {@code NumberDataset}.
     *
     * @return  a NumberDataset
     */
    public NumberDataset getDataset() {
        return new NumberDataset(table.getTitle(), getRawData());
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

            table.resetVariableMapping();

            // Reorder column name properties in TopsoilDataTable
            StringProperty tempName = table.getColumnNameProperties().remove(fromIndex);
            table.getColumnNameProperties().add(toIndex, tempName);

            if (!table.getOpenPlots().isEmpty()) {
                for (PlotInformation plotInfo : table.getOpenPlots()) {
                    plotInfo.getPlot().setData(getPlotData());
                }
            }

            TableColumnReorderCommand reorderCommand = new TableColumnReorderCommand(this, fromIndex, toIndex);
            ((TopsoilTabPane) tabContent.getTableView().getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo
                    (reorderCommand);
        }
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
