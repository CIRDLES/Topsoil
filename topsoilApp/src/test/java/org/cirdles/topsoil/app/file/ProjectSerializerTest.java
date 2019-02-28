package org.cirdles.topsoil.app.file;

import javafx.stage.Stage;
import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.parser.Squid3DataParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author marottajb
 */
public class ProjectSerializerTest extends ApplicationTest {

    static TopsoilProject project;
    static DataTable table;

    static String CONTENT = (
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
    }

    @Before
    public void setupTest() {
        DataParser dataParser = new Squid3DataParser();
        table = dataParser.parseDataTable(CONTENT, ",", "CONTENT");
        project = new TopsoilProject(table);
    }

    @Test
    public void serialization_test() {
        try {
            Path tempPath = Files.createTempFile(null, ".topsoil");
            ProjectSerializer.serialize(tempPath, project);
            TopsoilProject tP = ProjectSerializer.deserialize(tempPath);
            DataTable after = tP.getDataTables().get(0);

            Assert.assertEquals(table, after);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
