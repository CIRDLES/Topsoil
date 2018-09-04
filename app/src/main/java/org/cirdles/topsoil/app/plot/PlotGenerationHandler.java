package org.cirdles.topsoil.app.plot;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.app.tab.TopsoilDataView;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.plot.DefaultProperties;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperty;
import org.cirdles.topsoil.plot.TopsoilPlotType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static org.cirdles.topsoil.plot.PlotProperty.*;

/**
 * A class containing a set of methods for handling plot generation.
 *
 * @author marottajb
 */
public class PlotGenerationHandler {

    private static double DEFAULT_WIDTH = 1000;
    private static double DEFAULT_HEIGHT = 600;

    /**
     * Generates a plot for the selected {@code TopsoilTab}.
     *
     * @param tabs  the active TopsoilTabPane
     */
    public static void generatePlotForSelectedTab(TopsoilTabPane tabs) {
        TopsoilDataView dataView = tabs.getSelectedTab().getDataView();

        // Check for open plots.
        List<Stage> stages = StageHelper.getStages();
        if (stages.size() > 1) {
            for (TopsoilTab tab : tabs.getTopsoilTabs()) {
                if ( ! tab.getDataView().getData().getOpenPlots().isEmpty() ) {
                    for (TopsoilPlotType type : tab.getDataView().getData().getOpenPlots().keySet()) {
                        tab.getDataView().getData().removePlot(type);
                    }
                }
            }
            generatePlot(dataView, TopsoilPlotType.BASE_PLOT, null);
        } else {
            generatePlot(dataView, TopsoilPlotType.BASE_PLOT, null);
        }
    }

    public static void generatePlotForDataView(TopsoilDataView dataView, TopsoilPlotType plotType) {
        generatePlot(dataView, plotType, null);
    }

    private static void generatePlot(TopsoilDataView dataView, TopsoilPlotType plotType, Map<PlotProperty, Object> properties) {

        List<Map<String, Object>> data = dataView.getData().getPlotEntries();

        Plot plot = plotType.getPlot();
	    plot.setData(data);
	    // reload data on column insertion/deletion
	    dataView.getData().getDataColumns().addListener((ListChangeListener<TopsoilDataColumn>) c -> {
		    plot.setData(dataView.getData().getPlotEntries());
	    });
	    for (TopsoilDataColumn column : dataView.getData().getDataColumns()) {
	    	// reload data on row insertion/deletion
	    	column.addListener((ListChangeListener<DoubleProperty>) c -> plot.setData(dataView.getData().getPlotEntries()) );
	    	for (DoubleProperty property : column) {
	    		// reload data on data changed
	    		property.addListener(c -> plot.setData(dataView.getData().getPlotEntries()) );
		    }
	    }

	    if (properties == null) {
		    properties = new DefaultProperties();
	    }

	    properties.put(TITLE, dataView.getData().getTitle());
		// @TODO assign and Y axis labels
	    properties.put(UNCERTAINTY, dataView.getData().getUnctFormat().getValue());
	    TopsoilPlotView plotView = new TopsoilPlotView(plot);

	    // Connect table data to properties panel
	    PlotPropertiesPanel panel = plotView.getPropertiesPanel();
	    panel.isotopeSystemProperty().bindBidirectional(dataView.getData().isotopeTypeProperty());

        // Update properties panel with changes in the plot
        PlotObservationThread observationThread = new PlotObservationThread();
        ScheduledExecutorService observer = observationThread.initializePlotObservation(plot, panel);

	    Scene scene = new Scene(plotView, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	    Stage plotStage = new Stage();
	    plotStage.setScene(scene);
	    plotStage.getIcons().add(MainWindow.getWindowIcon());
	    plotStage.titleProperty().bind(Bindings.createStringBinding(
			    () -> plotType.getName() + ": " + panel.getPlotTitle(), panel.plotTitleProperty()));
        plotStage.setOnCloseRequest(closeEvent -> {
            observer.shutdown();
            plot.stop();
            dataView.getData().removePlot(plotType);
        });

        // Show Plot
        plotStage.show();

        // Store plot information in TopsoilDataTable
        dataView.getData().addPlot(plotType, plotView);
    }
}
