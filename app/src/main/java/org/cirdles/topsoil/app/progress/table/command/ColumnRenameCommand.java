package org.cirdles.topsoil.app.progress.table.command;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.util.Command;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
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
