package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableColumn;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a {@link TopsoilTab}'s {@link UndoManager} when a column is
 * renamed. This command stores the {@link TableColumn} that was deleted and both the old and new names of the column.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class ColumnRenameCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TableColumn} that was renamed.
     */
    private TopsoilDataColumn column;

    /**
     * The old name of the column.
     */
    private String oldName;

    /**
     * The new name of the column.
     */
    private String newName;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code ColumnRenameCommand} for the specified column.
     *
     * @param column    the TopsoilDataColumn that was renamed
     * @param oldName   the old name of the column
     * @param newName   the new name of the column
     */
    public ColumnRenameCommand(TopsoilDataColumn column, String oldName, String newName) {
        this.column = column;
        this.oldName = oldName;
        this.newName = newName;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Called to execute the column rename.
     */
    public void execute() {
        column.setName(newName);
    }

    /**
     * Called to undo the column rename.
     */
    public void undo() {
        column.setName(oldName);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Rename column";
    }
}
