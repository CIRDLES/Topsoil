package org.cirdles.topsoil.app.progress.util;

import java.util.ArrayDeque;

/**
 * A container for undoable <tt>Command</tt> objects. An <tt>UndoManager</tt>
 * stores Command objects and handles undo and redo operations. Command
 * objects are stored in a pair of <tt>ArrayDeque</tt>s, one for executed
 * Commands, and the other for undone Commands. Each stores up to maxSize
 * commands.
 *
 * @author marottajb
 * @see Command
 */
public class UndoManager {

    private int maxSize;
    private ArrayDeque<Command> undo;
    private ArrayDeque<Command> redo;

    /**
     * Constructs an empty <tt>UndoManager</tt>, with undo and redo
     * <tt>ArrayDeque</tt>s of size maxSize.
     *
     * @param maxSize   the maximum number of commands that can be stored
     */
    public UndoManager(int maxSize) {
        undo = new ArrayDeque<>(maxSize);
        redo = new ArrayDeque<>(maxSize);
        this.maxSize = maxSize;
    }

    /**
     * Adds a newly executed <tt>Command</tt> instance to the
     * <tt>UndoManager</tt>.
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
     * Undoes the most recently executed <tt>Command</tt>, and moves the
     * Command to the redo <tt>ArrayDeque</tt>.
     */
    public void undo() {
        if (!undo.isEmpty()) {
            Command command = undo.pop();
            command.undo();
            redo.push(command);
        }
    }

    /**
     * Re-executes the most recently undone <tt>Command</tt>, and moves the
     * Command to the undo <tt>ArrayDeque</tt>.
     */
    public void redo() {
        if (!redo.isEmpty()) {
            Command command = redo.pop();
            command.execute();
            undo.push(command);
        }
    }

    /**
     * Returns a short description of the most recently executed
     * <tt>Command</tt>.
     *
     * @return the name of the command
     */
    public String getUndoName() {
        return undo.isEmpty() ? "" : undo.peek().getActionName();
    }

    /**
     * Returns a short description of the most recently undone <tt>Command</tt>.
     *
     * @return the name of the command
     */
    public String getRedoName() {
        return redo.isEmpty() ? "" : redo.peek().getActionName();
    }

    /**
     * Clears both the undo and redo <tt>ArrayDeque</tt>s, effectively erasing
     * the <tt>Command</tt> history.
     */
    public void clear() {
        undo.clear();
        redo.clear();
    }
}
