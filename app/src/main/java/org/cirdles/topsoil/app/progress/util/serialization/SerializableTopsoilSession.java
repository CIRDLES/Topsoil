package org.cirdles.topsoil.app.progress.util.serialization;

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.dataset.field.NumberField;
import org.cirdles.topsoil.app.plot.*;
import org.cirdles.topsoil.app.progress.TopsoilRawData;
import org.cirdles.topsoil.app.progress.dataset.NumberDataset;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.menu.MenuItemEventHandler;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTab;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilPlotEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.table.TopsoilTableController;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

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

    // TODO Check all casts and map calls.
    
    private static final long serialVersionUID = -1395082509985011027L;
    private List<Map<String, Serializable>> data;

    private static Map<String, IsotopeType> ISOTOPE_TYPES;
    private static Map<String, TopsoilPlotType> TOPSOIL_PLOT_TYPES;
    private static Map<String, Variable<Number>> VARIABLES;
    private static Map<String, VariableFormat<Number>> VARIABLE_FORMATS;
    static {
        ISOTOPE_TYPES = new HashMap<>();
        for (IsotopeType type : IsotopeType.ISOTOPE_TYPES) {
            ISOTOPE_TYPES.put(type.getAbbreviation(), type);
        }
        TOPSOIL_PLOT_TYPES = new HashMap<>();
        for (TopsoilPlotType type : TopsoilPlotType.TOPSOIL_PLOT_TYPES) {
            TOPSOIL_PLOT_TYPES.put(type.getName(), type);
        }
        VARIABLES = new HashMap<>();
        for (Variable v : Variables.VARIABLE_LIST) {
            VARIABLES.put(v.getName(), v);
        }
        VARIABLE_FORMATS = new HashMap<>();
        for (VariableFormat vf : VariableFormats.UNCERTAINTY_FORMATS) {
            VARIABLE_FORMATS.put(vf.getName(), vf);
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
            this.storeTable(tab.getTableController());
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

            TopsoilTableController tableController = tabs.getSelectedTab().getTableController();
            tableController.getTabContent().setXUncertainty(VARIABLE_FORMATS.get(tableData.get("X Variable Format " +
                                                                                              "Name")));
            tableController.getTabContent().setYUncertainty(VARIABLE_FORMATS.get(tableData.get("Y Variable Format " +
                                                                                               "Name")));

            for (HashMap<String, Serializable> plot : (ArrayList<HashMap>) tableData.get("Plots")) {
                this.restorePlot(tableController, plot);
            }
        }
    }

    /**
     * Takes data from a <tt>TopsoilTable</tt> and puts it into a
     * <tt>HashMap</tt>, ensuring that the data is serializable.
     *
     * @param table the TopsoilTable containing the target data
     */
    private void storeTable(TopsoilTableController tableController) {
        HashMap<String, Serializable> tableData = new HashMap<>();

        // Title of the TopsoilTable
        tableData.put("Title", tableController.getTable().getTitle());

        // Arrangement of the TableView's headers
        tableData.put("Headers", tableController.getTable().getColumnNames());

        // IsotopeType of TopsoilTable
        tableData.put("IsotopeType", tableController.getTable().getIsotopeType().getAbbreviation());

        // Data stored in TopsoilTable
        tableData.put("Data", new ArrayList<>(tableController.getTable().getDataAsArrays()));

        // Plots which visualize data in this table
        ArrayList<HashMap<String, Serializable>> plots = new ArrayList<>();
        for (PlotInformation plotInfo : tableController.getTable().getOpenPlots()) {
            plots.add(convertPlotInformation(plotInfo));
        }
        tableData.put("Plots", plots);

        tableData.put("X Variable Format Name", tableController.getTabContent().getXUncertainty().getName());
        tableData.put("Y Variable Format Name", tableController.getTabContent().getYUncertainty().getName());

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

        plotOptions.put("Topsoil Plot Type", plotInfo.getTopsoilPlotType().getName());

        // Convert VariableBindings
        // TODO Variable bindings should be stored.
//        HashMap<String, Serializable> bindings = new HashMap<>();
//        for (VariableBinding binding : plotInfo.getVariableBindings()) {
//            HashMap<String, Serializable> bindingMap = new HashMap<>();
//            bindingMap.put("Variable Name", binding.getVariable().getName());
//            bindingMap.put("Field Name", binding.getField().getName());
//            bindingMap.put("Variable Format Name", binding.getFormat().getName());
//            System.out.println("Format: " + bindingMap.get("Variable Format Name"));
//            bindings.put(binding.getVariable().getName(), bindingMap);
//        }
//        plotOptions.put("Variable Bindings", bindings);

        plotOptions.put("Properties", (HashMap<String, Object>) plotInfo.getPlotProperties());

        return plotOptions;
    }

    private void restorePlot(TopsoilTableController tableController, HashMap<String, Serializable> plot) {

        List<Field<Number>> fields = tableController.getTable().getFields();

        List<TopsoilDataEntry> dataEntries = tableController.getTable().getCopyOfDataAsEntries();
        List<Entry> plotEntries = new ArrayList<>();

        // Copy TopsoilDataEntries into TopsoilPlotEntries
        for (int i = 0; i < dataEntries.size(); i ++) {
            plotEntries.add(new TopsoilPlotEntry());
            for (int j = 0; j < tableController.getTable().getColumnNames().length; j++) {
                double currentValue = dataEntries.get(i).getProperties().get(j).getValue();
                plotEntries.get(i).set(fields.get(j), currentValue);
            }
        }

        NumberDataset dataset = new NumberDataset(tableController.getTable().getTitle(), new TopsoilRawData<>(fields,
                                                                                                    plotEntries));
        PlotContext plotContext = new SimplePlotContext(dataset);

        /*
        TODO Variable Bindings should be restored from information in the serialized file. Right now, they are
        TODO re-formed from the order of columns in the TableView.
         */
//        // Bind Variables
//        Map<String, Serializable> bindingMaps = (Map<String, Serializable>) plot.get("Variable Bindings");
//        Collection<VariableBinding> bindings = new ArrayList<>();
//        for (Serializable b : bindingMaps.values()) {
//            Map<String, Serializable> bindingMap = (Map<String, Serializable>) b;
//            Variable<Number> variable = VARIABLES.get((String) bindingMap.get("Variable Name"));
//            Field<Number> field = new NumberField((String) bindingMap.get("Field Name"));
//            VariableFormat<Number> format;
//            if (bindingMap.get("Variable Format Name").equals("Identity")) {
//                format = new IdentityVariableFormat<>();
//            } else {
//                format = VARIABLE_FORMATS.get((String) bindingMap.get("Variable Format Name"));
//            }
//            plotContext.addBinding(variable, field, format);
//        }

        for (int i = 0; i < Variables.VARIABLE_LIST.size(); i++) {
            Variable<Number> variable = Variables.VARIABLE_LIST.get(i);
            Field<Number> field = fields.get(i);
            VariableFormat<Number> format;
            if (variable == Variables.SIGMA_X) {
                format = tableController.getTabContent().getXUncertainty();
            } else if (variable == Variables.SIGMA_Y) {
                format = tableController.getTabContent().getYUncertainty();
            } else {
                format = variable.getFormats().size() > 0 ? variable.getFormats().get(0) : null;
            }
            plotContext.addBinding(variable, field, format);

        }

        Map<String, Object> plotProperties = (Map<String, Object>) plot.get("Properties");

        MenuItemEventHandler.restorePlot(tableController, TOPSOIL_PLOT_TYPES.get(plot.get("Topsoil Plot Type")),
                                         plotContext, plotProperties);
    }
}
