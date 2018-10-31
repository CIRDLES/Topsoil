package org.cirdles.topsoil.app.spreadsheet.command;

import org.cirdles.topsoil.app.data.ObservableDataRow;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.tab.TopsoilTab;
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

    private ObservableDataTable data;
    private int index;
    private ObservableDataRow row;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new {@code InsertRowCommand} for an inserted row at the specified index.
     *
     * @param   data
     *          ObservableDataTable
     * @param   rowIndex
     *          inserted row's index
     * @param   values
     *          a List of Double values to create a row
     */
    public InsertRowCommand(ObservableDataTable data, int rowIndex, List<Double> values) {
        this(data, rowIndex, new ObservableDataRow((Double[]) values.toArray(new Double[]{})));

    }

    /**
     * Constructs a new {@code InsertRowCommand} for an inserted row at the specified index.
     *
     * @param   data
     *          ObservableDataTable
     * @param   rowIndex
     *          inserted row's index
     * @param   row
     *          ObservableDataRow
     */
    public InsertRowCommand(ObservableDataTable data, int rowIndex, ObservableDataRow row) {
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