package org.cirdles.topsoil.app;

import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.app.data.FXDataTable;

/**
 * Utility methods for menu actions in {@link TopsoilMenuBar} and its associated helpers.
 *
 * @author marottajb
 *
 * @see TopsoilMenuBar
 */
final class MenuUtils {

    private MenuUtils() {
        // Prevents instantiation by default constructor
    }

    /**
     * Returns the {@code DataTable} that is currently being displayed; otherwise, null.
     *
     * @return  current DataTable; else null
     */
    static FXDataTable getCurrentDataTable() {
        if (ProjectManager.getProject() != null) {
            ProjectView projectView = MainController.getInstance().getProjectView();
            if (projectView != null) {
                return projectView.getVisibleDataTable();
            }
        }
        return null;
    }

    static void undoLastAction() {
        FXDataTable table = getCurrentDataTable();
        if (table != null) {
            table.undoLastAction();
        }
    }

    static void redoLastAction() {
        FXDataTable table = getCurrentDataTable();
        if (table != null) {
            table.redoLastAction();
        }
    }

    static String lastUndoName() {
        FXDataTable table = getCurrentDataTable();
        if (table != null) {
            return table.lastUndoName();
        }
        return null;
    }

    static String lastRedoName() {
        FXDataTable table = getCurrentDataTable();
        if (table != null) {
            return table.lastRedoName();
        }
        return null;
    }

}
