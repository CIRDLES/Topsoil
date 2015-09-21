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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.dataset.reader.TsvRawDataReader;
import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.RawData;
import org.cirdles.topsoil.dataset.SimpleDataset;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasItems;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;

/**
 *
 * @author John Zeringue
 */
public class TsvTableTest extends ApplicationTest {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(TsvTableTest.class);

    private static final String RAW_DATA = "" +
            "207Pb*/235U\t±2σ (%)\t206Pb*/238U\t±2σ (%)\tcorr coef\n" +
            "29.165688743\t1.519417676\t0.712165893\t1.395116767\t0.918191745\n" +
            "29.031535970\t1.799945600\t0.714916493\t1.647075269\t0.915069472\n" +
            "29.002008069\t1.441943510\t0.709482828\t1.324922704\t0.918845083\n" +
            "29.203969765\t1.320690194\t0.707078490\t1.216231698\t0.920906132\n" +
            "29.194452092\t1.359029744\t0.709615006\t1.248057588\t0.918344571\n" +
            "29.293320455\t1.424328137\t0.710934267\t1.309135282\t0.919124777\n" +
            "28.497489852\t1.353243890\t0.686951820\t1.245648095\t0.920490463\n" +
            "29.218573677\t1.383868032\t0.715702180\t1.271276031\t0.918639641\n" +
            "28.884872020\t1.264304654\t0.702153693\t1.164978444\t0.921438073\n" +
            "28.863259209\t1.455550200\t0.700081472\t1.335582301\t0.917579003\n" +
            "29.014325453\t1.614480021\t0.701464404\t1.478394505\t0.915709384\n" +
            "29.917885787\t1.564622589\t0.725185047\t1.434906094\t0.917094067\n" +
            "30.159907714\t1.488528691\t0.724886106\t1.366282212\t0.917874287\n" +
            "28.963153308\t1.480754780\t0.698240706\t1.359750830\t0.918282249\n" +
            "29.350104553\t1.513999270\t0.711983592\t1.384417989\t0.914411266\n" +
            "29.979576581\t1.595745814\t0.724426340\t1.458894294\t0.914239775\n" +
            "29.344673618\t1.551935035\t0.714166474\t1.420060290\t0.915025602";

    private TableView<Entry> tsvTable;

    private Parent getRootNode() throws Exception {
        RawData rawData = new TsvRawDataReader().read(RAW_DATA);
        Dataset dataset = new SimpleDataset("Test", rawData);
        tsvTable = new TsvTable(dataset);

        return tsvTable;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(getRootNode());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testCorrectlyLoadABasicDataset() {
        verifyThat(tsvTable, hasTableCell(29.165688743)); // the first number in the file
        verifyThat(tsvTable, hasTableCell(0.702153693)); // a number in the middle

        verifyThat(tsvTable, hasItems(17)); // RAW_DATA contains 17 lines
    }

}
