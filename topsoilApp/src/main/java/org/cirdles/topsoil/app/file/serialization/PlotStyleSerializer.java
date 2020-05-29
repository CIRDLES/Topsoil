package org.cirdles.topsoil.app.file.serialization;

import java.io.Serializable;

public class PlotStyleSerializer {

    public static void exportPlotStyle(Serializable serializableObject, String fileName) throws Exception {
        TopsoilFileSerializer.serializeObjectToFile(serializableObject, fileName);
    }

    public static Object importPlotStyle(String filename, boolean verbose) {
        return TopsoilFileSerializer.getSerializedObjectFromFile(filename, verbose);
    }
}
