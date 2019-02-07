package org.cirdles.topsoil.app.util.serialization.objects;

import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.model.DataTable;
import org.cirdles.topsoil.app.model.TopsoilProject;
import org.cirdles.topsoil.app.control.menu.helpers.VisualizationsMenuHelper;
import org.cirdles.topsoil.app.util.serialization.ProjectSerializer;
import org.cirdles.topsoil.app.control.TopsoilProjectView;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.constant.Lambda;
import org.cirdles.topsoil.plot.PlotType;

import java.io.Serializable;

import java.util.*;

import static org.cirdles.topsoil.app.util.serialization.objects.SerializableTopsoilProject.ProjectKey.*;

/**
 * @author marottajb
 *
 * @see ProjectSerializer
 */
public class SerializableTopsoilProject implements Serializable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -3402100385336874762L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final Map<ProjectKey, Serializable> data = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SerializableTopsoilProject(TopsoilProject project) {
        data.put(LAMBDAS, extractLambdaSettings());
        ArrayList<SerializableDataTable> sTables = new ArrayList<>();
        ArrayList<SerializablePlotData> sPlots;
        SerializableDataTable sT;

        for (DataTable table : project.getDataTableList()) {
            Map<PlotType, TopsoilPlotView> openPlots = project.getOpenPlots().columnMap().get(table);
            sPlots = new ArrayList<>();
            if (openPlots != null) {
                for (TopsoilPlotView plotView : openPlots.values()) {
                    sPlots.add(new SerializablePlotData(plotView.getPlot().getPlotType(),
                                                        plotView.getPropertiesPanel().getPlotProperties()));
                }
            }

            sT = new SerializableDataTable(table);
            sT.setOpenPlotData(sPlots);
            sTables.add(new SerializableDataTable(table));
        }
        data.put(DATA_TABLES, sTables);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public TopsoilProject getTopsoilProjectObject() {
        List<DataTable> tables = new ArrayList<>();
        DataTable table;
        List<SerializableDataTable> sTables = (List<SerializableDataTable>) data.get(DATA_TABLES);
        for (SerializableDataTable sTable : sTables) {
            table = sTable.getDataTable();
            tables.add(table);
        }
        return new TopsoilProject(tables.toArray(new DataTable[]{}));
    }

    public boolean reloadProject() {
        boolean completed = false;
        // Reload lambdas
        Map<Lambda, Double> lambdas = (Map<Lambda, Double>) data.get(LAMBDAS);
        for (Map.Entry<Lambda, Double> entry : lambdas.entrySet()) {
            entry.getKey().setValue(entry.getValue());
        }

        // Reload model tables
        List<DataTable> tables = new ArrayList<>();
        Map<DataTable, List<SerializablePlotData>> plotsToOpen = new HashMap<>();
        DataTable table;
        List<SerializableDataTable> sTables = (List<SerializableDataTable>) data.get(DATA_TABLES);
        for (SerializableDataTable sTable : sTables) {
            table = sTable.getDataTable();
            plotsToOpen.put(table, new ArrayList<>());
            for (SerializablePlotData plotData : sTable.getOpenPlotData()) {
                plotsToOpen.get(table).add(plotData);
            }
            tables.add(table);
        }

        // Reload project
        TopsoilProject project = new TopsoilProject(tables.toArray(new DataTable[]{}));
        Main.getController().setProjectView(new TopsoilProjectView(project));

        // Reopen plots
        for (Map.Entry<DataTable, List<SerializablePlotData>> entry : plotsToOpen.entrySet()) {
            for (SerializablePlotData plotData : entry.getValue()) {
                VisualizationsMenuHelper.generatePlot(plotData.getPlotType(), entry.getKey(), project,
                                                      plotData.getPlotProperties());

            }
        }
        completed = true;
        return completed;
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//    }
//
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//    }

    private HashMap<Lambda, Double> extractLambdaSettings() {
        HashMap<Lambda, Double> valueMap = new HashMap<>();
        for (Lambda lambda : Lambda.values()) {
            valueMap.put(lambda, lambda.getValue());
        }
        return valueMap;
    }

    public enum ProjectKey implements SerializableDataKey<TopsoilProjectView> {
        LAMBDAS,
        DATA_TABLES
    }

}
