package org.cirdles.topsoil.app.control.menu;

import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.app.data.DataTable;

/**
 * Utility methods for menu actions in {@link TopsoilMenuBar} and its associated helpers.
 *
 * @author marottajb
 *
 * @see TopsoilMenuBar
 * @see FileMenu
 * @see org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper
 * @see org.cirdles.topsoil.app.control.menu.helpers.EditMenuHelper
 * @see org.cirdles.topsoil.app.control.menu.helpers.ViewMenuHelper
 * @see org.cirdles.topsoil.app.control.menu.helpers.VisualizationsMenuHelper
 * @see org.cirdles.topsoil.app.control.menu.helpers.HelpMenuHelper
 */
class MenuUtils {

    /**
     * Returns the {@code DataTable} that is currently being displayed; otherwise, null.
     *
     * @return  current DataTable; else null
     */
    static DataTable getCurrentDataTable() {
        if (Topsoil.getController().getProject() != null) {
            ProjectView projectView = (ProjectView) Topsoil.getController().getMainContent();
            if (projectView != null) {
                return projectView.getVisibleDataTable();
            }
        }
        return null;
    }

}
