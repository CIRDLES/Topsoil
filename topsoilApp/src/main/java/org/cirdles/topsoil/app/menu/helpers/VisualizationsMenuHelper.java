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
import org.cirdles.topsoil.plot.*;
import org.cirdles.topsoil.variable.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ScheduledExecutorService;

import static org.cirdles.topsoil.plot.PlotProperties.*;

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
    public static boolean generatePlot(PlotType plotType, DataTable table, PlotProperties properties) {
        // Check for required plotting variables
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

            // Notify the user of any invalid data values
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

            table.addListener(c -> plot.setData(DataUtils.getPlotData(table)));

            if (properties == null) {
                properties = new PlotProperties();
            }

            // Set data-dependent properties
            properties.set(TITLE, table.getLabel());
            // @TODO assign X and Y axis labels
            properties.set(UNCERTAINTY, table.getUncertainty().getMultiplier());
            if (table.getVariableColumnMap().containsKey(Variables.X)) {
                properties.set(X_AXIS, table.getColumnForVariable(Variables.X).getLabel());
            }
            if (table.getVariableColumnMap().containsKey(Variables.Y)) {
                properties.set(Y_AXIS, table.getColumnForVariable(Variables.Y).getLabel());
            }
            plot.setProperties(properties);
            TopsoilPlotView plotView = new TopsoilPlotView(plot);

            // Update properties panel with changes in the table
            PlotPropertiesPanel panel = plotView.getPropertiesPanel();
            panel.isotopeSystemProperty().bindBidirectional(table.isotopeSystemProperty());


            // Update properties panel with changes in the plot
            PlotObservationThread observationThread = new PlotObservationThread();
            ScheduledExecutorService observer = observationThread.initializePlotObservation(plot, panel);

            // Setup plot Stage
            Scene scene = new Scene(plotView, DEFAULT_PLOT_WIDTH, DEFAULT_PLOT_HEIGHT);
            Stage plotStage = new PlotStage(plotType);
            plotStage.setScene(scene);
            plotStage.getIcons().add(Topsoil.getLogo());
            plotStage.titleProperty().bind(Bindings.createStringBinding(
                    () -> plotType.getName() + ": " + panel.getPlotTitle(), panel.plotTitleProperty()));
            plotStage.setOnCloseRequest(closeEvent -> {
                observer.shutdown();
                ProjectManager.deregisterOpenPlot(table, plotType);
            });

            // Show Plot
            plotStage.show();
            ProjectManager.registerOpenPlot(table, plotView);
        }
        return true;
    }
}
