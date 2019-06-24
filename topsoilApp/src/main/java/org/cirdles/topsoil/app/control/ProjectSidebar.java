package org.cirdles.topsoil.app.control;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.app.MenuUtils;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.control.dialog.PlotConfigDialog;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.TableUtils;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.javafx.SingleChildRegion;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotOption;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author marottajb
 */
public class ProjectSidebar extends SingleChildRegion<TreeView<String>> {

    private final ResourceExtractor re = new ResourceExtractor(ProjectSidebar.class);
    private TopsoilProject project;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    ProjectSidebar(TopsoilProject project) {
        super(new TreeView<>());

        this.project = project;

        final TreeItem<String> rootItem = new TreeItem<>("root");
        TreeView<String> treeView = getChild();
        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);
        treeView.setCellFactory(param -> new SidebarTreeCell());

        for (FXDataTable table : project.getDataTables()) {
            rootItem.getChildren().add(new TableTreeItem(table));
        }
        project.dataTablesProperty().addListener((ListChangeListener<? super FXDataTable>) c -> {
            while(c.next()) {
                if (c.wasAdded()) {
                    for (FXDataTable table : c.getAddedSubList()) {
                        rootItem.getChildren().add(new TableTreeItem(table));
                    }
                }
                if (c.wasRemoved()) {
                    for (FXDataTable table : c.getRemoved()) {
                        removeTable(table);
                    }
                }
            }
        });
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private void removeTable(FXDataTable table) {
        if (table == null) {
            return;
        }
        Iterator<TreeItem<String>> iterator = getChild().getRoot().getChildren().iterator();
        TreeItem<String> item;
        while (iterator.hasNext()) {
            item = iterator.next();
            if (item instanceof TableTreeItem) {
                if (table.equals(((TableTreeItem) item).table)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private ImageView getTreeItemGraphic(String resource) {
        ImageView graphic = null;
        try {
            graphic = new ImageView(
                    new Image(
                            re.extractResourceAsPath(resource).toUri().toURL().toString(),
                            16.0,
                            16.0,
                            true,
                            true
                    )
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return graphic;
    }

    private class TableTreeItem extends TreeItem<String> {

        private final FXDataTable table;
        private final TreeItem<String> infoSectionItem;
        private final TreeItem<String> plotSectionItem;

        TableTreeItem(FXDataTable dataTable) {
            this.table = dataTable;
            valueProperty().bind(table.titleProperty());
            setGraphic(getTreeItemGraphic("table-icon.png"));
            setExpanded(true);

            this.infoSectionItem = new TreeItem<>("Info:");
            getChildren().add(infoSectionItem);
            TreeItem<String> uncertaintyItem = new TreeItem<>("Unct. Format: " + table.getUncertainty().getName());
            table.uncertaintyProperty().addListener(c ->
                uncertaintyItem.setValue("Unct. Format: " +
                        ((table.getUncertainty() != null) ? table.getUncertainty().getName() : "[none]"))
            );
            TreeItem<String> columnCountItem = new TreeItem<>("# Columns: " + TableUtils.countLeafColumns(table.getColumns()));
            columnCountItem.valueProperty().bind(Bindings.createStringBinding(
                    () -> "# Columns: " + TableUtils.countLeafColumns(table.getColumns()),
                    table.columnsProperty()
            ));
            TreeItem<String> rowCountItem = new TreeItem<>("# Rows: " + table.getLeafRows().size());
            rowCountItem.valueProperty().bind(Bindings.createStringBinding(
                    () -> "# Rows: " + TableUtils.countLeafRows(table.getRows()),
                    table.rowsProperty()
            ));
            infoSectionItem.getChildren().addAll(Arrays.asList(uncertaintyItem, columnCountItem, rowCountItem));

            this.plotSectionItem = new TreeItem<>("Plots:");
            ReadOnlyListProperty<PlotView> plotList = project.getPlotMap().get(table);
            for (PlotView plot : plotList) {
                plotSectionItem.getChildren().add(new PlotTreeItem(plot));
            }
            // Update plotSectionItem's children when plot opened or closed
            plotList.addListener((ListChangeListener<PlotView>) c -> {
                while (c.next()) {
                    if (c.wasAdded()) {
                        if (!getChildren().contains(plotSectionItem)) {
                            getChildren().add(plotSectionItem);
                        }
                        for (PlotView plot : c.getAddedSubList()) {
                            plotSectionItem.getChildren().add(new PlotTreeItem(plot));
                        }
                    }
                    if (c.wasRemoved()) {
                        if (c.getList().size() == 0) {
                            getChildren().remove(plotSectionItem);
                        }
                        for (PlotView plot : c.getRemoved()) {
                            removePlot(plot);
                        }
                    }
                }
            });
            if (! plotList.isEmpty()) {
                // Add plot section if plots are open for this table
                getChildren().add(plotSectionItem);
            }
        }

        private void removePlot(Plot plot) {
            for (TreeItem<String> child : plotSectionItem.getChildren()) {
                if (((PlotTreeItem) child).plot.equals(plot)) {
                    plotSectionItem.getChildren().remove(child);
                    break;
                }
            }
        }
    }

    private class PlotTreeItem extends TreeItem<String> {

        private final PlotView plot;
        private final TreeItem<String> variableSectionItem;
        private final Map<Variable<?>, TreeItem<String>> variableItemMap = new HashMap<>();

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
            setGraphic(getTreeItemGraphic("plot-icon.png"));

            TreeItem<String> isotopeSystemItem = new TreeItem<>();
            ObjectBinding<Object> isotopeSystemBinding = Bindings.valueAt(plot.plotOptionsProperty(), PlotOption.ISOTOPE_SYSTEM);
            isotopeSystemItem.valueProperty().bind(Bindings.createStringBinding(
                    () -> {
                        IsotopeSystem isotopeSystem = (IsotopeSystem) isotopeSystemBinding.get();
                        return "Isotope System: " + ((isotopeSystem != null) ? isotopeSystem.getAbbreviation() : "[none]");
                    },
                    isotopeSystemBinding
            ));
            this.getChildren().add(isotopeSystemItem);

            variableSectionItem = new TreeItem<>("Variables:");

            ReadOnlyMapProperty<Variable<?>, DataColumn<?>> variableMap = plot.variableMapProperty();
            for (Map.Entry<Variable<?>, DataColumn<?>> entry : variableMap.entrySet()) {
                addVariableMappingItem(entry.getKey(), plot);
            }
            variableMap.addListener((MapChangeListener<Variable<?>, DataColumn<?>>) c -> {
                Variable<?> variable = c.getKey();
                TreeItem<String> variableMappingItem = variableItemMap.get(variable);
                if (c.wasAdded()) {
                    if (variableMappingItem == null) {
                        addVariableMappingItem(variable, plot);
                    }
                    if (!getChildren().contains(variableSectionItem)) {
                        getChildren().add(0, variableSectionItem);
                    }
                }
                if (c.wasRemoved()) {
                    if (! c.wasAdded()) {
                        removeVariableMappingItem(variable);
                    }
                    if (c.getMap().size() == 0) {
                        getChildren().remove(variableSectionItem);
                    }
                }
            });
            if (! variableMap.isEmpty()) {
                getChildren().add(0, variableSectionItem);
            }
        }

        private void addVariableMappingItem(Variable<?> variable, PlotView plot) {
            TreeItem<String> item = new TreeItem<>();
            ObjectBinding<DataColumn<?>> columnBinding = plot.variableMapProperty().valueAt(variable);
            item.valueProperty().bind(Bindings.createStringBinding(
                    () -> {
                        DataColumn<?> column = columnBinding.get();
                        String columnTitle = (column != null) ? column.getTitle() : "";
                        return variable.getAbbreviation() + " => " + columnTitle;
                    },
                    columnBinding
            ));
            variableItemMap.put(variable, item);
            variableSectionItem.getChildren().add(item);
        }

        private void removeVariableMappingItem(Variable<?> variable) {
            TreeItem<String> item = variableItemMap.get(variable);
            if (item != null) {
                variableSectionItem.getChildren().remove(item);
            }
            variableItemMap.remove(variable);
        }
    }

    private class SidebarTreeCell extends TreeCell<String> {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
            } else {
                setText((item != null) ? item : "");
                setGraphic(getTreeItem().getGraphic());
                setContextMenu(createContextMenu());
            }
        }

        private ContextMenu createContextMenu() {
            TreeItem<String> item = getTreeItem();
            ContextMenu menu = null;

            if (item instanceof TableTreeItem) {
                FXDataTable table = ((TableTreeItem) item).table;

                MenuItem bringToFront = new MenuItem("Bring to Front");
                bringToFront.setOnAction(event -> MenuUtils.selectDataTable(table));

                MenuItem tableOptions = new MenuItem("Edit Table Options...");
                tableOptions.setOnAction(event -> {
                    Map<DataTableOptionsDialog.Key, Object> newSettings =
                            DataTableOptionsDialog.showDialog(table, Topsoil.getPrimaryStage());
                    if (newSettings != null) {
                        DataTableOptionsDialog.applySettings(table, newSettings);
                    }
                });

                menu = new ContextMenu(
                        bringToFront,
                        new SeparatorMenuItem(),
                        tableOptions
                );
            } else if (item instanceof PlotTreeItem) {
                PlotView plotView = ((PlotTreeItem) item).plot;

                MenuItem bringToFront = new MenuItem("Bring to Front");
                bringToFront.setOnAction(event -> plotView.getScene().getWindow().requestFocus());

                MenuItem plotConfig = new MenuItem("Edit Plot Config...");
                plotConfig.setOnAction(event -> {
                    Map<PlotConfigDialog.Key, Object> settings = new HashMap<>();
                    settings.put(PlotConfigDialog.Key.VARIABLE_MAP, plotView.getVariableMap());
                    settings.put(PlotConfigDialog.Key.ISOTOPE_SYSTEM, plotView.getOptions().get(PlotOption.ISOTOPE_SYSTEM));
                    PlotConfigDialog dialog = new PlotConfigDialog((FXDataTable) plotView.getDataTable(), settings);

                    Map<PlotConfigDialog.Key, Object> newSettings = dialog.showAndWait().orElse(null);
                    if (newSettings != null) {
                        plotView.setVariableMap((Map<Variable<?>, DataColumn<?>>) newSettings.get(PlotConfigDialog.Key.VARIABLE_MAP));
                        plotView.getOptions().put(PlotOption.ISOTOPE_SYSTEM, newSettings.get(PlotConfigDialog.Key.ISOTOPE_SYSTEM));
                    }
                });

                menu = new ContextMenu(
                        bringToFront,
                        new SeparatorMenuItem(),
                        plotConfig
                );
            }

            return menu;
        }
    }

}
