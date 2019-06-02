package org.cirdles.topsoil.app.file;

import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.app.data.FXDataColumn;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.file.serialization.ProjectSerializer;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.data.ExampleData;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SerializationTest {

    @Test
    public void serialization_test() {
        try {
            Path path = Files.createTempFile(null, null);
            DataTable table = ExampleData.UPB.getDataTable();
            TopsoilProject project = new TopsoilProject(new FXDataTable(table));

            project.setLambdaValue(Lambda.U234, 0.0);

            ProjectSerializer.serialize(path, project);
            TopsoilProject reconstructed = ProjectSerializer.deserialize(path);

            for (Lambda lambda : Lambda.values()) {
                assertEquals(project.getLambdaValue(lambda), reconstructed.getLambdaValue(lambda));
            }

            DataTable newTable = reconstructed.getDataTables().get(0);

            assertEquals(table.getTitle(), newTable.getTitle());
            assertEquals(table.getTemplate(), newTable.getTemplate());
            assertEquals(table.getUncertainty(), newTable.getUncertainty());

            // @TODO Check data

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_nullPath_test() {
        try {
            TopsoilProject project = new TopsoilProject(new FXDataTable(ExampleData.UPB.getDataTable()));
            ProjectSerializer.serialize(null, project);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_nullProject_test() {
        try {
            Path path = Files.createTempFile(null, null);
            ProjectSerializer.serialize(path, null);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

}
