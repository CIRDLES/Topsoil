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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cirdles.topsoil.app.dataset.reader.DatasetReader;
import org.cirdles.topsoil.app.dataset.reader.TSVDatasetReader;

/**
 * Used for initialization
 * - Create the dataset directories
 * - Retrieve the saved datasets
 *
 * @author parizotclement
 */
public class DatasetResourceLoader {

    private final DatasetResourceFactory datasetResourceFactory;

    public DatasetResourceLoader(Path basePath) {

        datasetResourceFactory = new DatasetResourceFactory(basePath);

        try {
            Files.createDirectories(basePath);
        } catch (IOException ex) {
            Logger.getLogger(DatasetResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<DatasetResource> getDatasetResources(Path basePath) {
        List<DatasetResource> resources = new ArrayList();

        resources.addAll(getDatasetsForPath(basePath.resolve("open")));
        resources.addAll(getDatasetsForPath(basePath.resolve("closed")));

        return resources;
    }

    private List<DatasetResource> getDatasetsForPath(Path path) {
        List<DatasetResource> resources = new ArrayList<>();
        DatasetReader datasetReader = new TSVDatasetReader(true);

        try {
            // create the path if it does not yet exist
            Files.createDirectories(path);

            Files.walk(path, 1).forEach(datasetPath -> {
                try {
                    if (Files.isDirectory(datasetPath)) {
                        return;
                    }

                    SimpleDataset dataset = (SimpleDataset) datasetReader.read(datasetPath);
                    dataset.setName(datasetPath.getFileName().toString());
                    resources.add(datasetResourceFactory.makeDatasetResource(dataset));

                } catch (IOException ex) {
                    Logger.getLogger(DatasetResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(DatasetResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return resources;
    }
}
