package org.cirdles.topsoil.app.progress;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Created by sbunce on 6/29/2016.
 */
public class TopsoilTabPane extends TabPane {

    //Passed to the MainMenuBar and MainButtons Bar
    public TopsoilTabPane() {
        //Do nothing
    }

    //Adds a new tab to the MainWindow tab pane
    public void add (TopsoilTable table) {
        Tab newTab = new Tab();
        //TODO Create a way for user to rename tabs
        newTab.setText(table.getTitle());
        newTab.setContent(table.getTable());
        this.getTabs().addAll(newTab);
        this.getSelectionModel().select(newTab);
    }

}
