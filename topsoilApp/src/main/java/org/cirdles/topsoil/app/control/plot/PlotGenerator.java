package org.cirdles.topsoil.app.control.plot;

import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.data.DataUtils;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.plot.*;
import org.cirdles.topsoil.variable.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static org.cirdles.topsoil.plot.PlotProperties.*;

/**
 * A utility class providing helper methods for handling {@link Plot}s.
 *
 * @author marottajb
 */
public class PlotGenerator {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Generates and displays a plot.
     *
     * @param project       TopsoilProject
     * @param plotType      PlotType
     * @param table         DataTable
     * @param properties    plot properties
     */
    public static void generatePlot(TopsoilProject project, PlotType plotType, DataTable table, PlotProperties properties) {
        // Construct Plot
        Plot plot = plotType.getPlot();
        if (plot == null) {
            throw new IllegalArgumentException("Could not obtain plot instance from provided plot type: " + plotType);
        }

        // Check for required plotting variables
        List<Variable> missing = findMissingVariables(table, plotType);
        if (! missing.isEmpty()) {
            TopsoilNotification.error("Missing Variables", "The following variables must be assigned:\n\n" + missing.toString());
            return;
        }

        // Check if the requested plot type is already open for the table
        if (project.getOpenPlotTypesForTable(table).contains(plotType)) {
            TopsoilNotification.info("Plot Already Exists",
                    "A plot with plot type \"" + plotType.getName() + "\" is already open for table \"" + table.getLabel() + "\".");
            return;
        }

        // Get Plot-compatible data
        List<PlotDataEntry> data = DataUtils.getPlotData(table);

        // Notify the user of any invalid data values
        List<String> dataErrors = DataUtils.getDataErrors(table);
        if (dataErrors.size() > 0) {
            StringJoiner errors = new StringJoiner("\n");
            for (String err : dataErrors) {
                errors.add(err);
            }
            // @TODO Encapsulate reasons for invalid rows; right now there's only one:
            TopsoilNotification.error("Invalid Rows", "The following rows have errors: \n\n" + errors.toString());
        }

        // Set the plot data
        plot.setData(data);

        // Listen for changes in the DataTable; update plot with new data
        table.addListener(c -> plot.setData(DataUtils.getPlotData(table)));

        // Assign plot properties
        if (properties == null) {
            properties = PlotProperties.defaultProperties();
            syncProperties(properties, project, table);  // Set data-dependent properties
        }
        plot.setProperties(properties);

        // Setup plot window
        PlotStage plotStage = new PlotStage(plot, table);

        // Make the project aware that this plot is open
        project.registerOpenPlot(table, plot);
        plotStage.setOnCloseRequest(closeEvent -> project.deregisterOpenPlot(table, plotType));

        // Show plot
        plotStage.show();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Returns a list of {@code Variable}s which are required by the plot type, but not defined in the table.
     *
     * @param table     DataTable
     * @param plotType  PlotType
     *
     * @return          List of missing Variables
     */
    private static List<Variable> findMissingVariables(DataTable table, PlotType plotType) {
        List<Variable> missing = new ArrayList<>();
        for (Variable v : plotType.getRequiredVariables()) {
            if (table.getColumnForVariable(v) == null) {
                missing.add(v);
            }
        }
        return missing;
    }

    /**
     * Assigns data-specific plot properties to the provided {@code PlotProperties} instance based on the table.
     *
     * @param properties    PlotProperties instance
     * @param table         DataTable
     */
    private static void syncProperties(PlotProperties properties, TopsoilProject project, DataTable table) {
        // Sync properties from table
        properties.set(TITLE, table.getLabel());
        properties.set(ISOTOPE_SYSTEM, table.getIsotopeSystem());
        properties.set(UNCERTAINTY, table.getUncertainty());
        if (table.getVariableColumnMap().containsKey(Variables.X)) {
            properties.set(X_AXIS, table.getColumnForVariable(Variables.X).getLabel());
        }
        if (table.getVariableColumnMap().containsKey(Variables.Y)) {
            properties.set(Y_AXIS, table.getColumnForVariable(Variables.Y).getLabel());
        }

        // Sync lambdas
        Map<Lambda, Number> lambdas = project.getLambdas();
        properties.set(LAMBDA_U234, lambdas.get(Lambda.U234));
        properties.set(LAMBDA_U235, lambdas.get(Lambda.U235));
        properties.set(LAMBDA_U238, lambdas.get(Lambda.U238));
        properties.set(LAMBDA_TH230, lambdas.get(Lambda.Th230));
    }
}
