package org.cirdles.topsoil.app.util.serialization.objects;

import org.cirdles.topsoil.app.util.serialization.ObjectSerializer;
import org.cirdles.topsoil.plot.PlotProperty;
import org.cirdles.topsoil.plot.PlotType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * @author marottajb
 */
public class SerializablePlotDataTest {

    static ObjectSerializer<SerializablePlotData> serializer;

    @BeforeClass
    public static void setup() {
        try {
            serializer = new ObjectSerializer<>(Files.createTempFile(null, ".topsoil").toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void serialization_test() {
        HashMap<PlotProperty, Object> plotProperties = new HashMap<>();
        plotProperties.put(PlotProperty.ELLIPSES, true);
        SerializablePlotData plotData = new SerializablePlotData(PlotType.SCATTER, plotProperties);
        serializer.serialize(plotData);

        SerializablePlotData after = serializer.deserialize();
        Assert.assertEquals("Incorrect PlotType in reserialized file: " + serializer.getFile(),
                            plotData.getPlotType(),
                            after.getPlotType());
        Assert.assertEquals("Incorrect plot properties in reserialized file: " + serializer.getFile(),
                            plotData.getPlotProperties(),
                            after.getPlotProperties());
    }

}
