package org.cirdles.topsoil.app.progress.table.command;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.util.Command;

import java.util.Arrays;
import java.util.List;

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a <tt>TableColumn</tt> in the <tt>TableView</tt>
 * is dragged to a new position. This class determines which column was moved,
 * where it was moved from, and where it was moved to, so the action can be
 * undone or replicated.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
public class TableColumnReorderCommand implements Command {

    private List<StringProperty> columnNameProperties;
    private TableView<TopsoilDataEntry> tableView;
    private ObservableList<TopsoilDataEntry> data;
    private int fromIndex;
    private int toIndex;

    /**
     * Constructs a new column reorder command for the table view in which the
     * column was moved. The former and new index of the moved column is
     * determined by the order of the String ids of the columns, which
     * increment from 0.
     *
     * @param tableView the TableView that the columns belong to
     */
    public TableColumnReorderCommand(List<StringProperty> columnNameProperties, TableView<TopsoilDataEntry>
            tableView, ObservableList<TopsoilDataEntry> data, int fromIndex, int toIndex) {
        this.columnNameProperties = columnNameProperties;
        this.tableView = tableView;
        this.data = data;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    /**
     * Called to execute the column reorder.
     */
    public void execute() {
        TableColumn<TopsoilDataEntry, ?> columnTemp = tableView.getColumns().get(fromIndex);
        StringProperty nameTemp = columnNameProperties.get(fromIndex);

        // Order TableColumns in TableView
        tableView.getColumns().remove(fromIndex);
        tableView.getColumns().add(toIndex, columnTemp);

        // Order columnNameProperties in TopsoilTable
        columnNameProperties.remove(fromIndex);
        columnNameProperties.add(toIndex, nameTemp);

        // Order data in TopsoilTable
        for (TopsoilDataEntry entry : data) {
            entry.swap(fromIndex, toIndex);
        }
    }

    /**
     * Called to undo the column reorder.
     */
    public void undo() {
        TableColumn<TopsoilDataEntry, ?> temp = tableView.getColumns().get(toIndex);
        StringProperty temp2 = columnNameProperties.get(toIndex);

        // Order TableColumns in TableView
        tableView.getColumns().remove(toIndex);
        tableView.getColumns().add(fromIndex, temp);

        // Order columnNameProperties in TopsoilTable
        columnNameProperties.remove(toIndex);
        columnNameProperties.add(fromIndex, temp2);

        // Order data in TopsoilTable
        for (TopsoilDataEntry entry : data) {
            entry.swap(fromIndex, toIndex);
        }
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Change column position";
    }
}
