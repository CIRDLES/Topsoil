package org.cirdles.topsoil.app.control.plot;

import org.apache.commons.lang3.Validate;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotOptions;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.javafx.PlotView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static org.cirdles.topsoil.plot.PlotOption.*;

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
     * @param table         DataTable
     * @param variableMap   Map of plotting Variables to table DataColumns
     * @param plotType      PlotType
     * @param options    plot options
     */
    public static void generatePlot(
            TopsoilProject project,
            FXDataTable table,
            Map<Variable<?>, DataColumn<?>> variableMap,
            PlotType plotType,
            PlotOptions options
    ) {
        Validate.notNull(project, "Project cannot be null.");
        Validate.notNull(table, "Data table cannot be null.");
        Validate.notNull(variableMap, "Variable map cannot be null.");
        Validate.notNull(plotType, "Plot type cannot be null.");

//        // Check if the requested plot type is already open for the table
//        if (project.getPlotMap().get(table).contains(plotType)) {
//            TopsoilNotification.info(
//                    "Plot Already Exists",
//                    "A plot with plot type \"" + plotType.getName() +
//                            "\" is already open for table \"" + table.getTitle() + "\"."
//            );
//            return;
//        }

        // Notify the user of any invalid data values
//        List<String> dataErrors = DataUtils.getDataErrors(table);
//        if (dataErrors.size() > 0) {
//            StringJoiner errors = new StringJoiner("\n");
//            for (String err : dataErrors) {
//                errors.add(err);
//            }
//            // @TODO Encapsulate reasons for invalid rows; right now there's only one:
//            TopsoilNotification.error("Invalid Rows", "The following rows have errors: \n\n" + errors.toString());
//        }

        // Assign plot options
        if (options == null) {
            options = PlotOptions.defaultOptions();
            options.put(TITLE, table.getTitle());
            options.put(X_AXIS, variableMap.get(Variable.X).getTitle());
            options.put(Y_AXIS, variableMap.get(Variable.Y).getTitle());
        }

        // Construct Plot
        PlotView plot = new FXPlotView(plotType, table, variableMap, options);
//        table.rowsProperty().addListener((ListChangeListener<? super FXDataRow>) c -> {
//            plot.setDataTable(table, plot.getVariableMap());
//        });

        // Setup plot window
        PlotStage plotStage = new PlotStage(plot, table);

        // Make the project aware that this plot is open
        project.registerOpenPlot(table, plot);
        plotStage.setOnCloseRequest(closeEvent -> project.deregisterOpenPlot(table, plot));

        // Show plot
        plotStage.show();
    }

    /**
     * Returns a list of {@code Variable}s which are required by the plot type, but not defined in the table.
     *
     * @param variableMap     Map of Variables to DataColumns
     * @param plotType        PlotType
     *
     * @return          List of missing Variables
     */
    public static List<Variable> findMissingVariables(Map<Variable<?>, DataColumn<?>> variableMap, PlotType plotType) {
        List<Variable> missing = new ArrayList<>();
        for (Variable v : plotType.getRequiredVariables()) {
            if (variableMap.get(v) == null) {
                missing.add(v);
            }
        }
        return missing;
    }

}
