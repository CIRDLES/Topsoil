package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a {@link TopsoilTab}'s {@link UndoManager} when a row is
 * deleted in the {@link TableView}. This command stores the {@link TopsoilDataEntry} that was deleted and the index
 * of the {@code TableView} from which it was deleted.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class DeleteRowCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The former index of the deleted row.
     */
    private int index;

    /**
     * The {@code TopsoilDataEntry} that was deleted.
     */
    private TopsoilDataEntry dataEntry;

    /**
     * The {@code TableView} that the row was deleted from.
     */
    private TableView<TopsoilDataEntry> tableView;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code DeleteRowCommand} for the specified cell. In this case, the row containing the cell
     * is the row that was deleted.
     *
     * @param cell the TopsoilTableCell that the command came from
     */
    public DeleteRowCommand(TopsoilTableCell cell) {
        this.index = cell.getIndex();
        this.dataEntry = cell.getDataEntry();
        this.tableView = cell.getTableView();
    }

    /**
     * Constructs a new {@code DeleteRowCommand} for the specified {@code TableView}. In this case, the last row in
     * the table view was removed.
     *
     * @param tableView the TableView that the row was deleted from
     */
    public DeleteRowCommand(TableView<TopsoilDataEntry> tableView) {
        this.index = tableView.getItems().size() - 1;
        this.dataEntry = tableView.getItems().get(index);
        this.tableView = tableView;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Called to execute the row deletion.
     */
    public void execute() {
        if (index == 0 && tableView.getItems().size() <= 1) {
            tableView.getItems().remove(index);
            tableView.getItems().add(TopsoilDataEntry.newEmptyDataEntry(tableView));
        } else {
            tableView.getItems().remove(index);
        }
    }

    /**
     * Called to undo the row deletion.
     */
    public void undo() {
        if (((TopsoilTabPane) tableView.getScene().lookup("#TopsoilTabPane")).getSelectedTab().getTableController()
                .getTable().isCleared()) {
            tableView.getItems().remove(0);
        }
        tableView.getItems().add(index, dataEntry);

    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Delete row";
    }
}