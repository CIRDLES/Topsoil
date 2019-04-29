package org.cirdles.topsoil.app.control.undo;

import java.util.ArrayDeque;

/**
 * A container for undoable {@link UndoAction} objects. An {@code UndoManager} stores
 * UndoAction objects and handles undo and redo operations. {@code UndoAction} objects are stored in a pair of
 * {@link ArrayDeque}s, one for executed {@code UndoAction}s, and the other for undone {@code UndoAction}s. Each stores up
 * to maxSize {@code UndoAction}s.
 *
 * @author marottajb
 *
 * @see UndoAction
 */
public class UndoManager {

    /**
     * The maximum size of both the undo and redo {@code ArrayDeque}s. Effectively, the maximum number of changes
     * that a user can undo.
     */
    private int maxSize;

    /**
     * An {@code ArrayDeque} which stores recently executed {@link UndoAction}s in
     * order. A {@code UndoAction} is popped off of the deque when one needs to be undone.
     */
    private ArrayDeque<UndoAction> undo;

    /**
     * An {@code ArrayDeque} which stores recently undone {@link UndoAction}s in order.
     * A {@code UndoAction} is popped off of the deque when one needs to be re-executed.
     */
    private ArrayDeque<UndoAction> redo;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs an empty {@code UndoManager}, with undo and redo {@code ArrayDeque}s of size maxSize.
     *
     * @param   maxSize
     *          the maximum number of commands that can be stored
     */
    public UndoManager(int maxSize) {
        undo = new ArrayDeque<>(maxSize);
        redo = new ArrayDeque<>(maxSize);
        this.maxSize = maxSize;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Adds a newly executed {@code UndoAction} to the {@code UndoManager}.
     *
     * @param undoAction   new UndoAction
     */
    public void add(UndoAction undoAction) {
        if (undoAction != null && maxSize > 0) {
            if (undo.size() == maxSize) {
                undo.removeLast();
            }
            undo.push(undoAction);
            redo.clear();
        }
    }

    /**
     * Undoes the most recently executed {@link UndoAction}, and moves the UndoAction to
     * the redo {@code ArrayDeque}.
     */
    public void undo() {
        if (!undo.isEmpty()) {
            UndoAction undoAction = undo.pop();
            undoAction.undo();
            redo.push(undoAction);
        }
    }

    /**
     * Re-executes the most recently undone {@link UndoAction}, and moves the
     * {@code UndoAction} to the undo {@code ArrayDeque}.
     */
    public void redo() {
        if (!redo.isEmpty()) {
            UndoAction undoAction = redo.pop();
            undoAction.execute();
            undo.push(undoAction);
        }
    }

    /**
     * Returns a short description of the most recently executed {@link UndoAction}.
     *
     * @return the name of the command
     */
    public String getUndoName() {
        return undo.isEmpty() ? null : undo.peek().getActionName();
    }

    /**
     * Returns a short description of the most recently undone {@link UndoAction}.
     *
     * @return the name of the command
     */
    public String getRedoName() {
        return redo.isEmpty() ? null : redo.peek().getActionName();
    }

    /**
     * Clears both the undo and redo {@code ArrayDeque}s, effectively erasing
     * the {@link UndoAction} history.
     */
    public void clear() {
        undo.clear();
        redo.clear();
    }
}
