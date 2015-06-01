/*
 * Copyright 2015 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.topsoil.app.table;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.stage.Stage;
import org.cirdles.TableSelector;
import org.cirdles.topsoil.app.TSVTable;
import org.cirdles.topsoil.app.TSVTableTest;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.dataset.reader.DatasetReader;
import org.cirdles.topsoil.app.dataset.reader.TSVDatasetReader;
import org.cirdles.topsoil.dataset.Dataset;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 *
 * @author parizotclement
 */
public class EntryTableColumnTest extends ApplicationTest {

    private TableSelector tableSelector;

    private Parent getRootNode() {
        // generate path to the sample TSV file
        Path sampleTSVPath = null;
        try {
            URI sampleTSV_URI = getClass().getResource("../sample.tsv").toURI();
            sampleTSVPath = Paths.get(sampleTSV_URI);
        } catch (URISyntaxException ex) {
            Logger.getLogger(TSVTableTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        TSVTable testTSVTable = new TSVTable();

        tableSelector = new TableSelector(testTSVTable);
        DatasetReader tableReader = new TSVDatasetReader(true);
        try {
            Dataset dataset = tableReader.read(sampleTSVPath);
            testTSVTable.setDataset(dataset);
        } catch (IOException ex) {
            Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return testTSVTable;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(getRootNode());
        stage.setScene(scene);
        stage.show();
    }

    //Test if the filter is correctly working on a non-number input
    @Test
    public void testInputNotANumber() {
        TableCell cell = tableSelector.cell(0, 0);

        //Get the value of the first cell, before any editing
        String value = cell.getText();

        doubleClickOn(cell) //Double-click on the first cell
                .write("test") //Write "test"
                .push(ENTER) //Press ENTER for committing
                .push(ENTER); //Press ENTER to close the Warning Dialog

        //Check that the first cell content hasn't been modified
        verifyThat(cell, hasText(value));
    }

    //Test if the filter is correctly working on a number input
    @Test
    public void testChangeCellValue() {
        TableCell cell = tableSelector.cell(0, 0);

        doubleClickOn(cell)
                .write("123")
                .push(ENTER);

        verifyThat(cell, hasText("123.0"));
    }
}
