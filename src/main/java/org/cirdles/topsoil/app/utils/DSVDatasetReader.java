/*
 * Copyright 2015 CIRDLES.
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
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.field.Field;
import org.cirdles.topsoil.dataset.field.NumberField;
import org.cirdles.topsoil.dataset.SimpleDataset;
import org.cirdles.topsoil.dataset.entry.SimpleEntry;
import org.cirdles.topsoil.dataset.field.TextField;

/**
 *
 * @author John Zeringue
 */
public class DSVDatasetReader implements DatasetReader {
    
    private char delimiter;
    private boolean expectingHeaders;
    
    public DSVDatasetReader(char delimiter) {
        this(delimiter, true);
    }

    public DSVDatasetReader(char delimiter, boolean expectingHeaders) {
        this.delimiter = delimiter;
        this.expectingHeaders = expectingHeaders;
    }

    boolean isSquare(List<String[]> lines) {
        boolean isSquare = true;

        if (!lines.isEmpty()) {
            int lengthOfFirstLine = lines.get(0).length;
            isSquare = lines.stream()
                    .allMatch(line -> line.length == lengthOfFirstLine);
        }

        return isSquare;
    }
    
    void validate(List<String[]> lines) {
        if (!isSquare(lines)) {
            throw new IllegalArgumentException("DSV data must be square");
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

    int indexOfFirstEntry() {
        return expectingHeaders ? 1 : 0;
    }

    Class<? extends Field> fieldType(List<String[]> lines, int column) {
        Class<? extends Field> fieldType = NumberField.class;

        for (int i = indexOfFirstEntry(); i < lines.size(); i++) {
            if (!isNumber(lines.get(i)[column])) {
                fieldType = TextField.class;
                break;
            }
        }

        return fieldType;
    }

    @Override
    public Dataset read(InputStream source) throws IOException {
        Reader reader = new InputStreamReader(source, Charset.forName("UTF-8"));
        CSVReader dsvReader = new CSVReader(reader, delimiter);

        List<String[]> lines = dsvReader.readAll();
        validate(lines);

        if (lines.isEmpty()) {
            return Dataset.EMPTY_DATASET;
        }

        int headerCount = lines.get(0).length;
        List<Field<?>> fields = new ArrayList<>(headerCount);

        for (int i = 0; i < headerCount; i++) {
            final String fieldName
                    = expectingHeaders ? lines.get(0)[i] : "Field " + i;

            if (fieldType(lines, i) == NumberField.class) {
                fields.add(new NumberField(fieldName));
            } else if (fieldType(lines, i) == TextField.class) {
                fields.add(new TextField(fieldName));
            }
        }

        List<Entry> entries = new ArrayList<>(lines.size() - indexOfFirstEntry());
        for (int i = indexOfFirstEntry(); i < lines.size(); i++) {
            Entry entry = new SimpleEntry();

            for (int j = 0; j < fields.size(); j++) {
                Field currentField = fields.get(j);
                entry.set(currentField,
                        currentField.getStringConverter().fromString(lines.get(i)[j]));
            }

            entries.add(entry);
        }

        return new SimpleDataset(fields, entries);
    }
    
}
