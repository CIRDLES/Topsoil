package org.cirdles.topsoil.app.file;

import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.util.ExampleData;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SerializationTest {

    @Test
    public void serialization_test() {
        try {
            Path path = Files.createTempFile(null, null);
            TopsoilProject project = new TopsoilProject(ExampleData.UPB.getDataTable());
            ProjectSerializer.serialize(path, project);
            assertEquals(project, ProjectSerializer.deserialize(path));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_nullPath_test() {
        try {
            TopsoilProject project = new TopsoilProject(ExampleData.UPB.getDataTable());
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
