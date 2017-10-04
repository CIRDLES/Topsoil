package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * An undoable {@link Command} instance that can be added to a TopsoilTab's {@link UndoManager} when a
 * {@link TableColumn} in the {@code TableView} is cleared. This {@code Command} stores the data from the cleared
 * column, and its index in {@link TableView#getColumns()}.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class ClearColumnCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TopsoilTableView} that the column is in.
     */
    private TableView<TopsoilDataEntry> tableView;

    /**
     * The index of the {@code TableColumn} in {@code TableView.getColumns()}.
     */
    private int colIndex;

    /**
     * The number of values in the column.
     */
    private int numRows;

    /**
     * An {@code ArrayDeque} that stores the properties from the cleared {@code TableColumn}.
     */
    private List<Double> columnData;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code ClearColumnCommand} for the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    public ClearColumnCommand(TopsoilTableCell cell) {

        this.tableView = cell.getTableView();
        this.colIndex = cell.getColumnIndex();
        this.numRows = tableView.getItems().size();
        this.columnData = new ArrayList<>(numRows);

        for (int i = 0; i < numRows; i++) {
            columnData.add(i, tableView.getItems().get(i).getProperties().get(colIndex).get());
        }
    }

    //***********************
    // Methods
    //***********************

    /**
     * Called to execute the column deletion.
     */
    public void execute() {

        for (TopsoilDataEntry row : tableView.getItems()) {
            row.setValue(colIndex, 0.0);
        }

        // This is a workaround to force the TableView to update the visible Node
        tableView.setVisible(false);
        tableView.setVisible(true);
    }

    /**
     * Called to undo the column deletion.
     */
    public void undo() {

        for (int i = 0; i < tableView.getItems().size(); i++) {
            tableView.getItems().get(i).setValue(colIndex, columnData.get(i));
        }

        // This is a workaround to force the TableView to update the visible Node
        tableView.setVisible(false);
        tableView.setVisible(true);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Clear column";
    }
}
