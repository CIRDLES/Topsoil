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
package org.cirdles.topsoil.app.dataset.writer;

import org.cirdles.topsoil.app.dataset.RawData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An interface implemented by classes which write {@code RawData} to {@code File}s.
 *
 * @author John Zeringue
 */
public interface RawDataWriter {

    /**
     * Writes the provided {@code RawData} to the specified destination.
     *
     * @param rawData   RawData
     * @param destination   OutputStream destination
     * @throws IOException  .
     */
    public void write(RawData rawData, OutputStream destination)
            throws IOException;

    /**
     * Attempts to write the provided {@code RawData} to a {@code String} using a  {@code ByteArrayOutputStream}.
     *
     * @param rawData   RawData
     * @return  String
     * @throws IOException  .
     */
    public default String write(RawData rawData) throws IOException {
        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            write(rawData, outputStream);
            return outputStream.toString();
        }
    }

    /**
     * Write the provided {@code RawData} to the specified destination.
     *
     * @param rawData   RawData
     * @param destination   File destination
     * @throws IOException  .
     * @throws FileNotFoundException    .
     */
    public default void write(RawData rawData, File destination)
            throws FileNotFoundException, IOException {
        try (OutputStream outputStream = new FileOutputStream(destination)) {
            write(rawData, outputStream);
        }
    }

    /**
     * Write the provided {@code RawData} to the specified destination.
     *
     * @param rawData   RawData
     * @param destination   Path destination
     * @throws IOException  .
     */
    public default void write(RawData rawData, Path destination)
            throws IOException {
        write(rawData, Files.newOutputStream(destination));
    }

}
