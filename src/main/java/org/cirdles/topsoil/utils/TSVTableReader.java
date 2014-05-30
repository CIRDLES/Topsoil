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
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.table.Field;
import org.cirdles.topsoil.table.NumberField;
import org.cirdles.topsoil.table.Record;
import org.cirdles.topsoil.table.RecordTableColumn;
import org.cirdles.topsoil.table.TextField;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class TSVTableReader extends TableReader<Record> {

    private final boolean expectingHeader;

    public TSVTableReader(boolean expectingHeader) {
        this.expectingHeader = expectingHeader;
    }

    @Override
    public void read(String src, TableView<Record> dest) {
        // not much to do for src = null or ""
        if (src == null || src.trim().equals("")) {
            return;
        }

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

        String[] header;
        int rowLength;
        Field[] fields;

        if (expectingHeader) {
            header = lines.remove(0);
            rowLength = header.length;
        } else {
            rowLength = lines.get(0).length;

            // generate default column names
            header = new String[rowLength];
            for (int i = 0; i < rowLength; i++) {
                header[i] = "Field " + (char) ('A' + i);
            }
        }

        fields = new Field[rowLength];
        for (int i = 0; i < rowLength; i++) {
            if (isNumber(lines.get(0)[i])) {
                fields[i] = new NumberField(header[i]);
            } else {
                fields[i] = new TextField(header[i]);
            }
            dest.getColumns().add(new RecordTableColumn<>(fields[i]));
        }

        for (String[] line : lines) {
            Record row = new Record();

            for (int i = 0; i < rowLength; i++) {
                try {
                    row.setValue(fields[i], fields[i].getStringConverter().fromString(line[i]));
                } catch (NumberFormatException e) {
                    row.setValue(fields[i], null);
                }
            }

            dest.getItems().add(row);
        }
    }

    private static boolean isNumber(String string) {
        try {
            Double.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
