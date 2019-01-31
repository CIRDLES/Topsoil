package org.cirdles.topsoil.app.util.serialization.objects;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.util.serialization.Serializer;
import org.cirdles.topsoil.app.view.TopsoilProjectView;
import org.cirdles.topsoil.app.view.plot.TopsoilPlotView;
import org.cirdles.topsoil.constants.Lambda;
import org.cirdles.topsoil.plot.AbstractPlot;

import java.io.Serializable;

import java.util.*;

import static org.cirdles.topsoil.app.util.serialization.objects.SerializableTopsoilProject.ProjectKey.*;

/**
 * @author marottajb
 *
 * @see Serializer
 */
public class SerializableTopsoilProject implements Serializable {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final Map<ProjectKey, Serializable> data = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SerializableTopsoilProject(TopsoilProjectView projectView) {
        data.put(LAMBDAS, extractLambdaSettings());
        ArrayList<SerializableDataTable> sTables = new ArrayList<>();
        ArrayList<SerializablePlotView> sPlots;
        SerializableDataTable sT;

        for (DataTable table : projectView.getDataTables()) {
            Map<AbstractPlot.PlotType, TopsoilPlotView> openPlots = projectView.getOpenPlots().columnMap().get(table);
            sPlots = new ArrayList<>();
            for (TopsoilPlotView plotView : openPlots.values()) {
                sPlots.add(new SerializablePlotView(plotView));
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

    public void reloadProjectToDataView(TopsoilProjectView projectView) {
        // TODO
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

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
