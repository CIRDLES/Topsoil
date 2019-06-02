package org.cirdles.topsoil.app.control;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.PlotOption;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author marottajb
 */
public class ProjectSidebar extends Region {

    private final ResourceExtractor re = new ResourceExtractor(ProjectSidebar.class);
    private TopsoilProject project;
    private TreeView<String> treeView;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ProjectSidebar(TopsoilProject project) {
        setFocusTraversable(true);

        this.project = project;

        final TreeItem<String> rootItem = new TreeItem<>("root");
        treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(false);
        getChildren().add(treeView);

        for (FXDataTable table : project.getDataTables()) {
            addDataTable(table);
        }
        project.dataTablesProperty().addListener((ListChangeListener<? super FXDataTable>) c -> {
            while(c.next()) {
                for (FXDataTable table : c.getRemoved()) {
                    removeDataTable(table);
                }
                for (FXDataTable table : c.getAddedSubList()) {
                    addDataTable(table);
                }
            }
        });
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private void addDataTable(FXDataTable table) {
        TreeItem<String> tableItem = new TableTreeItem(table);
        TreeItem<String> plotSectionItem = new TreeItem<>("Plots");

        ReadOnlyListProperty<PlotView> plotList = project.getPlotMap().get(table);
        for (PlotView plot : plotList) {
            plotSectionItem.getChildren().add(new PlotTreeItem(plot));
        }
        // Update plotSectionItem's children when plot opened or closed
        plotList.addListener((ListChangeListener<PlotView>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (PlotView plot : c.getAddedSubList()) {
                        plotSectionItem.getChildren().add(new PlotTreeItem(plot));
                    }
                }
                if (c.wasRemoved()) {
                    for (PlotView plot : c.getRemoved()) {
                        for (TreeItem<String> child : plotSectionItem.getChildren()) {
                            if (child.getValue().equals(plot.getOptions().get(PlotOption.TITLE))) {
                                plotSectionItem.getChildren().remove(child);
                                break;
                            }
                        }
                    }
                }
            }
        });
        tableItem.getChildren().add(plotSectionItem);
        treeView.getRoot().getChildren().add(tableItem);
    }

    private void removeDataTable(FXDataTable table) {
        List<TreeItem<String>> tableItems = new ArrayList<>(treeView.getRoot().getChildren());
        for (TreeItem<String> item : tableItems) {
            if (((TableTreeItem) item).getTable().equals(table)) {
                treeView.getRoot().getChildren().remove(item);
                break;
            }
        }
    }

    @Override
    protected double computeMinWidth(double height) {
        return treeView.minWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        return treeView.minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return treeView.prefWidth(height) +
                snappedLeftInset() +
                snappedRightInset();
    }

    @Override
    protected double computePrefHeight(double width) {
        return treeView.prefHeight(width) +
                snappedTopInset() +
                snappedBottomInset();
    }

    @Override
    protected void layoutChildren() {
        final double x = snappedLeftInset();
        final double y = snappedTopInset();

        final double width = getWidth() - (snappedLeftInset() + snappedRightInset());
        final double height = getHeight() - (snappedTopInset() + snappedBottomInset());

        treeView.resizeRelocate(x, y, width, height);
    }

    private class TableTreeItem extends TreeItem<String> {

        private final FXDataTable table;

        TableTreeItem(FXDataTable dataTable) {
            this.table = dataTable;
            valueProperty().bind(table.titleProperty());
            try {
                ImageView graphic = new ImageView(
                        new Image(
                                re.extractResourceAsPath("table-icon.png").toUri().toURL().toString(),
                                16.0,
                                16.0,
                                true,
                                true
                        )
                );
                setGraphic(graphic);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        public FXDataTable getTable() {
            return table;
        }
    }

    private class PlotTreeItem extends TreeItem<String> {

        private final PlotView plot;

        PlotTreeItem(PlotView plotView) {
            this.plot = plotView;
            ObjectBinding<Object> valueBinding = Bindings.valueAt(plot.plotOptionsProperty(), PlotOption.TITLE);
            valueProperty().bind(Bindings.createStringBinding(
                    () -> {
                        String value = String.valueOf(valueBinding.get());
                        return (value.isEmpty()) ? "[untitled]" : value;
                    },
                    valueBinding
            ));

            try {
                ImageView graphic = new ImageView(
                        new Image(
                                re.extractResourceAsPath("plot-icon.png").toUri().toURL().toString(),
                                16.0,
                                16.0,
                                true,
                                true
                        )
                );
                setGraphic(graphic);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

}
