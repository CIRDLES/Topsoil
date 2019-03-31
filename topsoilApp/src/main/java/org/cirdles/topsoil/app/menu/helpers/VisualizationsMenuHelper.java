package org.cirdles.topsoil.app.menu.helpers;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.ProjectManager;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.control.plot.PlotStage;
import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.util.PlotObservationThread;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.app.util.TableObserver;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.plot.*;
import org.cirdles.topsoil.variable.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static org.cirdles.topsoil.plot.PlotProperty.TITLE;
import static org.cirdles.topsoil.plot.PlotProperty.UNCERTAINTY;

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
        List<Variable<?>> required = Arrays.asList(IndependentVariable.X, IndependentVariable.Y);
        List<Variable<?>> missing = new ArrayList<>();
        for (Variable<?> v : required) {
            if (table.getVariableColumnMap().get(v) == null) {
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
            List<Map<String, Object>> data = getPlotDataFromTable(table);

            Plot plot = plotType.getPlot();
            plot.setData(data);
            TableObserver tableObserver = new TableObserver(table, plot);
            table.addObserver(tableObserver);

            // @TODO Update plot on model changes

            if (properties == null) {
                properties = new DefaultProperties();
            }

            properties.put(TITLE, table.getLabel());
            // @TODO assign X and Y axis labels
            properties.put(UNCERTAINTY, table.getUncertainty().getMultiplier());
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
                table.deleteObserver(tableObserver);
                ProjectManager.deregisterOpenPlot(table, plotType);
            });

            // Show Plot
            plotStage.show();
            ProjectManager.registerOpenPlot(table, plotView);
        }
        return true;
    }

    /**
     * Extracts and returns the relevant plot data from a {@code DataTable} in a format that a {@link Plot} expects.
     *
     * @param table     DataTable
     *
     * @return          plot data
     */
    public static List<Map<String, Object>> getPlotDataFromTable(DataTable table) {
        List<Map<String, Object>> plotData = new ArrayList<>();
        List<DataSegment> tableAliquots = table.getDataRoot().getChildren();
        Map<Variable<?>, DataColumn<?>> varMap = table.getVariableColumnMap();

        List<DataRow> rows;
        DataColumn column;
        Map<String, Object> entry;

        for (DataSegment aliquot : tableAliquots) {
            rows = aliquot.getChildren();
            for (DataRow row : rows) {
                entry = new HashMap<>();

                entry.put(TextVariable.LABEL.getName(), row.getLabel());
                entry.put(TextVariable.ALIQUOT.getName(), aliquot.getLabel());
                entry.put(BooleanVariable.SELECTED.getName(), row.isSelected());

                for (Variable var : Variables.ALL) {
                    Object value;
                    column = varMap.get(var);
                    if (column != null) {
                        column = varMap.get(var);
                        value = row.getValueForColumn(column).getValue();
                        if (var instanceof DependentVariable && Uncertainty.PERCENT_FORMATS.contains(table.getUncertainty())) {
                            // @TODO The code below assumes that a dep-variable is always dependent on an ind-variable
                            double doubleVal = (double) value;
                            DependentVariable dependentVariable = (DependentVariable) var;
                            IndependentVariable dependency = (IndependentVariable) dependentVariable.getDependency();
                            DataColumn dependentColumn = varMap.get(dependency);
                            doubleVal /= 100;
                            doubleVal *= (Double) row.getValueForColumn(dependentColumn).getValue();
                            value = doubleVal;
                        }
                    } else {
                        if (var instanceof IndependentVariable || var instanceof DependentVariable) {
                            value = 0.0;
                        } else {
                            value = "";
                        }
                    }
                    entry.put(var.getName(), value);
                }
                plotData.add(entry);
            }
        }
        return plotData;
    }
}
