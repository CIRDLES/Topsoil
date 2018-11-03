package org.cirdles.topsoil.app.util.undo;

/**
 * An undoable action. Instances of the implementing class can be stored in an
 * {@link UndoManager}.
 *
 * <p>The {@code Command} interface provides methods to undo or redo a  previously executed action. The method should
 * return any affected objects to the same state they were in before the action was executed.
 *
 * @author marottajb
 *
 * @see UndoManager
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
