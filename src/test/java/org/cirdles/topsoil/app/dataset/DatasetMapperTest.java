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
package org.cirdles.topsoil.app.dataset;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.cirdles.topsoil.app.flyway.FlywayModule;
import org.cirdles.topsoil.app.sqlite.SQLiteMyBatisModule;
import org.cirdles.topsoil.dataset.Dataset;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.inject.name.Names.named;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by johnzeringue on 9/8/15.
 */
public class DatasetMapperTest {

    private Dataset dataset;
    private DatasetMapper datasetMapper;

    private static DatasetMapper getDatasetMapper() throws IOException {
        Path tempDbPath = Files.createTempFile("test-", ".db");

        Injector injector = Guice.createInjector(
                new FlywayModule(),
                new SQLiteMyBatisModule(),

                // anything else
                binder -> {
                    binder.bind(String.class)
                            .annotatedWith(named("JDBC.schema"))
                            .toInstance(tempDbPath.toString());
                });

        Flyway flyway = injector.getInstance(Flyway.class);
        flyway.clean();
        flyway.migrate();

        return injector.getInstance(DatasetMapper.class);
    }

    @Before
    public void setUp() throws Throwable {
        dataset = new TsvDataset("Dataset", "A\tB\n1\t2");
        datasetMapper = getDatasetMapper();
    }

    @Test
    public void testAddDataset() throws Exception {
        datasetMapper.addDataset(dataset);

        assertEquals("Dataset", datasetMapper
                .getDatasets()
                .get(0)
                .getName());
    }

    @Test
    public void testGetDatasets() throws Exception {
        assertTrue(datasetMapper.getDatasets().isEmpty());

        for (int i = 0; i < 3; i++) {
            datasetMapper.addDataset(dataset);
        }

        assertEquals(3, datasetMapper.getDatasets().size());

        assertEquals("A", datasetMapper
                .getDatasets()
                .get(0)
                .getFields()
                .get(0)
                .getName());
    }

}
