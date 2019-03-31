package org.cirdles.topsoil.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.menu.helpers.VisualizationsMenuHelper;
import org.cirdles.topsoil.plot.PlotType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

    /**
     * Keeps the {@code TopsoilProject} representing the current working state of the application, if one exists.
     */
    private static ObjectProperty<TopsoilProject> project = new SimpleObjectProperty<>();
    public static ObjectProperty<TopsoilProject> projectProperty() {
        return project;
    }
    public static TopsoilProject getProject() {
        return project.get();
    }
    public static void setProject(TopsoilProject project) {
        ProjectManager.project.set(project);
    }

    /**
     * Keeps the {@code Path} associated with the current working state of the application, if one exists.
     */
    private static ObjectProperty<Path> projectPath = new SimpleObjectProperty<>();
    public static ObjectProperty<Path> projectPathProperty() {
        return projectPath;
    }
    public static Path getProjectPath() {
        return projectPath.get();
    }
    public static void setProjectPath(Path path) {
        ProjectManager.projectPath.set(path);
    }

    private static List<OpenPlot> openPlots = new ArrayList<>();
    public static void registerOpenPlot(DataTable table, TopsoilPlotView plotView) {
        openPlots.add(new OpenPlot(table, plotView));
    }
    public static void deregisterOpenPlot(DataTable table, PlotType plotType) {
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table) && openPlot.getPlotView().getPlot().getPlotType().equals(plotType)) {
                openPlots.remove(openPlot);
                break;
            }
        }
    }
    public static TopsoilPlotView getOpenPlotView(DataTable table, PlotType plotType) {
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table) && openPlot.getPlotView().getPlot().getPlotType().equals(plotType)) {
                return openPlot.getPlotView();
            }
        }
        return null;
    }
    public static List<PlotType> getOpenPlotTypesForTable(DataTable table) {
        List<PlotType> plotTypes = new ArrayList<>();
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table)) {
                plotTypes.add(openPlot.getPlotView().getPlot().getPlotType());
            }
        }
        return plotTypes;
    }
    public static void updatePlotsForTable(DataTable table) {
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table)) {
                openPlot.getPlotView().getPlot().setData(VisualizationsMenuHelper.getPlotDataFromTable(table));
            }
        }
    }

    private ProjectManager() {}

    public static class OpenPlot {
        private DataTable table;
        private TopsoilPlotView plotView;

        OpenPlot(DataTable table, TopsoilPlotView plotView) {
            this.table = table;
            this.plotView = plotView;
        }

        public DataTable getTable() {
            return table;
        }

        public TopsoilPlotView getPlotView() {
            return plotView;
        }
    }
}
