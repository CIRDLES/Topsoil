package org.cirdles.topsoil.app.table;

import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.dataset.field.NumberField;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.TopsoilRawData;
import org.cirdles.topsoil.app.dataset.NumberDataset;
import org.cirdles.topsoil.app.tab.TopsoilTabContent;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.command.TableColumnReorderCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marottajb
 */
public class TopsoilTableController {

    private TopsoilTable table;
    private TopsoilTabContent tabContent;

    private ObservableList<TopsoilDataEntry> tableData;

    public TopsoilTableController(TopsoilTable t, TopsoilTabContent content) {
        this.table = t;
        this.tabContent = content;

        // Get data from the TopsoilTable and put it into the TableView in TabContent.
        this.tableData = table.getCopyOfDataAsEntries();
        tabContent.getTableView().setItems(tableData);

        // Bind values in Controller's tableData and TopsoilTable's data.
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

        // Bind column names in the TableView to those headers specified in TopsoilTable
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

    public TopsoilTable getTable() {
        return table;
    }

    public void setTable(TopsoilTable table) {
        this.table = table;
    }

    public TopsoilTabContent getTabContent() {
        return tabContent;
    }

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
            // Reorder data in TopsoilTable
            for (TopsoilDataEntry entry : table.getData()) {
                entry.swap(fromIndex, toIndex);
            }

            // Reorder column name properties in TopsoilTable
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
     * Resets the string ids associated with each <tt>TableColumn</tt> in the <tt>TableView</tt>.
     * <p>Each TableColumn has an associated String id assigned to it, increasing numerically from 1, left to right.
     * This is to keep track of the order of the columns before and after they are re-ordered due to clicking and
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

    public TopsoilRawData<Number> getRawData() {
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
        // TODO Take data from TopsoilTable.data
        for (int i = 0; i < tabContent.getTableView().getItems().size(); i ++) {
            entries.add(new TopsoilPlotEntry());
            for (int j = 0; j < table.getColumnNames().length; j++) {
                double currentValue = tableEntries.get(i).getProperties().get(j).getValue();
                entries.get(i).set(fields.get(j), currentValue);
            }
        }
        return new TopsoilRawData<>(fields, entries);
    }

    public NumberDataset getDataset() {
        return new NumberDataset(table.getTitle(), getRawData());
    }
}
