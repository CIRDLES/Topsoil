package org.cirdles.topsoil.app.view;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.DataRow;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.node.DataNode;
import org.cirdles.topsoil.app.view.plot.TopsoilPlotView;
import org.cirdles.topsoil.plot.AbstractPlot;

import java.io.IOException;
import java.util.List;

/**
 * @author marottajb
 */
public class TopsoilProjectView extends SplitPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "topsoil-project-view.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private TabPane tabPane;
    @FXML private ProjectTreeView projectTreeView;
    @FXML private Label projectViewLabel;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Table<AbstractPlot.PlotType, DataTable, TopsoilPlotView> openPlots = HashBasedTable.create();

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private ListProperty<DataTable> dataTableList = new SimpleListProperty<>(FXCollections.observableArrayList());
    public ListProperty<DataTable> dataTableListProperty() {
        return dataTableList;
    }
    public final List<DataTable> getDataTables() {
        return dataTableList.get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProjectView() {
        dataTableList.addListener((ListChangeListener.Change<? extends DataTable> c) -> {
            c.next();
            if (c.wasAdded()) {
                for (DataTable table : c.getAddedSubList()) {
                    addTabForTable(table);
                }
            }
        });

        try {
            final ResourceExtractor re = new ResourceExtractor(TopsoilProjectView.class);
            final FXMLLoader loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
        }
    }

    @FXML
    public void initialize() {

    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void addDataTable(DataTable dataTable) {
        dataTableList.add(dataTable);
    }

    public void removeDataTable(DataTable dataTable) {
        dataTableList.remove(dataTable);
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public ProjectTreeView getProjectTreeView() {
        return projectTreeView;
    }

    public Table<AbstractPlot.PlotType, DataTable, TopsoilPlotView> getOpenPlots() {
        return openPlots;
    }

    public void addOpenPlot(AbstractPlot.PlotType plotType, DataTable dataTable, TopsoilPlotView plotView) {
        openPlots.put(plotType, dataTable, plotView);
    }

    public void removeOpenPlot(AbstractPlot.PlotType plotType, DataTable dataTable) {
        openPlots.remove(plotType, dataTable);
    }

    public void setSelectedForNode(ProjectViewTabPane tab, DataNode node, boolean b) {
        TreeItem<DataRow> item = tab.getTreeItemForNode(node);
        if (item instanceof CheckBoxTreeItem) {
            CheckBoxTreeItem<DataRow> cbti = (CheckBoxTreeItem<DataRow>) item;
            cbti.setSelected(b);
        }
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void addTabForTable(DataTable dataTable) {
        Tab tab = new Tab();
        tab.setContent(new ProjectViewTabPane(dataTable));
        tab.textProperty().bindBidirectional(dataTable.labelProperty());
        tabPane.getTabs().add(tab);
    }

}
