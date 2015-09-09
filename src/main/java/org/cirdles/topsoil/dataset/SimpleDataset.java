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

import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.field.Field;

import java.util.List;

/**
 *
 * @author John Zeringue
 */
public class SimpleDataset implements Dataset {

    private final String name;
    private final RawData rawData;

    public SimpleDataset(String name, RawData rawData) {
        this.name = name;
        this.rawData = rawData;
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
