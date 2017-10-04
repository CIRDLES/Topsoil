package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.app.util.serialization.PlotInformation;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a {@link TopsoilTab}'s {@link UndoManager} when a
 * {@link TableColumn} in the {@link TableView} is dragged to a new position. This class determines which column
 * was moved, where it was moved from, and where it was moved to, so the action can be undone or replicated.
 *
 * @author marottajb
 * @see Command
 * @see UndoManager
 */
public class TableColumnReorderCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TableView} whose columns where reordered.
     */
    private TopsoilTableController tableController;

    /**
     * The former index of the moved column.
     */
    private int fromIndex;

    /**
     * The new index of the moved column.
     */
    private int toIndex;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new column reorder command for the table view in which the column was moved, given the {@code
     * TopsoilTableController} for the table view, the former index, and the new index of the column.
     *
     * @param tableController   the TopsoilTableController for the table
     * @param fromIndex the former index of the moved column
     * @param toIndex   the new index of the moved column
     */
    public TableColumnReorderCommand(TopsoilTableController tableController, int fromIndex, int toIndex) {
        this.tableController = tableController;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Moves a TableColumn from the fromIndex, to the toIndex.
     *
     * @param fromIndex the column's starting index
     * @param toIndex   the column's target index
     */
    private void reorder(int fromIndex, int toIndex) {
        // Order TableColumns in TableView
        TableColumn<TopsoilDataEntry, ?> columnTemp = tableController.getTabContent().getTableView().getColumns().remove(fromIndex);
        tableController.getTabContent().getTableView().getColumns().add(toIndex, columnTemp);

        // Order data in TopsoilDataTable
        TopsoilDataColumn tempColumn = tableController.getTable().getDataColumns().remove(fromIndex);
        tableController.getTable().getDataColumns().add(toIndex, tempColumn);

        tableController.resetColumnIndices();

        if (!tableController.getTable().getOpenPlots().isEmpty()) {
            for (PlotInformation plotInfo : tableController.getTable().getOpenPlots()) {
                plotInfo.getPlot().setData(tableController.getPlotData());
            }
        }
    }

    /**
     * Called to execute the column reorder.
     */
    public void execute() {
        reorder(fromIndex, toIndex);
    }

    /**
     * Called to undo the column reorder.
     */
    public void undo() {
        reorder(toIndex, fromIndex);
    }

    /**
     * Called from the {@code UndoManager} to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Change column position";
    }
}
