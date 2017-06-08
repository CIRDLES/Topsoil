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
package org.cirdles.topsoil.app.dataset.writer;

import au.com.bytecode.opencsv.CSVWriter;
import org.cirdles.topsoil.app.dataset.RawData;
import org.cirdles.topsoil.app.dataset.field.Field;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * A {@code RawDataWriter} for writing delimited data, using a {@code CSVWriter} with a custom delimiter.
 *
 * @author John Zeringue
 */
public class DsvRawDataWriter implements RawDataWriter {

    /**
     * The delimiter of the data.
     */
    private final char delimiter;

    /**
     * A {@code CSVWriter} used for writing data.
     */
    private CSVWriter dsvWriter;

    /**
     * Constructs a new {@code DsvRawDataWriter} using the specified delimiter.
     *
     * @param delimiter char delimiter
     */
    public DsvRawDataWriter(char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Returns the delimiter used by this {@code DsvRawDataWriter}.
     *
     * @return  char delimiter
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Writes the {@code Field}s for a {@code RawData} object.
     *
     * @param rawData   RawData
     */
    private void writeFields(RawData rawData) {
        String[] line = rawData.getFields().stream()
                .map(Field::getName)
                .toArray(String[]::new);

        dsvWriter.writeNext(line);
    }

    /**
     * Writes the {@code Entry}s for a {@code RawData} object.
     *
     * @param rawData   RawData
     */
    private void writeEntries(RawData rawData) {
        List<String[]> lines = rawData.getEntries().stream()
                .map(entry -> {
                    return rawData.getFields().stream()
                            .map(entry::get)
                            .map(value -> value
                                    .map(Object::toString)
                                    .orElse(""))
                            .toArray(String[]::new);
                })
                .collect(toList());

        dsvWriter.writeAll(lines);
    }

    /** {@inheritDoc}
     */
    @Override
    public void write(RawData rawData, OutputStream destination)
            throws IOException {

        Writer writer
                = new OutputStreamWriter(destination, Charset.forName("UTF-8"));

        dsvWriter = new CSVWriter(writer, getDelimiter());

        writeFields(rawData);
        writeEntries(rawData);

        dsvWriter.close();
        dsvWriter = null; // allow dsvWriter to be garbage collected
    }

}
