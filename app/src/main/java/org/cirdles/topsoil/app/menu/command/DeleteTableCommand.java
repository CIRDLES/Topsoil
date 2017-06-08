package org.cirdles.topsoil.app.menu.command;

import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
     * Asks the user if they really want to delete the open data table.
     *
     * @return true if delete is confirmed, false if not
     */
    public static Boolean confirmDeletion() {
        final AtomicReference<Boolean> reference = new AtomicReference<>(null);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you really want to delete this table?\n"
                        + "This operation can not be undone.",
                ButtonType.NO,
                ButtonType.YES);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                reference.set(true);
            } else if (response == ButtonType.NO) {
                reference.set(false);
            }
        });
        return reference.get();
    }
    
    /**
     * Called to execute the table deleting.
     */
    public void execute() {
        if(confirmDeletion()){
            
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