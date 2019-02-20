package org.cirdles.topsoil.app.control.menu.helpers;

import com.google.common.collect.BiMap;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.util.PlotObservationThread;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.plot.*;
import org.cirdles.topsoil.variable.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static org.cirdles.topsoil.plot.PlotProperty.TITLE;
import static org.cirdles.topsoil.plot.PlotProperty.UNCERTAINTY;

/**
 * A utility class providing helper methods for the logic behind items in
 * {@link org.cirdles.topsoil.app.control.menu.TopsoilMenuBar}.
 *
 * @author marottajb
 */
public class VisualizationsMenuHelper {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final double DEFAULT_PLOT_WIDTH = 1000;
    private static final double DEFAULT_PLOT_HEIGHT = 600;

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Generates and displays a plot.
     *
     * @param plotType      PlotType
     * @param table         DataTable
     * @param project       TopsoilProject that the table belongs to
     * @param properties    plot properties
     *
     * @return              true if successful
     */
    public static boolean generatePlot(PlotType plotType, DataTable table, TopsoilProject project,
                                       Map<PlotProperty, Object> properties) {
        List<Map<String, Object>> data = getPlotDataFromTable(table);

        Plot plot = plotType.getPlot();
        plot.setData(data);

        // @TODO Update plot on model changes

        if (properties == null) {
            properties = new DefaultProperties();
        }

        properties.put(TITLE, table.getLabel());
        // @TODO assign X and Y axis labels
        properties.put(UNCERTAINTY, table.getUnctFormat().getMultiplier());
        TopsoilPlotView plotView = new TopsoilPlotView(plot, table);

        // Connect table model to properties panel
        PlotPropertiesPanel panel = plotView.getPropertiesPanel();
        panel.isotopeSystemProperty().bindBidirectional(table.isotopeSystemProperty());

        // Update properties panel with changes in the plot
        PlotObservationThread observationThread = new PlotObservationThread();
        ScheduledExecutorService observer = observationThread.initializePlotObservation(plot, panel);

        Scene scene = new Scene(plotView, DEFAULT_PLOT_WIDTH, DEFAULT_PLOT_HEIGHT);
        Stage plotStage = new Stage();
        plotStage.setScene(scene);
        plotStage.getIcons().add(Main.getController().getTopsoilLogo());
        plotStage.titleProperty().bind(Bindings.createStringBinding(
                () -> plotType.getName() + ": " + panel.getPlotTitle(), panel.plotTitleProperty()));
        plotStage.setOnCloseRequest(closeEvent -> {
            observer.shutdown();
//            plot.stop();
            VisualizationsMenuHelper.closePlot(plotType, table, project);
        });

        // Show Plot
        plotStage.show();

        project.addOpenPlot(plotType, table, plotView);
        return true;
    }

    /**
     * Closes a particular plot.
     *
     * @param plotType  PlotType
     * @param table     DataTable
     * @param project   TopsoilProject that table belongs to
     *
     * @return          true if successful
     */
    public static boolean closePlot(PlotType plotType, DataTable table, TopsoilProject project) {
        project.removeOpenPlot(plotType, table);
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
        List<DataSegment> tableAliquots = table.getChildren();
        BiMap<Variable<?>, DataColumn<?>> varMap = table.getVariableColumnMap();

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
                        if (var instanceof DependentVariable && Uncertainty.PERCENT_FORMATS.contains(table.getUnctFormat())) {
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
