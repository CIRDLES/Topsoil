package org.cirdles.topsoil.app.control.menu;

import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.control.ProjectTableTab;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.app.data.DataTable;

/**
 * @author marottajb
 */
public class MenuUtils {

    public static boolean isDataOpen() {
        return Main.getController().getMainContent() instanceof ProjectView;
    }

    public static ProjectView getProjectView() {
        if (isDataOpen()) {
            return (ProjectView) Main.getController().getMainContent();
        }
        return null;
    }

    public static ProjectTableTab getSelectedTableTab() {
        if (isDataOpen()) {
            ProjectView projectView = getProjectView();
            return (ProjectTableTab) projectView.getTabPane().getSelectionModel().getSelectedItem();
        }
        return null;
    }

    public static DataTable getCurrentTable() {
        if (isDataOpen()) {
            ProjectTableTab tab = getSelectedTableTab();
            return tab.getDataTable();
        }
        return null;
    }

}
