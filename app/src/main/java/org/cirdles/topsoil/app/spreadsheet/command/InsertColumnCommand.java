package org.cirdles.topsoil.app.spreadsheet.command;

import javafx.beans.property.SimpleDoubleProperty;
import org.cirdles.topsoil.app.spreadsheet.ObservableTableData;
import org.cirdles.topsoil.app.spreadsheet.TopsoilDataColumn;
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

    private ObservableTableData data;
    private int index;
    private TopsoilDataColumn column;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new insert column command from the specified data controller, column, and index.
     *
     * @param   data
     *          the ObservableTableData that the command came from
     * @param   colIndex
     *          the int index of the column
     * @param   column
     *          the TopsoilDataColumn to be inserted
     */
    public InsertColumnCommand( ObservableTableData data, int colIndex, TopsoilDataColumn column ) {
        this.data = data;
        this.index = colIndex;
        this.column = column;
    }

    public InsertColumnCommand( ObservableTableData data, int colIndex, List<Double> column ) {
        this.data = data;
        this.index = colIndex;
        this.column = new TopsoilDataColumn("Untitled");
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
