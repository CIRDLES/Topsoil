package org.cirdles.topsoil.app.menu.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@code Command} instance that is added to a {@link TopsoilTab}'s {@link UndoManager} when a table is
 * cleared. This {@code Command} stores the data that was cleared, and the {@link TableView} that the data was
 * cleared from.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class ClearTableCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TableView} that was cleared.
     */
    private TableView<TopsoilDataEntry> tableView;

    /**
     * The data that was cleared.
     */
    private TopsoilDataEntry[] rows;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code ClearTableCommand} for the specified {@code TableView}.
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

    //***********************
    // Methods
    //***********************

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

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Clear table";
    }

}
