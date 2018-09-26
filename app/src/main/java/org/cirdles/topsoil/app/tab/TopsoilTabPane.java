package org.cirdles.topsoil.app.tab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.cirdles.topsoil.app.spreadsheet.ObservableTableData;

/**
 * A custom {@code TabPane} for managing {@link TopsoilTab}s.
 *
 * @author sbunce
 * @see Tab
 * @see TopsoilTab
 */
public class TopsoilTabPane extends TabPane {

    private int untitledCount;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs an empty {@code TopsoilTabPane}.
     */
    public TopsoilTabPane() {
        super();
        this.untitledCount = 0;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void add(ObservableTableData data) {
        TopsoilDataView dataView = new TopsoilDataView(data);

        // Create new TopsoilTab
        TopsoilTab tab = createTopsoilTab(dataView);

        // Disables 'x' button to close tab.
        tab.setClosable(false);

        this.getTabs().add(tab);
        this.getSelectionModel().select(tab);

//        for (TopsoilDataColumn column : data.getDataColumns()) {
//            column.nameProperty().addListener(c -> {
//                if (column.getVariable() == Variables.X) {
//                    dataView.getPlotPanel().setxAxisTitle(column.getName());
//                } else if (column.getVariable() == Variables.Y) {
//                    dataView.getPlotPanel().setyAxisTitle(column.getName());
//                }
//            });
//        }

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

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private TopsoilTab createTopsoilTab(TopsoilDataView dataView) {
        String title = dataView.getData().getTitle();

        // If the table has just been generated.
        if (title.equals("Untitled Table")) {
            return newUntitledTab(dataView);
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
                return new TopsoilTab(dataView);
            }
        }
        // The table has a custom name.
        return new TopsoilTab(dataView);
    }

    private TopsoilTab newUntitledTab(TopsoilDataView dataView) {
        TopsoilTab tab = new TopsoilTab(dataView);
        tab.setContent(dataView);
        tab.setTitle("Table" + ++this.untitledCount);
        return tab;
    }

    /**
     * Returns true if there are no tabs in the {@code TopsoilTabPane}.
     *
     * @return  true if there are no tabs in the TopsoilTabPane
     */
    public boolean isEmpty() {
        return getTabs().isEmpty();
    }

}
