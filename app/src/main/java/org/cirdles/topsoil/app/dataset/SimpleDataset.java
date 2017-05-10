/*
 * Copyright 2016 CIRDLES.
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

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;

import java.util.List;

/**
 * Stores {@link RawData} with a name.
 *
 * @author John Zeringue
 * @see Dataset
 * @see RawData
 */
public class SimpleDataset implements Dataset {

    //***********************
    // Attributes
    //***********************

    /**
     * The name of the {@code SimpleDataset}.
     */
    private final String name;

    /**
     * The {@code RawData} of the {@code SimpleDataset}.
     */
    private final RawData rawData;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code SimpleDataset} with the specified name and {@code RawData}.
     *
     * @param name  String name
     * @param rawData   data as RawData
     */
    public SimpleDataset(String name, RawData rawData) {
        this.name = name;
        this.rawData = rawData;
    }

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc}
     */
    @Override
    public List<Field<?>> getFields() {
        return rawData.getFields();
    }

    /** {@inheritDoc}
     */
    @Override
    public List<Entry> getEntries() {
        return rawData.getEntries();
    }

}
