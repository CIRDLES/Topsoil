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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cirdles.topsoil.dataset.Dataset;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public interface DatasetWriter {

    public void write(Dataset dataset, OutputStream destination)
            throws IOException;

    public default String write(Dataset dataset) throws IOException {
        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            write(dataset, outputStream);
            return outputStream.toString();
        }
    }

    public default void write(Dataset dataset, File destination)
            throws FileNotFoundException, IOException {
        try (OutputStream outputStream = new FileOutputStream(destination)) {
            write(dataset, outputStream);
        }
    }

    public default void write(Dataset dataset, Path destination)
            throws IOException {
        write(dataset, Files.newOutputStream(destination));
    }

}
