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
import org.cirdles.topsoil.app.tab.TopsoilTabContent;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.command.TableColumnReorderCommand;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.dataset.entry.TopsoilPlotEntry;
import org.cirdles.topsoil.app.util.undo.UndoManager;

import java.util.ArrayList;
import java.util.List;

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
    private ObservableList<TopsoilDataEntry> tableData;

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
        this.tableData = table.getCopyOfDataAsEntries();
        tabContent.getTableView().setItems(tableData);

        // Bind values in Controller's tableData and TopsoilDataTable's data.
        ObservableList<TopsoilDataEntry> data = table.getData();
        for (int i = 0; i < tableData.size(); i++) {
            for (int j = 0; j < tableData.get(i).getProperties().size(); j++) {
                data.get(i).getProperties().get(j).bind(tableData.get(i).getProperties().get(j));
            }
        }

        // Listen for changes in the TableView rows
        tableData.addListener((ListChangeListener<TopsoilDataEntry>) c -> {
            c.next();

            if (c.wasAdded()) {
                int index = c.getFrom();
                TopsoilDataEntry newEntry = tableData.get(index).cloneEntry();
                for (int i = 0; i < tableData.get(index).getProperties().size(); i++) {
                    newEntry.getProperties().get(i).bind(tableData.get(index).getProperties().get(i));
                }
                table.getData().add(index, newEntry);
            }

            if (c.wasRemoved()) {
                int index = c.getFrom();
                table.getData().remove(index);
            }
        });

        tabContent.getTableView().setItems(tableData);

        // Bind column names in the TableView to those headers specified in TopsoilDataTable
        List<StringProperty> headers = table.getColumnNameProperties();
        List<TableColumn<TopsoilDataEntry, ?>> columns = tabContent.getTableView().getColumns();
        for (int i = 0; i < headers.size(); i++) {
            columns.get(i).textProperty().bindBidirectional(headers.get(i));
        }

        // Listen for column reordering.
        tabContent.getTableView().getColumns().addListener((ListChangeListener<TableColumn>) c -> {
            c.next();
            //   if (c.wasRemoved() && c.wasAdded())
            if (c.wasReplaced()) {
                handleColumnReorder();
            }
            resetIds();
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
        List<TopsoilDataEntry> tableEntries = table.getCopyOfDataAsEntries();
        // TODO Take data from TopsoilDataTable.data
        for (int i = 0; i < tabContent.getTableView().getItems().size(); i ++) {
            entries.add(new TopsoilPlotEntry());
            for (int j = 0; j < table.getColumnNames().length; j++) {
                double currentValue = tableEntries.get(i).getProperties().get(j).getValue();
                entries.get(i).set(fields.get(j), currentValue);
            }
        }
        return new TopsoilRawData<>(fields, entries);
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

        int[] newColumnOrder = new int[numColumns];
        for (int i = 0; i < numColumns; i++) {
            newColumnOrder[i] = Integer.parseInt(tableView.getColumns().get(i).getId());
        }

        if (newColumnOrder[0] != 0) {
            // if a column was dragged to the beginning
            if (newColumnOrder[0] > 1) {
                toIndex = 0;
                fromIndex = newColumnOrder[0];
                // if the first column was dragged somewhere else
            } else {
                fromIndex = 0;
                toIndex = 0;
                while (newColumnOrder[toIndex] != fromIndex) {
                    toIndex++;
                }
            }
        } else if (newColumnOrder[numColumns - 1] != numColumns - 1) {
            // if a column was dragged to the end
            if (newColumnOrder[numColumns - 1] < numColumns - 2) {
                toIndex = numColumns - 1;
                fromIndex = newColumnOrder[numColumns - 1];
                // if the last column was dragged somewhere else
            } else {
                fromIndex = numColumns - 1;
                toIndex = newColumnOrder[numColumns - 1];
                while (newColumnOrder[toIndex] != fromIndex) {
                    toIndex--;
                }
            }
            // any other drag
        } else {
            for (int j = 1; j < newColumnOrder.length - 2; j++) {
                if ((newColumnOrder[j - 1] != newColumnOrder[j] - 1)
                    && (newColumnOrder[j + 1] != newColumnOrder[j] + 1)) {
                    fromIndex = newColumnOrder[j];
                    toIndex = j;
                    break;
                }
            }
        }

        if (fromIndex >= 0 && toIndex >= 0) {
            // Reorder data in TopsoilDataTable
            for (TopsoilDataEntry entry : table.getData()) {
                entry.swap(fromIndex, toIndex);
            }

            // Reorder column name properties in TopsoilDataTable
            StringProperty sTemp = table.getColumnNameProperties().get(fromIndex);
            table.getColumnNameProperties().remove(fromIndex);
            table.getColumnNameProperties().add(toIndex, sTemp);

            TableColumnReorderCommand reorderCommand = new TableColumnReorderCommand(table.getColumnNameProperties(),
                                                                                     tabContent.getTableView(),
                                                                                     table.getData(),
                                                                                     fromIndex, toIndex);
            ((TopsoilTabPane) tabContent.getTableView().getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo
                    (reorderCommand);
        }
    }

    /**
     * Resets the string ids associated with each {@code TableColumn} in the {@code TableView}.
     * <p>Each {@code TableColumn} has an associated String id assigned to it, increasing numerically from 1, left to
     * right. This is to keep track of the order of the columns before and after they are re-ordered due to clicking and
     * dragging.
     * </p>
     */
    private void resetIds() {
        int id = 0;
        for (TableColumn<TopsoilDataEntry, ?> column : tabContent.getTableView().getColumns()) {
            column.setId(Integer.toString(id));
            id++;
        }
    }
}
