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
package org.cirdles.topsoil.chart;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.TSVTable;
import org.cirdles.topsoil.app.TSVTableTest;
import org.cirdles.topsoil.app.table.Record;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

/**
 *
 * @author John Zeringue
 */
public class ChartInitializationViewTest extends GuiTest {

    @Override
    protected Parent getRootNode() {
        // generate path to the sample TSV file
        Path sampleTSVPath = null;
        try {
            URI sampleTSV_URI = getClass().getResource("sample.tsv").toURI();
            sampleTSVPath = Paths.get(sampleTSV_URI);
        } catch (URISyntaxException ex) {
            Logger.getLogger(TSVTableTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        TableView<Record> testTable = new TSVTable(sampleTSVPath);
        
        return new ChartInitializationView(testTable);
    }
    
}
