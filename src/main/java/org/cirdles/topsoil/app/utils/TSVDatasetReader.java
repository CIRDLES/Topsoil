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
package org.cirdles.topsoil.app.utils;

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.cirdles.topsoil.data.Field;
import org.cirdles.topsoil.data.NumberField;
import org.cirdles.topsoil.data.Entry;
import org.cirdles.topsoil.data.Dataset;
import org.cirdles.topsoil.data.SimpleDataset;
import org.cirdles.topsoil.data.TextField;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class TSVDatasetReader implements DatasetReader {

    private final boolean expectingHeaders;

    public TSVDatasetReader(boolean expectingHeaders) {
        this.expectingHeaders = expectingHeaders;
    }

    private static boolean isNumber(String string) {
        try {
            Double.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Dataset read(InputStream source) throws IOException {
        CSVReader tsvReader = new CSVReader(new InputStreamReader(source, "utf-8"), '\t');

        List<String[]> lines = tsvReader.readAll();

        if (!isSquare(lines)) {
            throw new IllegalArgumentException("TSV data must be square");
        }

        if (lines.isEmpty()) {
            return Dataset.emptyDataset();
        }

        int headerCount = lines.get(0).length;
        List<Field> fields = new ArrayList<>(headerCount);

        for (int i = 0; i < headerCount; i++) {
            final String fieldName
                    = expectingHeaders ? lines.get(0)[i] : "Field " + i;

            if (fieldType(lines, i) == NumberField.class) {
                fields.add(new NumberField(fieldName));
            } else if (fieldType(lines, i) == TextField.class) {
                fields.add(new TextField(fieldName));
            }
        }

        List<Entry> entries = new ArrayList<>(lines.size() - firstRow());
        for (int i = firstRow(); i < lines.size(); i++) {
            Entry entry = new Entry();

            for (int j = 0; j < fields.size(); j++) {
                Field currentField = fields.get(j);
                entry.setValue(currentField,
                        currentField.getStringConverter().fromString(lines.get(i)[j]));
            }

            entries.add(entry);
        }

        return new SimpleDataset(fields, entries);
    }

    int firstRow() {
        return expectingHeaders ? 1 : 0;
    }

    Class<? extends Field> fieldType(List<String[]> lines, int column) {
        Class<? extends Field> fieldType = NumberField.class;

        for (int i = firstRow(); i < lines.size(); i++) {
            if (!isNumber(lines.get(i)[column])) {
                fieldType = TextField.class;
                break;
            }
        }

        return fieldType;
    }

    boolean isSquare(List<String[]> lines) {
        boolean isSquare = true;

        if (!lines.isEmpty()) {
            int expectedLineLength = lines.get(0).length;

            for (String[] line : lines) {
                if (line.length != expectedLineLength) {
                    isSquare = false;
                    break;
                }
            }
        }

        return isSquare;
    }

}
