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

import au.com.bytecode.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class TSVTableWriter implements TableWriter<Map> {
    
    private final boolean writeHeaders;

    public TSVTableWriter(boolean writeHeaders) {
        this.writeHeaders = writeHeaders;
    }

    @Override
    public void write(TableView<Map> src, Path dest) {
        CSVWriter tsvWriter;
        try {
            tsvWriter = new CSVWriter(new FileWriter(dest.toFile()), '\t');
        } catch (IOException ex) {
            Logger.getLogger(TSVTableWriter.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        if (writeHeaders) {
            String[] headers = new String[src.getColumns().size()];
            for (int i = 0; i < src.getColumns().size(); i++) {
                headers[i] = src.getColumns().get(i).getText();
            }
            
            tsvWriter.writeNext(headers);
        }
        
        for (int i = 0; i < src.getItems().size(); i++) {
            String[] row = new String[src.getColumns().size()];
            for (int j = 0; j < src.getColumns().size(); j++) {
                row[j] = String.valueOf(src.getColumns().get(j).getCellData(i));
            }
            
            tsvWriter.writeNext(row);
        }
        
        try {
            tsvWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(TSVTableWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
