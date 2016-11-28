package org.cirdles.topsoil.app.progress.tab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;

/**
 * Created by sbunce on 6/29/2016.
 * Extends JavaFX TabPane class
 * @author sbunce
 */
public class TopsoilTabPane extends TabPane {

    private int untitledCount;

    public TopsoilTabPane() {
        super();
        this.untitledCount = 0;
    }

    // Add a new tab to the MainWindow tab pane
    public void add(TopsoilTable table) {
        Tab newTab = createTopsoilTab(table);
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

    private TopsoilTab createTopsoilTab(TopsoilTable table) {
        String title = table.getTitle();

        // If the table has just been generated.
        if (title.equals("Untitled Table")) {
            return newUntitledTab(table);
        }

        // If the table is being read in, and starts with "Table".
        if (title.substring(0, 5).equals("Table")) {
            try {
                // The table is default-named.
                int number = Integer.parseInt(title.substring(5));
                this.untitledCount = Math.max(this.untitledCount, number);
            } catch (NumberFormatException e) {
                // Do nothing, is not a default-named table.
            }
            return new TopsoilTab(table);
        }
        // The table has a custom name.
        return new TopsoilTab(table);
    }

    private TopsoilTab newUntitledTab(TopsoilTable table) {
        TopsoilTab tab = new TopsoilTab(table);
        tab.setContent(table.getTable());
        tab.setTitle("Table" + ++this.untitledCount);
        return tab;
    }

    public void setUntitledCount(int count) {
        this.untitledCount = count;
    }

    public boolean isEmpty() {
        return this.getTabs().isEmpty();
    }

}
