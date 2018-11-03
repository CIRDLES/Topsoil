package org.cirdles.topsoil.app.spreadsheet.command;

import javafx.beans.property.SimpleDoubleProperty;
import org.cirdles.topsoil.app.data.ObservableDataColumn;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

import java.util.List;

/**
 * An undoable {@link Command} instance that can be added to a TopsoilTab's {@link UndoManager} when a column is
 * inserted into the data.
 *
 * @author marottajb
 *
 * @see Command
 * @see UndoManager
 */
public class InsertColumnCommand implements Command {

    private ObservableDataTable data;
    private int index;
    private ObservableDataColumn column;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new insert column command from the specified data controller, column, and index.
     *
     * @param   data
     *          the ObservableDataTable that the command came from
     * @param   colIndex
     *          the int index of the column
     * @param   column
     *          the ObservableDataColumn to be inserted
     */
    public InsertColumnCommand(ObservableDataTable data, int colIndex, ObservableDataColumn column ) {
        this.data = data;
        this.index = colIndex;
        this.column = column;
    }

    public InsertColumnCommand(ObservableDataTable data, int colIndex, List<Double> column ) {
        this.data = data;
        this.index = colIndex;
        this.column = new ObservableDataColumn("Untitled");
        column.forEach((value) -> this.column.add(new SimpleDoubleProperty(value)));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Called to execute the column insertion.
     */
    public void execute() {
        data.addColumn(index, column);
    }

    /**
     * Called to undo the column insertion.
     */
    public void undo() {
        data.removeColumn(index);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Insert column";
    }

}
