package org.cirdles.topsoil.app.tab;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.app.table.TopsoilDataTable;
import org.cirdles.topsoil.app.table.TopsoilTableController;

import java.io.IOException;

/**
 * A custom {@code TabPane} for managing {@link TopsoilTab}s.
 *
 * @author sbunce
 * @see Tab
 * @see TopsoilTab
 */
public class TopsoilTabPane extends TabPane {

    //***********************
    // Attributes
    //***********************

    /**
     * A count of the number of default-titled tabs in the tab pane.
     */
    private int untitledCount;

    /**
     * A {@code BooleanProperty} that keeps track of whether or not the tab pane has tabs in it.
     */
    private BooleanProperty isEmpty = new SimpleBooleanProperty(true);

    /**
     * A {@code String} path to the {@code .fxml} file for {@code TopsoilTabContent}.
     */
    private static final String TOPSOIL_TAB_FXML_PATH = "topsoil-tab-content.fxml";

    /**
     * A {@code ResourceExtractor} for extracting necessary resources. Used by CIRDLES projects.
     */
    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(TopsoilTabPane.class);

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs an empty {@code TopsoilTabPane}.
     */
    public TopsoilTabPane() {
        super();
        this.untitledCount = 0;
        this.getTabs().addListener((ListChangeListener<? super Tab>) c -> isEmptyProperty().set(this.getTabs().isEmpty()));
    }

    //***********************
    // Methods
    //***********************

    /**
     * Adds a {@code TopsoilDataTable} to the {@code TopsoilTabPane} by loading a {@code TopsoilTabContent} from FXML, creating a
     * {@code TopsoilTableController} to manage them, and putting the controller and tab content into a {@code TopsoilTab}.
     *
     * @param table the TopsoilDataTable to be added
     */
    public void add(TopsoilDataTable table) {
        try {
            // Load tab content
            FXMLLoader fxmlLoader = new FXMLLoader(
                    RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_TAB_FXML_PATH).toUri().toURL());
            SplitPane tabContentView = fxmlLoader.load();
            TopsoilTabContent tabContent = fxmlLoader.getController();
            tabContent.setUncertaintyFormatLabel(table.getUncertaintyFormat().getName());

            // Create new TopsoilTab
            TopsoilTab tab = createTopsoilTab(tabContent, new TopsoilTableController(table, tabContent));

            // Set default plot name, x-axis name, and y-axis name.
            tabContent.getPlotPropertiesPanelController().setTitle(table.getTitle() + " - Plot");
            TopsoilDataColumn xColumn = table.getVariableAssignments().get(Variables.X);
            TopsoilDataColumn yColumn = table.getVariableAssignments().get(Variables.Y);
            tabContent.getPlotPropertiesPanelController().setxAxisTitle(xColumn == null ? "X Axis Title" : xColumn.getName());
            tabContent.getPlotPropertiesPanelController().setyAxisTitle(yColumn == null ? "Y Axis Title" : yColumn.getName());

            for (TopsoilDataColumn column : table.getDataColumns()) {
                column.nameProperty().addListener(c -> {
                    if (column.getVariable() == Variables.X) {
                        tabContent.getPlotPropertiesPanelController().setxAxisTitle(column.getName());
                    } else if (column.getVariable() == Variables.Y) {
                        tabContent.getPlotPropertiesPanelController().setyAxisTitle(column.getName());
                    }
                });
            }

            tab.setContent(tabContentView);

            // Disables 'x' button to close tab.
            tab.setClosable(false);

            this.getTabs().addAll(tab);
            this.getSelectionModel().select(tab);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the tabs in the {@code TopsoilTabPane} as an ObservableList of {@code TopsoilTab}s.
     *
     * @return  an ObservableList of TopsoilTabs
     */
    public ObservableList<TopsoilTab> getTopsoilTabs() {
        ObservableList<Tab> tabs = this.getTabs();
        ObservableList<TopsoilTab> topsoilTabs = FXCollections.observableArrayList();
        for (Tab tab : tabs) {
            topsoilTabs.add((TopsoilTab) tab);
        }
        return topsoilTabs;
    }

    /**
     * Returns the currently selected {@code TopsoilTab}.
     *
     * @return the current TopsoilTab
     */
    public TopsoilTab getSelectedTab() {
        return (TopsoilTab) this.getSelectionModel().getSelectedItem();
    }

    /**
     * Creates a new {@code TopsoilTab}. for the specified {@code TopsoilTabContent} and {@code TopsoilTableController}.
     *
     * @param tabContent    TopsoilTabContent to display
     * @param tableController   TopsoilTableController
     * @return  a new TopsoilTab
     */
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

    /**
     * Creates a new {@code TopsoilTab} with a default title, if the {@code TopsoilDataTable} being added does not have its own title.
     *
     * @param tabContent    TopsoilTabContent to display
     * @param tableController   TopsoilTabController
     * @return  a new default-titled TopsoilTab
     */
    private TopsoilTab newUntitledTab(TopsoilTabContent tabContent, TopsoilTableController tableController) {
        TopsoilTab tab = new TopsoilTab(tabContent, tableController);
        tab.setContent(tabContent.getTableView());
        tab.setTitle("Table" + ++this.untitledCount);
        return tab;
    }

    public final BooleanProperty isEmptyProperty() {
        if (isEmpty == null) {
            isEmpty = new SimpleBooleanProperty();
        }
        return isEmpty;
    }
    /**
     * Returns true if there are no tabs in the {@code TopsoilTabPane}.
     *
     * @return  true if there are no tabs in the TopsoilTabPane
     */
    public boolean isEmpty() {
        return isEmptyProperty().get();
    }

}
