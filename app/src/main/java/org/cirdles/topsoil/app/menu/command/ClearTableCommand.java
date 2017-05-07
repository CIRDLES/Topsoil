package org.cirdles.topsoil.app.menu.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.util.Command;
import org.cirdles.topsoil.app.util.UndoManager;

/**
 * An undoable <tt>Command</tt> instance that is added to a
 * <tt>TopsoilTab</tt>'s <tt>UndoManager</tt> when a new tab (and hence, new
 * table) is created. This class stores the isotope type of the new table so
 * the user doesn't have to specify again upon a redo.
 *
 * @author marottajb
 * @see Command
 * @see UndoManager
 */
public class ClearTableCommand implements Command {

    private TableView<TopsoilDataEntry> tableView;
    private TopsoilDataEntry[] rows;

    /**
     * Constructs a new clear table command for the specified table view.
     *
     * @param tableView the TableView that was cleared
     */
    public ClearTableCommand(TableView<TopsoilDataEntry> tableView) {
        this.tableView = tableView;
        this.rows = new TopsoilDataEntry[this.tableView.getItems().size()];
        for (int i = 0; i < this.tableView.getItems().size(); i++) {
            this.rows[i] = this.tableView.getItems().get(i);
        }
    }

    /**
     * Called to execute the table clearing.
     */
    public void execute() {
        ((TopsoilTabPane) this.tableView.getScene().lookup("#TopsoilTabPane"))
                .getSelectedTab().getTabContent().getTableView().getItems().clear();
    }

    /**
     * Called to undo the table clearing.
     */
    public void undo() {
        this.tableView.getItems().setAll(this.rows);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Clear table";
    }

}