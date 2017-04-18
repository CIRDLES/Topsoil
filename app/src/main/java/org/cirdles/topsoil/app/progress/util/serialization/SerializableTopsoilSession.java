package org.cirdles.topsoil.app.progress.util.serialization;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.dataset.SimpleDataset;
import org.cirdles.topsoil.app.plot.PlotWindow;
import org.cirdles.topsoil.app.plot.SimplePlotContext;
import org.cirdles.topsoil.app.plot.Variable;
import org.cirdles.topsoil.app.plot.VariableBindingDialog;
import org.cirdles.topsoil.app.plot.VariableBindingDialogPane;
import org.cirdles.topsoil.app.progress.TopsoilRawData;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTab;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.plot.Plot;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A serializable class which stores information about all open tables and
 * plots for serialization to a .topsoil file. Data is read from a
 * <tt>TopsoilTabPane</tt>, whose <tt>TopsoilTab</tt>s each contain a
 * <tt>TopsoilTable</tt> for housing information about a specific table. Data
 * for each table is stored inside of a <tt>HashMap</tt>, then added to an
 * <tt>ArrayList</tt>. Data for plots are stored within the HashMap for the
 * table that they belong to.
 *
 * @author marottajb
 * @see TopsoilSerializer
 * @see TopsoilTabPane
 * @see TopsoilTab
 * @see TopsoilTable
 */
class SerializableTopsoilSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Map<String, Serializable>> data;

    private static Map<String, IsotopeType> ISOTOPE_TYPES;
    private static Map<String, TopsoilPlotType> TOPSOIL_PLOT_TYPES;
    static {
        ISOTOPE_TYPES = new HashMap<>();
        for (IsotopeType type : IsotopeType.ISOTOPE_TYPES) {
            ISOTOPE_TYPES.put(type.getAbbreviation(), type);
        }

        TOPSOIL_PLOT_TYPES = new HashMap<>();
        for (TopsoilPlotType type : TopsoilPlotType.TOPSOIL_PLOT_TYPES) {
            TOPSOIL_PLOT_TYPES.put(type.getName(), type);
        }
    }

    /**
     * Constructs an instance of <tt>SerializableTopsoilSession</tt>, using
     * the provided <tt>TopsoilTabPane</tt> as a data source.
     *
     * @param tabs  a TopsoilTabPane which contains tables
     */
    public SerializableTopsoilSession(TopsoilTabPane tabs) {
        this.data = new ArrayList<>();

        List<TopsoilTab> topsoilTabs = tabs.getTopsoilTabs();
        for (TopsoilTab tab : topsoilTabs) {
            this.storeTable(tab.getTopsoilTable());
        }
    }

//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//        ObjectStreamClass obj = ObjectStreamClass.lookup(
//                Class.forName(SerializableTopsoilSession.class.getCanonicalName()));
////        long svuid = obj.getSerialVersionUID();
////        System.out.println("Customized De-serialization of SerializableTopsoilSession: "
////                + svuid);
//    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Adds each stored <tt>TopsoilTable</tt> and their corresponding plots
     * to the specified <tt>TopsoilTabPane</tt>.
     *
     * @param tabs  the target TopsoilTabPane
     */
    public void loadDataToTopsoilTabPane(TopsoilTabPane tabs) {
        TopsoilTable table;
        for (Map<String, Serializable> tableData : this.data) {
            String[] headers = (String[]) tableData.get("Headers");

            ArrayList<Double[]> storedEntries = (ArrayList<Double[]>) tableData.get("Data");
            TopsoilDataEntry[] dataEntries = new TopsoilDataEntry[storedEntries.size()];
            for (int index = 0; index < storedEntries.size(); index++) {
                dataEntries[index] = new TopsoilDataEntry(storedEntries.get(index));
            }

            table = new TopsoilTable(
                    headers,
                    this.ISOTOPE_TYPES.get((String) tableData.get("IsotopeType")),
                    dataEntries
            );
            table.setTitle((String) tableData.get("Title"));
            tabs.add(table);

            for (HashMap<String, Serializable> plots : (ArrayList<HashMap>) tableData.get("Plots")) {
                this.openPlotWithOptions(table, plots);
            }
        }
    }

    /**
     * Takes data from a <tt>TopsoilTable</tt> and puts it into a
     * <tt>HashMap</tt>, ensuring that the data is serializable.
     *
     * @param table the TopsoilTable containing the target data
     */
    private void storeTable(TopsoilTable table) {
        HashMap<String, Serializable> tableData = new HashMap<>();

        // Title of the TopsoilTable
        tableData.put("Title", table.getTitle());

        // Arrangement of the TableView's headers
        tableData.put("Headers", table.getColumnNames());

        // IsotopeType of the TopsoilTable
        tableData.put("IsotopeType", table.getIsotopeType().getAbbreviation());

        // Data stored in the TableView
        tableData.put("Data", new ArrayList<>(table.getDataAsArrays()));

        // Plots which visualize data stored in the TableView
        ArrayList<HashMap<String, Serializable>> plots = new ArrayList<>();
        for (PlotInformation plotInfo : table.getOpenPlots()) {
            plots.add(convertPlotInformation(plotInfo));
        }
        tableData.put("Plots", plots);

        this.data.add(tableData);
    }

    /**
     * Converts <tt>PlotInformation</tt> stored within a
     * <tt>TopsoilTable</tt> into a <tt>HashMap</tt>.
     *
     * @param plotInfo  a PlotInformation object
     * @return  a HashMap<String, Serializable> containing plot information
     */
    private HashMap<String, Serializable> convertPlotInformation(PlotInformation plotInfo) {
        HashMap<String, Serializable> plotOptions = new HashMap<>();
        plotOptions.put("Topsoil Plot Typ" +
                "e", plotInfo.getTopsoilPlotType().getName());
        //TODO Store bindings for a table once binding is done prior to table
        // creation.
        plotOptions.put("Variable Bindings", plotInfo.getVariableBindingNames());
        plotOptions.put("Properties", plotInfo.getPlotProperties());

        return plotOptions;
    }

    //TODO Associate variable bindings with a table instead of a plot.
    /**
     * Called inside of loadDataToTopsoilPane(). Opens a plot which
     * was stored inside of a <tt>TopsoilTable</tt>. plotOptions should be a
     * Map following the same structure as the properties defined by that
     * plot type.
     *
     * FAIRLY HACKY: This method of opening plots assumes that the variable
     * bindings for each column in the table were left to their defaults.
     * Once the variable binding is done prior to table creation, and is more
     * easily accessible, this method should be updated, as well as this
     * class updated to store variable bindings for a table.
     *
     * @param table the TopsoilTable which the plot belongs to
     * @param plotOptions   a Map containing the stored plot properties
     */
    private void openPlotWithOptions(TopsoilTable table, Map<String, Serializable> plotOptions) {
        TopsoilPlotType plotType = this.TOPSOIL_PLOT_TYPES.get(plotOptions.get("Topsoil Plot Type"));
        List<Variable> variables = plotType.getVariables();
//        SimpleDataset dataset = new SimpleDataset(table.getTitle(), new TopsoilRawData(table).getRawData());
//        VariableBindingDialog variableBindingDialog = new VariableBindingDialog(variables, dataset);
//
//        // TODO Replace default variable bindings with selected bindings
//        List<Map<String, Object>> fieldData =
//                ((VariableBindingDialogPane)
//                        variableBindingDialog
//                                .getDialogPane()).getData();
//
//        Plot plot = plotType.getPlot();
//        plot.setData(fieldData);
//        plot.setProperties((Map<String, Object>) plotOptions.get("Properties"));
//
//        Parent plotWindow = new PlotWindow(plot, plotType.getPropertiesPanel());
//
//        SimplePlotContext plotContext =
//                (SimplePlotContext)
//                        ((VariableBindingDialogPane)
//                                variableBindingDialog
//                                        .getDialogPane()).getPlotContext();
//
//        // Store plot information in TopsoilTable
//        PlotInformation plotInfo = new PlotInformation(plot, plotType);
//        plotInfo.setVariableBindings(plotContext.getBindings());
//        table.addOpenPlot(plotInfo);
//
//        Scene scene = new Scene(plotWindow, 1200, 800);
//
//        Stage plotStage = new Stage();
//        plotStage.setTitle(plotType.getName() + ": " + table.getTitle());
//        plotStage.setOnCloseRequest(closeEvent -> table.removeOpenPlot(plotType));
//        plotInfo.setStage(plotStage);
//        plotStage.setScene(scene);
//        plotStage.show();
    }
}
