package org.cirdles.topsoil.app.progress.tab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;

/**
 * Created by sbunce on 6/29/2016.
 * Extends JavaFX TabPane class
 */
public class TopsoilTabPane extends TabPane {

    public TopsoilTabPane() {
        super();
    }

    // Add a new tab to the MainWindow tab pane
    public void add(TopsoilTable table) {
        Tab newTab = new TopsoilTab(table);
        newTab.setContent(table.getTable());
        this.getTabs().addAll(newTab);
        this.getSelectionModel().select(newTab);
    }

    public ObservableList<TopsoilTab> getTopsoilTabs() {
        ObservableList<Tab> tabs = this.getTabs();
        ObservableList<TopsoilTab> topsoilTabs = FXCollections.observableArrayList();
        for (Tab tab : tabs) {
            topsoilTabs.add((TopsoilTab) tab);
        }
        return topsoilTabs;
    }

    /**
     * Get the currently selected tab
     * @return TopsoilTab that is selected
     */
    public TopsoilTab getSelectedTab() {
        return (TopsoilTab) this.getSelectionModel().getSelectedItem();
    }

    public boolean isEmpty() {
        return this.getTabs().isEmpty();
    }
}
