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

import javafx.scene.control.TableView;
import org.cirdles.topsoil.table.Record;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zeringue
 */
public class TSVTableReaderTest {
    
    public TSVTableReaderTest() {
    }

    /**
     * Test of read method, of class TSVTableReader.
     */
    @Test
    public void testRead() {
        // test a file with only headers
        String src = "A\tB\tC";
        TableView<Record> dest = new TableView<>();
        TableReader instance = new TSVTableReader(true);
        instance.read(src, dest);
        
        assertEquals("dest should have three columns", 3, dest.getColumns().size());
        assertTrue("dest should have zero rows", dest.getItems().isEmpty());
        
        // test a file with one line of data
        src = "1\t2\t3";
        dest = new TableView<>();
        instance = new TSVTableReader(false);
        instance.read(src, dest);
        
        assertEquals("dest should have three columns", 3, dest.getColumns().size());
        assertEquals("dest should have one row", 1, dest.getItems().size());
        assertEquals("dest should have a three at the end of the first row", 3., dest.getColumns().get(2).getCellData(0));
    }
    
}
