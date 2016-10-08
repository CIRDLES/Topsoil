package org.cirdles.topsoil.app.progress.menu;

import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTab;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.util.Command;

/**
 * An undoable <tt>Command</tt> instance that is added to a
 * <tt>TopsoilTab</tt>'s <tt>UndoManager</tt> when a new tab (and hence, new
 * table) is created. This class stores the isotope type of the new table so
 * the user doesn't have to specify again upon a redo.
 *
 * @author marottajb
 * @see org.cirdles.topsoil.app.progress.util.Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class NewTableCommand implements Command {

    private TopsoilTabPane tabs;
    private TopsoilTab tab;
    private IsotopeType isotopeType;

    /**
     * Constructs a new table command of the specified isotope type for the
     * tab pane.
     *
     * @param tabs  the TopsoilTabPane in which the table was created
     * @param isotopeType   the IsotopeType of the table
     */
    NewTableCommand(TopsoilTabPane tabs, IsotopeType isotopeType) {
        this.tabs = tabs;
        this.tab = tabs.getSelectedTab();
        this.isotopeType = isotopeType;
    }

    /**
     * Called to execute the table creation.
     */
    public void execute() {
        tabs.add(new TopsoilTable(null, isotopeType,
                new TopsoilDataEntry[]{}));
        tab = tabs.getSelectedTab();
    }

    /**
     * Called to undo the table creation.
     */
    public void undo() {
        this.tabs.getTabs().remove(this.tab);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Create new table";
    }
}
