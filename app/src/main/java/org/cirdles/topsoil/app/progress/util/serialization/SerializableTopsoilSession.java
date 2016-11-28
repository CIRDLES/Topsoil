package org.cirdles.topsoil.app.progress.util.serialization;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SerializableTopsoilSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Map<String, Serializable>> data;
    private Map<String, IsotopeType> isotopeType;
    private Map<String, TopsoilPlotType> topsoilPlotType;

    public SerializableTopsoilSession(TopsoilTabPane tabs) {
        this.data = new ArrayList<>();

        this.isotopeType = new HashMap<>();
        this.topsoilPlotType = new HashMap<>();

        this.isotopeType.put("UPb", IsotopeType.UPb);
        this.isotopeType.put("UTh", IsotopeType.UTh);

        topsoilPlotType.put("Scatter Plot", TopsoilPlotType.SCATTER_PLOT);
        topsoilPlotType.put("Uncertainty Ellipse Plot", TopsoilPlotType.UNCERTAINTY_ELLIPSE_PLOT);
        topsoilPlotType.put("Evolution Plot", TopsoilPlotType.EVOLUTION_PLOT);

        List<TopsoilTab> topsoilTabs = tabs.getTopsoilTabs();
        for (TopsoilTab tab : topsoilTabs) {
            this.storeTable(tab.getTopsoilTable());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        ObjectStreamClass obj = ObjectStreamClass.lookup(
                Class.forName(SerializableTopsoilSession.class.getCanonicalName()));
//        long svuid = obj.getSerialVersionUID();
//        System.out.println("Customized De-serialization of SerializableTopsoilSession: "
//                + svuid);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

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
                    this.isotopeType.get((String) tableData.get("IsotopeType")),
                    dataEntries
            );
            table.setTitle((String) tableData.get("Title"));
            tabs.add(table);

            for (HashMap<String, Serializable> plots : (ArrayList<HashMap>) tableData.get("Plots")) {
                this.openPlotWithOptions(table, plots);
            }
        }
    }

    private void storeTable(TopsoilTable table) {
        HashMap<String, Serializable> tableData = new HashMap<>();

        // Title of the TopsoilTable
        tableData.put("Title", table.getTitle());

        // Arrangement of the TableView's headers
        String[] headers = new String[table.getTable().getColumns().size()];
        for (int i = 0; i < table.getTable().getColumns().size(); i++) {
            headers[i] = ((TableColumn) table.getTable().getColumns().get(i)).getText();
        }
        tableData.put("Headers", headers);

        // IsotopeType of the TopsoilTable
        tableData.put("IsotopeType", table.getIsotopeType().getAbbreviation());

        // Data stored in the TableView
        ArrayList<Double[]> tableEntries = new ArrayList<>();
        for (Object entry : table.getTable().getItems()) {
            tableEntries.add(((TopsoilDataEntry) entry).toArray());
        }
        tableData.put("Data", tableEntries);

        // Plots which visualize data stored in the TableView
        ArrayList<HashMap<String, Serializable>> plots = new ArrayList<>();
        for (PlotInformation plotInfo : table.getOpenPlots()) {
            plots.add(this.storePlotInformation(plotInfo));
        }
        tableData.put("Plots", plots);

        this.data.add(tableData);
    }

    private HashMap<String, Serializable> storePlotInformation(PlotInformation plotInfo) {
        HashMap<String, Serializable> plotOptions = new HashMap<>();
        plotOptions.put("Topsoil Plot Type", plotInfo.getTopsoilPlotType().getName());
        plotOptions.put("Variable Bindings", plotInfo.getVariableBindingNames());
        plotOptions.put("Properties", plotInfo.getPlotProperties());

        return plotOptions;
    }

    private void openPlotWithOptions(TopsoilTable table, Map<String, Serializable> plotOptions) {
        TopsoilPlotType plotType = this.topsoilPlotType.get(plotOptions.get("Topsoil Plot Type"));
        List<Variable> variables = plotType.getVariables();
        SimpleDataset dataset = new SimpleDataset(table.getTitle(), new TopsoilRawData(table).getRawData());
        VariableBindingDialog variableBindingDialog = new VariableBindingDialog(variables, dataset);

        // TODO Replace default variable bindings with selected bindings
        List<Map<String, Object>> fieldData =
                ((VariableBindingDialogPane)
                        variableBindingDialog
                                .getDialogPane()).getData();

        Plot plot = plotType.getPlot();
        plot.setData(fieldData);
        plot.setProperties((Map<String, Object>) plotOptions.get("Properties"));

        Parent plotWindow = new PlotWindow(
                plot, plotType.getPropertiesPanel());

        SimplePlotContext plotContext =
                (SimplePlotContext)
                        ((VariableBindingDialogPane)
                                variableBindingDialog
                                        .getDialogPane()).getPlotContext();

        // Store plot information in TopsoilTable
        PlotInformation plotInfo = new PlotInformation(plot, plotType);
        plotInfo.setVariableBindings(plotContext.getBindings());
        table.addOpenPlot(plotInfo);

        Scene scene = new Scene(plotWindow, 1200, 800);

        Stage plotStage = new Stage();
        plotStage.setTitle(plotType.getName() + ": " + table.getTitle());
        plotStage.setOnCloseRequest(closeEvent -> table.removeOpenPlot(plotType));
        plotStage.setScene(scene);
        plotStage.show();
    }
}
