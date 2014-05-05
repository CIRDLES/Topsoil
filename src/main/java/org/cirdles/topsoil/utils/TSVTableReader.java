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

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
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
        
        Scanner in = new Scanner(src);
        
        String[] columnNames, rowBuffer = new String[0];
        int rowLength;
        
        if (expectingHeader) {
            columnNames = in.nextLine().split("\t");
            rowLength = columnNames.length;
        } else {
            rowBuffer = in.nextLine().split("\t");
            rowLength = rowBuffer.length;
            
            // generate default column names
            columnNames = new String[rowLength];
            for (int i = 0; i < rowLength; i++) {
                columnNames[i] = "Column " + (char) ('A' + i);
            }
        }
        
        for (int i = 0; i < columnNames.length; i++) {
            dest.getColumns().add(new MapTableColumn<>(i, columnNames[i]));
        }
        
        if (rowBuffer.length != 0) {
            Map<Integer, Double> row = new HashMap();
            
            for (int i = 0; i < rowLength; i++) {
                row.put(i, Double.parseDouble(rowBuffer[i]));
            }
            
            dest.getItems().add(row);
        }
        
        while (in.hasNextLine()) {
            Map<Integer, Double> row = new HashMap();
            rowBuffer = in.nextLine().split("\t");
            
            for (int i = 0; i < rowLength; i++) {
                row.put(i, Double.parseDouble(rowBuffer[i])); 
            }
            
            dest.getItems().add(row);
        }
    }
}
