package org.cirdles.topsoil.app.plot;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.app.util.serialization.PlotInformation;
import org.cirdles.topsoil.plot.DefaultProperties;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static org.cirdles.topsoil.plot.PlotProperty.*;

/**
 * A class containing a set of methods for handling plot generation.
 *
 * @author Jake Marotta
 */
public class PlotGenerationHandler {

    /**
     * Generates a plot for the selected {@code TopsoilTab}.
     *
     * @param tabs  the active TopsoilTabPane
     */
    public static void handlePlotGenerationForSelectedTab(TopsoilTabPane tabs) {
        TopsoilTableController tableController = tabs.getSelectedTab().getTableController();

        // Check for open plots.
        List<Stage> stages = StageHelper.getStages();
        if (stages.size() > 1) {
            for (TopsoilTab tab : tabs.getTopsoilTabs()) {
                for (PlotInformation plotInfo : tab.getTableController().getTable().getOpenPlots()) {
                    tab.getTableController().getTable().removeOpenPlot(plotInfo.getTopsoilPlotType());
                    plotInfo.getStage().close();
                }
            }
            generatePlot(tableController, TopsoilPlotType.BASE_PLOT, null);
        } else {
            generatePlot(tableController, TopsoilPlotType.BASE_PLOT, null);
        }
    }

    /**
     * Generates a saved {@code Plot} from a .topsoil file.
     *
     * @param tableController   the TopsoilTableController for the table
     * @param plotType  the TopsoilPlotType of the plot
     */
    public static void handlePlotGenerationFromFile(TopsoilTableController tableController, TopsoilPlotType plotType,
                                                    Map<PlotProperty, Object> properties) {
        generatePlot(tableController, plotType, properties);
    }

    /**
     * Generates a {@code Plot}.
     *
     * @param tableController   the TopsoilTableController for the table
     * @param plotType  the TopsoilPlotType of the plot
     */
    private static void generatePlot(TopsoilTableController tableController, TopsoilPlotType plotType, Map<PlotProperty, Object> properties) {

        List<Map<String, Object>> data = tableController.getPlotData();

        Plot plot = plotType.getPlot();
	    plot.setData(data);

	    if (properties == null) {
		    properties = new DefaultProperties();
	    }
	    properties.put(TITLE, tableController.getTable().getTitle());
	    if (tableController.getAssignedVariables().contains(Variables.X)) {
		    properties.put(X_AXIS, tableController.getTable().getVariableAssignments().get(Variables.Y).getName());
	    }
	    if (tableController.getAssignedVariables().contains(Variables.Y)) {
	    	properties.put(Y_AXIS, tableController.getTable().getVariableAssignments().get(Variables.Y).getName());
	    }
	    properties.put(UNCERTAINTY, tableController.getTable().getUncertaintyFormat().getValue());
	    plot.setProperties(properties);

	    TopsoilPlotView plotView = new TopsoilPlotView(plot);

	    // Connect Plot with PropertiesPanel
	    PlotPropertiesPanel panel = plotView.getPropertiesPanel();
	    panel.isotopeSystemProperty().bindBidirectional(tableController.getTable().isotopeTypeObjectProperty());

        // Update properties panel with changes in the plot
        PlotObservationThread observationThread = new PlotObservationThread();
        ScheduledExecutorService observer = observationThread.initializePlotObservation(plot, panel);

	    // Create Plot Scene
	    Scene scene = new Scene(plotView, 1000, 600);

	    // Create Plot Stage
	    Stage plotStage = new Stage();
	    plotStage.setScene(scene);
	    plotStage.getIcons().add(MainWindow.getWindowIcon());
	    plotStage.titleProperty().bind(Bindings.createStringBinding(
			    () -> plotType.getName() + ": " + panel.getPlotTitle(), panel.plotTitleProperty()));
        plotStage.setOnCloseRequest(closeEvent -> {
            observer.shutdown();
            plot.stop();
            tableController.getTable().removeOpenPlot(plotType);
        });
//        plotStage.setOnShown((event) -> panel.refreshPlot() );

        // Show Plot
        plotStage.show();

        // Store plot information in TopsoilDataTable
        PlotInformation plotInfo = new PlotInformation(plot, plotType, FXCollections.observableMap(panel.getPlotProperties()), plotStage);
        tableController.getTable().addOpenPlot(plotInfo);
    }
}
