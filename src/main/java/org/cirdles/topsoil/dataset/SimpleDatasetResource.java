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
package org.cirdles.topsoil.dataset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cirdles.topsoil.app.dataset.writer.TSVDatasetWriter;

/**
 * Implementation of DatasetResource
 *
 * @author parizotclement
 */
public class SimpleDatasetResource implements DatasetResource {

    private static final Logger LOGGER
            = Logger.getLogger(DatasetResource.class.getName());

    private final Dataset dataset;
    private Path path;
    private final Path basePath;

    public SimpleDatasetResource(Dataset dataset, Path basePath) {

        this.dataset = dataset;
        this.basePath = basePath;

        //Check if dataset if closed otherwise path is open by default
        if (Files.exists(getClosedPath().resolve(dataset.getName().get()))) {
            path = getClosedPath().resolve(dataset.getName().get());
        } else {
            path = getOpenPath().resolve(dataset.getName().get());
            save();
        }

    }

    @Override
    public Dataset getDataset() {
        return dataset;
    }

    @Override
    public void delete() {
        try {
            Files.delete(path);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void save() {

        try {
            new TSVDatasetWriter().write(dataset, this.path);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private Path getOpenPath() {
        return basePath.resolve("open");
    }

    private Path getClosedPath() {
        return basePath.resolve("closed");
    }

    @Override
    public boolean isOpen() {
        return Optional.ofNullable(path)
                .map(Path::getParent)
                .map(parentPath -> parentPath.equals(getOpenPath()))
                .orElse(false);
    }

    @Override
    public void open() {
        if (isOpen()) {
            return;
        }

        try {
            path = Files.move(path, getOpenPath().resolve(path.getFileName()));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() {
        if (!isOpen()) {
            return;
        }

        try {
            save();
            path = Files.move(path, getClosedPath().resolve(path.getFileName()));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
