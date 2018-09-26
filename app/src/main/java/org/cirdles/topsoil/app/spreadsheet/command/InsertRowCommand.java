package org.cirdles.topsoil.app.spreadsheet.command;

import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.spreadsheet.ObservableTableData;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

import java.util.List;

/**
 * An undoable {@link Command} instance that can be added to a {@link TopsoilTab}'s {@link UndoManager} when a row is
 * inserted into the data. This command creates an empty row and inserts is at the specified index.
 *
 * @author marottajb
 *
 * @see Command
 * @see UndoManager
 */
public class InsertRowCommand implements Command {

    private ObservableTableData data;
    private int index;
    private List<Double> row;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new {@code InsertRowCommand} for an inserted row at the specified index.
     *
     * @param   data
     *          the ObservableTableData to which the row was inserted
     * @param   rowIndex
     *          the inserted row's index
     * @param   row
     *          the row that was inserted
     */
    public InsertRowCommand(ObservableTableData data, int rowIndex, List<Double> row) {
        this.data = data;
        this.index = rowIndex;
        this.row = row;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Called to execute the row insertion.
     */
    public void execute() {
        data.addRow(index, row);
    }

    /**
     * Called to undo the row insertion.
     */
    public void undo() {
        data.removeRow(index);
    }

    /** {@inheritDoc} */
    public String getActionName() {
        return "Insert row";
    }

}