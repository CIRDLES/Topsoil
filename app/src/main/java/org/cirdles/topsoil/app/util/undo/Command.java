package org.cirdles.topsoil.app.util.undo;

/**
 * An undoable action. Instances of the implementing class can be stored in an
 * {@link UndoManager}.
 *
 * <p>The {@code Command} interface provides a method to undo a previously executed action. The method should return any
 * affected objects to the same state they were in before the action was executed.
 *
 * <p>This interface provides a method for carrying out the original action. This way, once an action is undone, it can
 * also be redone. If possible, this method can be called upon creation of the {@code Command}, and executed in place of
 * additional code.
 *
 * <p>This interface provides a method for getting a short description of an action.
 *
 * @author Jake Marotta
 * @see UndoManager
 * @see org.cirdles.topsoil.app.table.command.ClearCellCommand
 * @see org.cirdles.topsoil.app.table.command.ClearColumnCommand
 * @see org.cirdles.topsoil.app.table.command.ClearRowCommand
 * @see org.cirdles.topsoil.app.menu.command.ClearTableCommand
 * @see org.cirdles.topsoil.app.table.command.ColumnRenameCommand
 * @see org.cirdles.topsoil.app.table.command.DeleteColumnCommand
 * @see org.cirdles.topsoil.app.table.command.DeleteRowCommand
 * @see org.cirdles.topsoil.app.table.command.InsertRowCommand
 * @see org.cirdles.topsoil.app.table.command.TableCellEditCommand
 * @see org.cirdles.topsoil.app.table.command.TableColumnReorderCommand
 */
public interface Command {

    /**
     * Executes the stored action.
     */
    void execute();

    /**
     * Undoes the stored action.
     */
    void undo();

    /**
     * Returns a short message describing the stored action.
     *
     * @return  String action message
     */
    String getActionName();
}
