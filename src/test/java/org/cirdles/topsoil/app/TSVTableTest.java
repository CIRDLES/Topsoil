/*
 * Copyright 2014 CIRDLES.
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
package org.cirdles.topsoil.app;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.cirdles.TableSelector;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.api.FxAssert.*;
import static org.testfx.matcher.control.TableViewMatchers.*;

/**
 *
 * @author John Zeringue
 */
public class TSVTableTest extends ApplicationTest {

    private static final Logger LOGGER
            = Logger.getLogger(TSVTableTest.class.getName());

    private TableView<Entry> tsvTable;
    private TableSelector<Entry> tableSelector;

    private Parent getRootNode() {
        // generate path to the sample TSV file
        Path sampleTSVPath = null;

        try {
            URI sampleTSV_URI = getClass().getResource("sample.tsv").toURI();
            sampleTSVPath = Paths.get(sampleTSV_URI);
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        tsvTable = new TSVTable(sampleTSVPath);
        tableSelector = new TableSelector(tsvTable);

        return tsvTable;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(getRootNode());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void tsvTable_should_correctlyLoadABasicTSVFile() {
        verifyThat(tsvTable, hasTableCell(29.165688743)); // the first number in the file
        verifyThat(tsvTable, hasTableCell(0.702153693)); // a number in the middle

        verifyThat(tsvTable, hasItems(17)); // sample.tsv contains 17 lines
    }   
}