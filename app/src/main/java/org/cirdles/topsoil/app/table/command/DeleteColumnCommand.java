package org.cirdles.topsoil.app.progress.table.command;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTableCell;
import org.cirdles.topsoil.app.progress.util.Command;

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a <tt>TableColumn</tt> in the <tt>TableView</tt>
 * is deleted. This class stores a copy of the deleted column, and its index
 * in <tt>TableView</tt>.<i>getColumns()</i>.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
public class DeleteColumnCommand implements Command {

    private TableView<TopsoilDataEntry> tableView;
    private TableColumn<TopsoilDataEntry, Double> column;
    private int index;

    /**
     * Constructs a new delete column command from the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    public DeleteColumnCommand(TopsoilTableCell cell) {
        this.tableView = cell.getTableView();
        this.column = cell.getTableColumn();
        this.index = cell.getColumnIndex();
    }

    /**
     * Called to execute the column deletion.
     */
    public void execute() {

        this.tableView.getColumns().remove(index);
    }

    /**
     * Called to undo the column deletion.
     */
    public void undo() {

        this.tableView.getColumns().add(index, this.column);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Delete column";
    }
}