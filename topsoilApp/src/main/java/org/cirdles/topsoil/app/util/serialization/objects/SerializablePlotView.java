package org.cirdles.topsoil.app.util.serialization.objects;

import org.cirdles.topsoil.app.view.plot.TopsoilPlotView;
import org.cirdles.topsoil.plot.PlotProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.cirdles.topsoil.app.util.serialization.objects.SerializablePlotView.PlotKey.*;

/**
 * @author marottajb
 */
public class SerializablePlotView implements Serializable {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final Map<PlotKey, Serializable> data = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SerializablePlotView(TopsoilPlotView plotView) {
        data.put(PLOT_TYPE, plotView.getPlot().getPlotType());
        data.put(PLOT_PROPERTIES, (HashMap<PlotProperty, Object>) plotView.getPlot().getProperties());
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    public enum PlotKey {

        PLOT_TYPE,
        PLOT_PROPERTIES

    }
}
