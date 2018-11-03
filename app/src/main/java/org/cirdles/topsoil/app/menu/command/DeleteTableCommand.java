package org.cirdles.topsoil.app.menu.command;

import org.cirdles.topsoil.app.menu.MenuItemEventHandler;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.util.undo.Command;

/**
 * A {@code Command} instance to delete a table.
 * This {@code Command} stores the the {@link TopsoilTab} that was cleared.
 * TO DO : add this command to a TabPane UndoManager to make the command undoable
 *
 * @author Adrien Laubus
 * @see Command
 */
public class DeleteTableCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TopsoilTab} that was deleted.
     */
    private final TopsoilTab topsoilTab;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code DeleteTableCommand} for the specified {@code TopsoilTab}.
     *
     * @param topsoilTab the Topsoil Tab that was deleted
     */
    public DeleteTableCommand(TopsoilTab topsoilTab) {
        this.topsoilTab = topsoilTab;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Called to execute the table deleting.
     */
    public void execute() {
        if (MenuItemEventHandler.confirmTableDeletion()) {

            TopsoilTabPane topsoilTabPane = (TopsoilTabPane) topsoilTab.getTabPane();
            topsoilTabPane.getTabs().remove(topsoilTabPane.getSelectedTab());
        }
    }

    /**
     * Called to undo the table deleting.
     */
    public void undo() {
        TopsoilTabPane topsoilTabPane = (TopsoilTabPane) topsoilTab.getTabPane();
        topsoilTabPane.getTabs().add(topsoilTab);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Delete table";
    }

}
