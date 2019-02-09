package org.cirdles.topsoil.app.util.serialization.objects;

import org.cirdles.topsoil.app.model.DataTable;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.util.serialization.ObjectSerializer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;

/**
 * @author marottajb
 */
public class SerializableDataTableTest {

    static ObjectSerializer<SerializableDataTable> serializer;

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
        DataTable table = SampleData.UPB.getDataTable();
        serializer.serialize(new SerializableDataTable(table));

        Assert.assertEquals("Error reserializing file: " + serializer.getFile() ,table, serializer.deserialize().getDataTable());
    }

}
