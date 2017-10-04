package org.cirdles.topsoil.app.plot;

import com.sun.javafx.stage.StageHelper;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.app.util.serialization.PlotInformation;
import org.cirdles.topsoil.plot.Plot;

import java.util.List;
import java.util.Map;

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
            generatePlot(tableController, TopsoilPlotType.BASE_PLOT);
        } else {
            generatePlot(tableController, TopsoilPlotType.BASE_PLOT);
        }
    }

    /**
     * Generates a saved {@code Plot} from a .topsoil file.
     *
     * @param tableController   the TopsoilTableController for the table
     * @param plotType  the TopsoilPlotType of the plot
     */
    public static void handlePlotGenerationFromFile(TopsoilTableController tableController, TopsoilPlotType plotType) {
        generatePlot(tableController, plotType);
    }

    /**
     * Generates a {@code Plot}.
     *
     * @param tableController   the TopsoilTableController for the table
     * @param plotType  the TopsoilPlotType of the plot
     */
    private static void generatePlot(TopsoilTableController tableController, TopsoilPlotType plotType) {

        List<Map<String, Object>> data = tableController.getPlotData();

        PlotPropertiesPanelController propertiesPanel = tableController.getTabContent().getPlotPropertiesPanelController();
        Map<String, Object> plotProperties = propertiesPanel.getProperties();

        Plot plot = plotType.getPlot();

        plot.setData(data);
        plot.setProperties(plotProperties);
        propertiesPanel.setPlot(plot);

        // Create Plot Scene
        Parent plotWindow = new PlotWindow(plot);
        Scene scene = new Scene(plotWindow, 900, 700);

        // Create Plot Stage
        Stage plotStage = new Stage();
        plotStage.setScene(scene);
//        plotStage.setResizable(false);
        plotStage.getIcons().add(MainWindow.getWindowIcon());

        // Connect Plot with PropertiesPanel
        plotStage.setTitle(plotType.getName() + ": " + propertiesPanel.getTitle());
        propertiesPanel.titleProperty().addListener(c -> {
            if (propertiesPanel.getTitle().length() > 0) {
                plotStage.setTitle(plotType.getName() + ": " + propertiesPanel.getTitle());
            } else {
                plotStage.setTitle(plotType.getName());
            }
        });

        plotStage.setOnCloseRequest(closeEvent -> {
            plot.stop();
            tableController.getTable().removeOpenPlot(plotType);
            propertiesPanel.removePlot();
        });

        // Show Plot
        plotStage.show();

        // Store plot information in TopsoilDataTable
        PlotInformation plotInfo = new PlotInformation(plot, plotType, propertiesPanel.getProperties(), plotStage);
        tableController.getTable().addOpenPlot(plotInfo);
    }
}
