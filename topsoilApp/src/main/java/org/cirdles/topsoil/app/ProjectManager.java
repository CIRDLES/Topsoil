package org.cirdles.topsoil.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
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
import java.util.concurrent.ScheduledExecutorService;

/**
 * Utility class for managing the current {@link TopsoilProject}, as well as its associated file {@code Path} and open
 * plots.
 */
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

    /**
     * Registers the {@code TopsoilPlotView} for a table so that the open plot can be tracked and updated.
     *
     * @param table     DataTable
     * @param plotView  TopsoilPlotView
     */
    public static void registerOpenPlot(DataTable table, TopsoilPlotView plotView) {
        openPlots.add(new OpenPlot(table, plotView));
    }

    /**
     * De-registers the plot of the specified plot type for the provided table.
     *
     * @param table     DataTable
     * @param plotType  PlotType
     */
    public static void deregisterOpenPlot(DataTable table, PlotType plotType) {
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table) && openPlot.getPlotType().equals(plotType)) {
                openPlot.shutdownObserver();
                openPlots.remove(openPlot);
                break;
            }
        }
    }

    /**
     * Returns the {@code TopsoilPlotView} with the specified plot type for the provided table.
     *
     * @param table     DataTable
     * @param plotType  PlotType
     *
     * @return          TopsoilPlotView
     */
    public static TopsoilPlotView getOpenPlotView(DataTable table, PlotType plotType) {
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table) && openPlot.getPlotType().equals(plotType)) {
                return openPlot.getPlotView();
            }
        }
        return null;
    }

    /**
     * Returns a list of the plot types that are currently open for the specified table.
     *
     * @param table     DataTable
     *
     * @return          List of PlotType
     */
    public static List<PlotType> getOpenPlotTypesForTable(DataTable table) {
        List<PlotType> plotTypes = new ArrayList<>();
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table)) {
                plotTypes.add(openPlot.getPlotView().getPlot().getPlotType());
            }
        }
        return plotTypes;
    }

    /**
     * Updates the data for all open plots for the specified table.
     *
     * @param table     DataTable
     */
    public static void updatePlotsForTable(DataTable table) {
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table)) {
                openPlot.getPlotView().getPlot().setData(DataUtils.getPlotData(table));
            }
        }
    }

    /**
     * Updates all plots with information about project {@link Lambda} values and the data for their associated
     * {@link DataTable}s.
     */
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
        }
    }

    /**
     * Closes all open plots for the project and sets the project to null.
     */
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

    /**
     * Acts as a handle for objects associated with an open plot, including the {@code DataTable} model and the
     * {@code TopsoilPlotView} view.
     *
     * Upon creation, it will start a {@code Service} that will automatically update certain properties in the
     * {@link org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel} that change within the JS execution of a
     * plot. The service must be shut down manually upon plot de-registration.
     */
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

        /**
         * Returns the table associated with this plot.
         *
         * @return  DataTable
         */
        public DataTable getTable() {
            return table;
        }

        /**
         * Returns the plot type of this plot.
         *
         * @return  PlotType
         */
        public PlotType getPlotType() {
            return plotView.getPlot().getPlotType();
        }

        /**
         * Returns the {@code TopsoilPlotView} node for this plot.
         *
         * @return  TopsoilPlotView
         */
        public TopsoilPlotView getPlotView() {
            return plotView;
        }

        /**
         * Shuts down the service that automatically updates the plot properties panel.
         */
        private void shutdownObserver() {
            observer.shutdown();
        }
    }
}
