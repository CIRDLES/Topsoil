package org.cirdles.topsoil.app.util.serialization.objects;

import org.cirdles.topsoil.app.view.plot.TopsoilPlotView;
import org.cirdles.topsoil.plot.PlotProperty;
import org.cirdles.topsoil.plot.PlotType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.cirdles.topsoil.app.util.serialization.objects.SerializablePlotData.PlotKey.*;

/**
 * @author marottajb
 */
public class SerializablePlotData implements Serializable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -8066326764352461521L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final Map<PlotKey, Serializable> data = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SerializablePlotData(PlotType type, Map<PlotProperty, Object> properties) {
        data.put(PLOT_TYPE, type);
        data.put(PLOT_PROPERTIES, (HashMap<PlotProperty, Object>) properties);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public PlotType getPlotType() {
        return (PlotType) data.get(PLOT_TYPE);
    }

    public Map<PlotProperty, Object> getPlotProperties() {
        return (Map<PlotProperty, Object>) data.get(PLOT_PROPERTIES);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//    }
//
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    public enum PlotKey {

        PLOT_TYPE,
        PLOT_PROPERTIES

    }
}
