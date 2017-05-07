package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableColumn;
import org.cirdles.topsoil.app.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.util.Command;
import org.cirdles.topsoil.app.util.UndoManager;

/**
 *
 * @author marottajb
 * @see Command
 * @see UndoManager
 */
public class ColumnRenameCommand implements Command {

    TableColumn<TopsoilDataEntry, Double> column;
    String oldName;
    String newName;

    /**
     *
     */
    public ColumnRenameCommand(TableColumn<TopsoilDataEntry, Double> column, String oldName, String newName) {
        this.column = column;
        this.oldName = oldName;
        this.newName = newName;
    }

    /**
     * Called to execute the column rename.
     */
    public void execute() {
        column.setText(newName);
    }

    /**
     * Called to undo the column rename.
     */
    public void undo() {
        column.setText(oldName);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Rename column";
    }
}
