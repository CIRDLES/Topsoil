package org.cirdles.topsoil.app.progress.util;

import java.util.ArrayDeque;

/**
 * A container for undoable Command objects. An UndoManager stores Command
 * objects and handles undo and redo operations. Command objects are stored in
 * a pair of ArrayDeques, one for executed Commands, and the other for undone
 * Commands. Each stores up to maxSize commands.
 *
 * @author marottajb
 * @see Command
 */
public class UndoManager {

    private int maxSize;
    private ArrayDeque<Command> undo;
    private ArrayDeque<Command> redo;

    /**
     * Constructs an empty UndoManager, with ArrayDeques of size maxSize.
     *
     * @param maxSize   the maximum number of commands that can be stored
     */
    public UndoManager(int maxSize) {
        undo = new ArrayDeque<>(maxSize);
        redo = new ArrayDeque<>(maxSize);
        this.maxSize = maxSize;
    }

    /**
     * Adds a newly executed command instance to the UndoManager.
     *
     * @param command   new Command instance
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
     * Undoes the most recently executed command, and moves the command to the
     * redo ArrayDeque.
     */
    public void undo() {
        if (!undo.isEmpty()) {
            Command command = undo.pop();
            command.undo();
            redo.push(command);
        }
    }

    /**
     * Re-executes the most recently undone command, and moves the command to
     * the undo ArrayDeque.
     */
    public void redo() {
        if (!redo.isEmpty()) {
            Command command = redo.pop();
            command.execute();
            undo.push(command);
        }
    }

    /**
     * Returns a short description of the most recently executed command.
     *
     * @return the name of the command
     */
    public String getUndoName() {
        return undo.isEmpty() ? "" : undo.peek().getActionName();
    }

    /**
     * Returns a short description of the most recently undone command.
     *
     * @return the name of the command
     */
    public String getRedoName() {
        return redo.isEmpty() ? "" : redo.peek().getActionName();
    }

    /**
     * Clears both the undo and redo ArrayDeques, effectively erasing the
     * command history.
     */
    public void clear() {
        undo.clear();
        redo.clear();
    }
}
