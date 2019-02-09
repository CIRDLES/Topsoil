package org.cirdles.topsoil.app.util.serialization;

import javafx.stage.Stage;
import org.cirdles.topsoil.app.model.*;
import org.cirdles.topsoil.app.util.file.DataParser;
import org.cirdles.topsoil.app.util.file.Squid3DataParser;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author marottajb
 */
public class ProjectSerializerTest extends ApplicationTest {

    TopsoilProject project;

    String CONTENT = (
            ",Cat1,,,Cat2,,Cat3\n" +
            ",,Col2,,,,\n" +
            ",Col1,Col2,,,,\n" +
            ",Col1,Col2,,Col4,,\n" +
            ",Col1,Col2,Col3,Col4,Col5,\n" +
            "Seg1,,,,,,\n" +
            "Seg1:Row1,1.0,2.0,3.0,4.0,5.0,\n" +
            "Seg2,,,,,,\n" +
            "Seg2:Row1,1.0,2.0,3.0,4.0,5.0,\n"
    );

    @Override
    public void start(Stage stage) {
        DataParser dataParser = new Squid3DataParser(CONTENT);
        DataTable table = dataParser.parseDataTable("TestTable");
        project = new TopsoilProject(table);
    }

    @Test
    public void serialization_test() {
        try {
            Path tempPath = Files.createTempFile(null, ".topsoil");
            DataTable before = project.getDataTableList().get(0);
            printDataTable(before);
            System.out.println();
            ProjectSerializer.serialize(tempPath, project);
            SerializableTopsoilProject sProject = ProjectSerializer.deserialize(tempPath);
            DataTable after = sProject.getTopsoilProject().getDataTableList().get(0);
            printDataTable(after);
            Assert.assertEquals(before, after);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printDataTable(DataTable table) {
        System.out.println("LABEL: " + table.getLabel());
        for (DataSegment seg : table.getChildren()) {
            System.out.println(("SEGMENT_LABEL: " + seg.getLabel()));
            for (DataRow row : seg.getChildren()) {
                System.out.println(row);
            }
        }
    }

}
