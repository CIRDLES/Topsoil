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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cirdles.topsoil.data.Dataset;
import org.cirdles.topsoil.data.DatasetManager;
import org.cirdles.topsoil.data.SimpleDataset;

/**
 *
 * @author John Zeringue
 */
public class TSVDatasetManager implements DatasetManager {

    private static final Logger LOGGER
            = Logger.getLogger(TSVDatasetManager.class.getName());

    private final Path basePath;
    private final DatasetReader datasetReader;
    private final Map<Dataset, Path> datasetToPath;

    public TSVDatasetManager(Path basePath) {
        this.basePath = basePath;

        datasetReader = new TSVDatasetReader(true);
        datasetToPath = new HashMap<>();

        try {
            // safer than Files.createDirectory
            Files.createDirectories(basePath);
        } catch (IOException ex) {
            Logger.getLogger(TSVDatasetManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    Path getBasePath() {
        return basePath;
    }

    Path getOpenPath() {
        return getBasePath().resolve("open");
    }

    @Override
    public void open(Dataset dataset) {
        if (datasetToPath.get(dataset).getParent().equals(getOpenPath())) {
            return;
        }
        
        try {
            Path datasetPath = datasetToPath.get(dataset);
            Files.move(datasetPath, getOpenPath().resolve(datasetPath.getFileName()));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    Path getClosedPath() {
        return getBasePath().resolve("closed");
    }

    @Override
    public void close(Dataset dataset) {
        if (datasetToPath.get(dataset).getParent().equals(getClosedPath())) {
            return;
        }
        
        try {
            Path datasetPath = datasetToPath.get(dataset);
            Files.move(datasetPath, getClosedPath().resolve(datasetPath.getFileName()));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    List<Dataset> getDatasetsForPath(Path path) {
        List<Dataset> datasets = new ArrayList<>();

        try {
            // create the path if it does not yet exist
            Files.createDirectories(path);
            
            Files.walk(path, 1).forEach(datasetPath -> {
                try {
                    if (Files.isDirectory(datasetPath)) {
                        return;
                    }
                    
                    SimpleDataset dataset = (SimpleDataset) datasetReader.read(datasetPath);

                    datasets.add(dataset);
                    dataset.setName(datasetPath.getFileName().toString());
                    datasetToPath.put(dataset, datasetPath);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            });
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return datasets;
    }

    List<Dataset> getOpenDatasets() {
        return getDatasetsForPath(getOpenPath());
    }

    @Override
    public boolean isOpen(Dataset dataset) {
        return datasetToPath.get(dataset).getParent().equals(getOpenPath());
    }

    List<Dataset> getClosedDatasets() {
        return getDatasetsForPath(getClosedPath());
    }

    @Override
    public List<Dataset> getDatasets() {
        List datasets = new ArrayList();

        datasets.addAll(getOpenDatasets());
        datasets.addAll(getClosedDatasets());

        return datasets;
    }

}
