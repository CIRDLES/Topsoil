package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.util.ExampleData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializableProjectTest {

    @Test
    public void reconstruct_test() {
        TopsoilProject project = new TopsoilProject(ExampleData.UPB.getDataTable());
        SerializableProject sProject = new SerializableProject(project);
        assertEquals(project, sProject.reconstruct());
    }

}
