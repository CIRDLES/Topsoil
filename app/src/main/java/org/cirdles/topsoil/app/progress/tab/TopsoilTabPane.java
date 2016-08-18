package org.cirdles.topsoil.app.progress.tab;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;

/**
 * Created by sbunce on 6/29/2016.
 */
public class TopsoilTabPane extends TabPane {

    //Passed to the MainMenuBar and MainButtons Bar
    public TopsoilTabPane() {
        super();
    }

    //Adds a new tab to the MainWindow tab pane
    public void add(TopsoilTable table) {
        Tab newTab = new TopsoilTab(table);
        newTab.setText(table.getTitle());
        newTab.setContent(table.getTable());
        this.getTabs().addAll(newTab);
        this.getSelectionModel().select(newTab);
    }

    public TopsoilTab getSelectedTab() {
        return (TopsoilTab) this.getSelectionModel().getSelectedItem();
    }

}
