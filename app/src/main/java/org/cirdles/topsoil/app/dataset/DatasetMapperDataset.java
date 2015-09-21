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

import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.RawData;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.field.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by johnzeringue on 9/3/15.
 */
public class DatasetMapperDataset implements Dataset {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(DatasetMapperDataset.class);

    private Long id;
    private String name;
    private RawData rawData;

    public DatasetMapperDataset() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Field<?>> getFields() {
        return rawData.getFields();
    }

    @Override
    public List<Entry> getEntries() {
        return rawData.getEntries();
    }

    @Override
    public RawData getRawData() {
        return rawData;
    }

}
