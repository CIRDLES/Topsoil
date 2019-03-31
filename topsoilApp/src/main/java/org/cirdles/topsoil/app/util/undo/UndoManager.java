package org.cirdles.topsoil.app.util.undo;

import java.util.ArrayDeque;

/**
 * A container for undoable {@link Command} objects. An {@code UndoManager} stores
 * Command objects and handles undo and redo operations. {@code Command} objects are stored in a pair of
 * {@link ArrayDeque}s, one for executed {@code Command}s, and the other for undone {@code Command}s. Each stores up
 * to maxSize {@code Command}s.
 *
 * @author marottajb
 *
 * @see Command
 */
public class UndoManager {

    /**
     * The maximum size of both the undo and redo {@code ArrayDeque}s. Effectively, the maximum number of changes
     * that a user can undo.
     */
    private int maxSize;

    /**
     * An {@code ArrayDeque} which stores recently executed {@link Command}s in
     * order. A {@code Command} is popped off of the deque when one needs to be undone.
     */
    private ArrayDeque<Command> undo;

    /**
     * An {@code ArrayDeque} which stores recently undone {@link Command}s in order.
     * A {@code Command} is popped off of the deque when one needs to be re-executed.
     */
    private ArrayDeque<Command> redo;

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
     * Adds a newly executed {@code Command} to the {@code UndoManager}.
     *
     * @param command   new Command
     */
    public void add(Command command) {
        if (command != null && maxSize > 0) {
            if (undo.size() == maxSize) {
                undo.removeLast();
            }
            undo.push(command);
            redo.clear();
        }
    }

    /**
     * Undoes the most recently executed {@link Command}, and moves the Command to
     * the redo {@code ArrayDeque}.
     */
    public void undo() {
        if (!undo.isEmpty()) {
            Command command = undo.pop();
            command.undo();
            redo.push(command);
        }
    }

    /**
     * Re-executes the most recently undone {@link Command}, and moves the
     * {@code Command} to the undo {@code ArrayDeque}.
     */
    public void redo() {
        if (!redo.isEmpty()) {
            Command command = redo.pop();
            command.execute();
            undo.push(command);
        }
    }

    /**
     * Returns a short description of the most recently executed {@link Command}.
     *
     * @return the name of the command
     */
    public String getUndoName() {
        return undo.isEmpty() ? "" : undo.peek().getActionName();
    }

    /**
     * Returns a short description of the most recently undone {@link Command}.
     *
     * @return the name of the command
     */
    public String getRedoName() {
        return redo.isEmpty() ? "" : redo.peek().getActionName();
    }

    /**
     * Clears both the undo and redo {@code ArrayDeque}s, effectively erasing
     * the {@link Command} history.
     */
    public void clear() {
        undo.clear();
        redo.clear();
    }
}
