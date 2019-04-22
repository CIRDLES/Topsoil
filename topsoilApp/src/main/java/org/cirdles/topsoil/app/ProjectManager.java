package org.cirdles.topsoil.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.data.DataUtils;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.util.PlotObservationThread;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperties;
import org.cirdles.topsoil.plot.PlotType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

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
            if (openPlot.getTable().equals(table) && openPlot.getPlotType().equals(plotType)) {
                openPlot.shutdownObserver();
                openPlots.remove(openPlot);
                break;
            }
        }
    }
    public static TopsoilPlotView getOpenPlotView(DataTable table, PlotType plotType) {
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table) && openPlot.getPlotType().equals(plotType)) {
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
                openPlot.getPlotView().getPlot().setData(DataUtils.getPlotData(table));
            }
        }
    }

    public static void updatePlots() {
        DataTable table;
        Plot plot;
        PlotProperties properties;
        for (OpenPlot openPlot : openPlots) {
            table = openPlot.getTable();
            plot = openPlot.getPlotView().getPlot();
            properties = plot.getProperties();

            properties.set(PlotProperties.LAMBDA_U234, project.get().getLambdaValue(Lambda.U234));
            properties.set(PlotProperties.LAMBDA_U235, project.get().getLambdaValue(Lambda.U235));
            properties.set(PlotProperties.LAMBDA_U238, project.get().getLambdaValue(Lambda.U238));
            properties.set(PlotProperties.LAMBDA_TH230, project.get().getLambdaValue(Lambda.Th230));

            plot.setData(DataUtils.getPlotData(table));
            openPlot.getPlotView().getPropertiesPanel().setPlotProperties(properties.getProperties());
//            plot.setProperties(properties);
        }
    }

    public static void closeProject() {
        Stage plotStage;
        OpenPlot[] plots = openPlots.toArray(new OpenPlot[]{});
        for (OpenPlot openPlot : plots) {
            plotStage = (Stage) openPlot.getPlotView().getScene().getWindow();
            plotStage.close();
            deregisterOpenPlot(openPlot.getTable(), openPlot.getPlotType());
        }
        ProjectManager.setProject(null);
        ProjectManager.setProjectPath(null);
    }

    private ProjectManager() {}

    public static class OpenPlot {
        private DataTable table;
        private TopsoilPlotView plotView;
        private ScheduledExecutorService observer;

        OpenPlot(DataTable table, TopsoilPlotView plotView) {
            this.table = table;
            this.plotView = plotView;

            // Update properties panel with changes in the plot
            PlotObservationThread observationThread = new PlotObservationThread();
            observer = observationThread.initializePlotObservation(plotView.getPlot(), plotView.getPropertiesPanel());
        }

        public DataTable getTable() {
            return table;
        }

        public PlotType getPlotType() {
            return plotView.getPlot().getPlotType();
        }

        public TopsoilPlotView getPlotView() {
            return plotView;
        }

        private void shutdownObserver() {
            observer.shutdown();
        }
    }
}
