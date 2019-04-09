package org.cirdles.topsoil.app.menu.helpers;

import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.ProjectManager;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.control.plot.PlotStage;
import org.cirdles.topsoil.app.data.DataUtils;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.util.PlotObservationThread;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.app.util.DataChangeObserver;
import org.cirdles.topsoil.plot.*;
import org.cirdles.topsoil.variable.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ScheduledExecutorService;

import static org.cirdles.topsoil.plot.PlotProperty.TITLE;
import static org.cirdles.topsoil.plot.PlotProperty.UNCERTAINTY;
import static org.cirdles.topsoil.plot.PlotProperty.X_AXIS;
import static org.cirdles.topsoil.plot.PlotProperty.Y_AXIS;

/**
 * A utility class providing helper methods for the logic behind items in the menu bar.
 *
 * @author marottajb
 */
public class VisualizationsMenuHelper {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final double DEFAULT_PLOT_WIDTH = 1000.0;
    private static final double DEFAULT_PLOT_HEIGHT = 600.0;

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Generates and displays a plot.
     *
     * @param plotType      PlotType
     * @param table         DataTable
     * @param properties    plot properties
     *
     * @return              true if successful
     */
    public static boolean generatePlot(PlotType plotType, DataTable table, Map<PlotProperty, Object> properties) {
        List<Variable<?>> required = Arrays.asList(Variables.X, Variables.Y);
        List<Variable<?>> missing = new ArrayList<>();
        for (Variable<?> v : required) {
            if (table.getColumnForVariable(v) == null) {
                missing.add(v);
            }
        }
        if (! missing.isEmpty()) {
            TopsoilNotification.error("Missing Variables", "The following variables must be assigned:\n\n" + missing.toString());
            return false;
        }

        TopsoilPlotView openPlotView = ProjectManager.getOpenPlotView(table, plotType);
        if (openPlotView != null) {
            // Find already-open plot and request focus
            openPlotView.getScene().getWindow().requestFocus();
        } else {
            List<Map<String, Object>> data = DataUtils.getPlotData(table);
            List<String> dataErrors = DataUtils.getDataErrors(table);

            if (dataErrors.size() > 0) {
                StringJoiner errors = new StringJoiner("\n");
                for (String err : dataErrors) {
                    errors.add(err);
                }
                // @TODO Encapsulate reasons for invalid rows; right now there's only one:
                TopsoilNotification.error("Invalid Rows", "The following rows have errors: \n\n" + errors.toString());
            }

            Plot plot = plotType.getPlot();
            plot.setData(data);
            DataChangeObserver dataChangeObserver = new DataChangeObserver(table, plot);
            table.addObserver(dataChangeObserver);

            // @TODO Update plot on data changes

            if (properties == null) {
                properties = new DefaultProperties();
            }

            properties.put(TITLE, table.getLabel());
            // @TODO assign X and Y axis labels
            properties.put(UNCERTAINTY, table.getUncertainty().getMultiplier());
            if (table.getVariableColumnMap().containsKey(Variables.X)) {
                properties.put(X_AXIS, table.getColumnForVariable(Variables.X).getLabel());
            }
            if (table.getVariableColumnMap().containsKey(Variables.Y)) {
                properties.put(Y_AXIS, table.getColumnForVariable(Variables.Y).getLabel());
            }
            plot.setProperties(properties);
            TopsoilPlotView plotView = new TopsoilPlotView(plot);

            // Connect table model to properties panel
            PlotPropertiesPanel panel = plotView.getPropertiesPanel();
            panel.isotopeSystemProperty().bindBidirectional(table.isotopeSystemProperty());


            // Update properties panel with changes in the plot
            PlotObservationThread observationThread = new PlotObservationThread();
            ScheduledExecutorService observer = observationThread.initializePlotObservation(plot, panel);

            Scene scene = new Scene(plotView, DEFAULT_PLOT_WIDTH, DEFAULT_PLOT_HEIGHT);
            Stage plotStage = new PlotStage(plotType);
            plotStage.setScene(scene);
            plotStage.getIcons().add(Topsoil.getLogo());
            plotStage.titleProperty().bind(Bindings.createStringBinding(
                    () -> plotType.getName() + ": " + panel.getPlotTitle(), panel.plotTitleProperty()));
            plotStage.setOnCloseRequest(closeEvent -> {
                observer.shutdown();
                table.deleteObserver(dataChangeObserver);
                ProjectManager.deregisterOpenPlot(table, plotType);
            });

            // Show Plot
            plotStage.show();
//            Platform.runLater(() -> ((SimplePlot) plot).resize());
            ProjectManager.registerOpenPlot(table, plotView);
        }
        return true;
    }
}
