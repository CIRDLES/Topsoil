package org.cirdles.topsoil.app.menu.helpers;

import com.google.common.collect.BiMap;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.util.PlotObservationThread;
import org.cirdles.topsoil.app.view.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.view.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.view.TopsoilProjectView;
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

    public static boolean generatePlot(AbstractPlot.PlotType plotType, DataTable dataTable, TopsoilProjectView dataView,
                                       Map<PlotProperty, Object> properties) {
        List<List<Map<String, Object>>> data = getPlotDataFromTable(dataTable, dataView);

        Plot plot = plotType.getPlot();
        plot.setData(data);

        // @TODO Update plot on data changes

        if (properties == null) {
            properties = new DefaultProperties();
        }

        properties.put(TITLE, dataTable.getLabel());
        // @TODO assign and Y axis labels
        properties.put(UNCERTAINTY, dataTable.getUnctFormat().getMultiplier());
        TopsoilPlotView plotView = new TopsoilPlotView(plot);

        // Connect table data to properties panel
        PlotPropertiesPanel panel = plotView.getPropertiesPanel();
        panel.isotopeSystemProperty().bindBidirectional(dataTable.isotopeSystemProperty());

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
            VisualizationsMenuHelper.closePlot(plotType, dataTable, dataView);
        });

        // Show Plot
        plotStage.show();

        dataView.addOpenPlot(plotType, dataTable, plotView);
        return true;
    }

    public static boolean closePlot(AbstractPlot.PlotType plotType, DataTable dataTable, TopsoilProjectView dataView) {
        dataView.removeOpenPlot(plotType, dataTable);
        return true;
    }

    public static List<List<Map<String, Object>>> getPlotDataFromTable(DataTable dataTable, TopsoilProjectView dataView) {
        List<List<Map<String, Object>>> plotData = new ArrayList<>();
        List<DataSegment> tableAliquots = dataTable.getChildren();
        BiMap<Variable, DataColumn> varMap = dataTable.getVariableColumnMap();

        List<DataRow> rows;
        DataColumn column;
        List<Map<String, Object>> plotAliquot;
        Map<String, Object> entry;

        for (DataSegment aliquot : tableAliquots) {
            rows = aliquot.getChildren();
            plotAliquot = new ArrayList<>();
            for (DataRow row : rows) {
                entry = new HashMap<>();

                entry.put(TextVariable.LABEL.getName(), row.getLabel());
                entry.put(TextVariable.ALIQUOT.getName(), aliquot.getLabel());
                // @TODO determine selected/unselected
                entry.put("Selected", true);

                for (Variable var : Variables.ALL) {
                    Object value;
                    if (varMap.containsKey(var)) {
                        column = varMap.get(var);
                        value = row.getValuePropertyForColumn(column).get();
                        if (var instanceof DependentVariable && UncertaintyFormat.PERCENT_FORMATS.contains(dataTable.getUnctFormat())) {
                            // @TODO The code below assumes that a dep-variable is always dependent on an ind-variable
                            double doubleVal = (double) value;
                            DependentVariable dependentVariable = (DependentVariable) var;
                            IndependentVariable dependency = (IndependentVariable) dependentVariable.getDependency();
                            DataColumn dependentColumn = varMap.get(dependency);
                            doubleVal /= 100;
                            doubleVal *= (Double) row.getValuePropertyForColumn(dependentColumn).get();
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
                plotAliquot.add(entry);
            }
            plotData.add(plotAliquot);
        }
        return plotData;
    }

}
