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

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.chart.MapTableColumn;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class TSVTableReader extends TableReader {
    
    private final boolean expectingHeader;
    
    public TSVTableReader(boolean expectingHeader) {
        this.expectingHeader = expectingHeader;
    }

    @Override
    public void read(String src, TableView<Map> dest) {
        // not much to do for src = null or ""
        if (src == null || src.equals("")) {
            return;
        }
        
        // clear dest
        dest.getItems().clear();
        dest.getColumns().clear();
        
        CSVReader tsvReader = new CSVReader(new StringReader(src), '\t');
        List<String[]> lines;
        try {
            lines = tsvReader.readAll();
        } catch (IOException ex) {
            Logger.getLogger(TSVTableReader.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        String[] columnNames;
        int rowLength;
        
        if (expectingHeader) {
            columnNames = lines.remove(0);
            rowLength = columnNames.length;
        } else {
            rowLength = lines.get(0).length;
            
            // generate default column names
            columnNames = new String[rowLength];
            for (int i = 0; i < rowLength; i++) {
                columnNames[i] = "Column " + (char) ('A' + i);
            }
        }
        
        for (int i = 0; i < columnNames.length; i++) {
            dest.getColumns().add(new MapTableColumn<>(i, columnNames[i]));
        }
        
        for (String[] line : lines) {
            Map<Integer, Double> row = new HashMap();
            
            for (int i = 0; i < rowLength; i++) {
                row.put(i, Double.parseDouble(line[i])); 
            }
            
            dest.getItems().add(row);
        }
    }
}
