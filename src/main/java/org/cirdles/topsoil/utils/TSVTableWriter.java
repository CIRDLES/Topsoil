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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.table.Record;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class TSVTableWriter implements TableWriter<Record> {

    private final boolean writeHeaders;

    // JFB
    // used to fill out missing columns
    private final int requiredColumnCount;

    public TSVTableWriter(boolean writeHeaders, int requiredColumnCount) {
        this.writeHeaders = writeHeaders;
        this.requiredColumnCount = requiredColumnCount;
    }

    @Override
    public void write(TableView<Record> src, Path dest) {
        CSVWriter tsvWriter;
        try {
            tsvWriter = new CSVWriter(new FileWriter(dest.toFile()), '\t');
        } catch (IOException ex) {
            Logger.getLogger(TSVTableWriter.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        int actualColumnCount = Math.max(requiredColumnCount, src.getColumns().size());

        if (writeHeaders) {
            String[] headers = new String[actualColumnCount];
            for (int i = 0; i < src.getColumns().size(); i++) {
                headers[i] = src.getColumns().get(i).getText();
            }

            for (int j = src.getColumns().size(); j < actualColumnCount; j++) {
                headers[j] = "fill-" + Integer.toString(j - src.getColumns().size() + 1);
            }

            tsvWriter.writeNext(headers);
        }

        for (int i = 0; i < src.getItems().size(); i++) {
            String[] row = new String[actualColumnCount];
            for (int j = 0; j < src.getColumns().size(); j++) {
                row[j] = String.valueOf(src.getColumns().get(j).getCellData(i));
            }

            for (int j = src.getColumns().size(); j < actualColumnCount; j++) {
                row[j] = "0.00";
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
