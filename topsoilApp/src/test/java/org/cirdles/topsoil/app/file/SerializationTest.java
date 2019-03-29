package org.cirdles.topsoil.app.file;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.util.ExampleData;
import org.cirdles.topsoil.variable.Variable;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SerializationTest {

    @Test
    public void serialization_test() {
        try {
            Path path = Files.createTempFile(null, null);
            DataTable table = ExampleData.UPB.getDataTable();
            TopsoilProject project = new TopsoilProject(table);
            ProjectSerializer.serialize(path, project);
            TopsoilProject reconstructed = ProjectSerializer.deserialize(path);

            DataTable newTable = reconstructed.getDataTables().get(0);

            assertEquals(table.getLabel(), newTable.getLabel());
            assertEquals(table.getTemplate(), newTable.getTemplate());
            assertEquals(table.getUncertainty(), newTable.getUncertainty());
            assertEquals(table.getIsotopeSystem(), newTable.getIsotopeSystem());

            testVariableAssignments(table, newTable);

            // @TODO Check data

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

    private void testVariableAssignments(DataTable expected, DataTable actual) {
        assertEquals(expected.getVariableColumnMap().size(), actual.getVariableColumnMap().size());
        DataColumn<?> expectedColumn, actualColumn;
        for (Map.Entry<Variable<?>, DataColumn<?>> entry : expected.getVariableColumnMap().entrySet()) {
            expectedColumn = entry.getValue();
            actualColumn =  actual.getVariableColumnMap().get(entry.getKey());
            assertEquals(expectedColumn.getLabel(), actualColumn.getLabel());
            assertEquals(expectedColumn.getType(), actualColumn.getType());
        }
    }

}
