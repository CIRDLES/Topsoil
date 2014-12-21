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
package org.cirdles.topsoil.utils;

import javafx.scene.Parent;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.table.Record;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.loadui.testfx.Assertions.*;

import static org.loadui.testfx.controls.TableViews.*;
import org.loadui.testfx.GuiTest;

/**
 *
 * @author zeringue
 */
public class TSVTableReaderTest extends GuiTest {

    @Override
    protected Parent getRootNode() {
        TableView<Record> recordTable = new TableView<>();

        recordTable.setId("recordTable");
        new TSVTableReader(true).read("A\tB\tC", recordTable);

        return recordTable;
    }

    /**
     * Test of read method, of class TSVTableReader.
     */
    @Test
    public void testRead() {
        verifyThat(numberOfRowsIn("#recordTable"), is(0));
    }

}
