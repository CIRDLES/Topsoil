package org.cirdles.topsoil.app.progress.tab;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.table.TopsoilTableController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Extends JavaFX TabPane class
 * @author sbunce
 */
public class TopsoilTabPane extends TabPane {

    private int untitledCount;
    private SimpleBooleanProperty isEmptyProperty = new SimpleBooleanProperty(true);

    private final String TOPSOIL_TAB_FXML_NAME = "topsoil-tab-content.fxml";

    private final ResourceExtractor resourceExtractor = new ResourceExtractor(TopsoilTabPane.class);

    public TopsoilTabPane() throws LoadException {
        super();
        this.untitledCount = 0;
        this.getTabs().addListener((ListChangeListener<? super Tab>) c -> isEmptyProperty.set(this.getTabs().isEmpty
                ()));
    }

    // Add a new tab to the MainWindow tab pane
    public void add(TopsoilTable table) {
        try {
            // Load tab content
            FXMLLoader fxmlLoader = new FXMLLoader(resourceExtractor.extractResourceAsFile(TOPSOIL_TAB_FXML_NAME).toURI().toURL());
            SplitPane tabContentView = fxmlLoader.load();
            TopsoilTabContent tabContent = fxmlLoader.getController();

            // Create new TopsoilTab
            TopsoilTab tab = createTopsoilTab(tabContent, new TopsoilTableController(table, tabContent));

            tab.setContent(tabContentView);
            this.getTabs().addAll(tab);
            this.getSelectionModel().select(tab);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private TopsoilTab createTopsoilTab(TopsoilTabContent tabContent, TopsoilTableController tableController) {
        String title = tableController.getTable().getTitle();

        // If the table has just been generated.
        if (title.equals("Untitled Table")) {
            return newUntitledTab(tabContent, tableController);
        }

        // If the table is being read in, and starts with "Table".
        if (title.length() > 5) {
            if (title.substring(0, 5).equals("Table")) {
                try {
                    // The table is default-named.
                    int number = Integer.parseInt(title.substring(5));
                    this.untitledCount = Math.max(this.untitledCount, number);
                } catch (NumberFormatException e) {
                    // Do nothing, is not a default-named table.
                }
                return new TopsoilTab(tabContent, tableController);
            }
        }
        // The table has a custom name.
        return new TopsoilTab(tabContent, tableController);
    }

    private TopsoilTab newUntitledTab(TopsoilTabContent tabContent, TopsoilTableController tableController) {
        TopsoilTab tab = new TopsoilTab(tabContent, tableController);
        tab.setContent(tabContent.getTableView());
        tab.setTitle("Table" + ++this.untitledCount);
        return tab;
    }

    public void setUntitledCount(int count) {
        this.untitledCount = count;
    }

    public final BooleanProperty isEmptyProperty() {
        return isEmptyProperty;
    }

    public boolean isEmpty() {
        return isEmptyProperty.get();
    }

}
