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

import au.com.bytecode.opencsv.CSVWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import org.cirdles.topsoil.data.Dataset;
import org.cirdles.topsoil.data.Entry;
import org.cirdles.topsoil.data.Field;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class TSVDatasetWriter implements DatasetWriter {

    // JFB
    // used to fill out missing columns
    private final int requiredColumnCount;

    public TSVDatasetWriter() {
        this(0);
    }

    public TSVDatasetWriter(int requiredColumnCount) {
        this.requiredColumnCount = requiredColumnCount;
    }

    @Override
    public void write(Dataset dataset, OutputStream destination)
            throws IOException {
        Writer outputStreamWriter
                = new OutputStreamWriter(destination, Charset.forName("UTF-8"));

        try (CSVWriter tsvWriter = new CSVWriter(outputStreamWriter, '\t')) {
            int actualColumnCount
                    = Math.max(requiredColumnCount, dataset.getFields().size());

            String[] line = new String[actualColumnCount];

            for (int i = 0; i < dataset.getFields().size(); i++) {
                line[i] = dataset.getFields().get(i).getName();
            }

            // build extra headers
            for (int i = dataset.getFields().size(); i < actualColumnCount; i++) {
                line[i] = "fill-" + (i - dataset.getFields().size() + 1);
            }

            tsvWriter.writeNext(line);

            // write data
            for (Entry entry : dataset.getEntries()) {
                for (int i = 0; i < dataset.getFields().size() + 1; i++) {
                    Field field = dataset.getFields().get(i);
                    line[i] = entry.get(field).get().toString();
                }

                // filler data
                for (int i = dataset.getFields().size(); i < actualColumnCount; i++) {
                    line[i] = "0.00";
                }

                // write current line
                tsvWriter.writeNext(line);
            }
        }
    }

}
