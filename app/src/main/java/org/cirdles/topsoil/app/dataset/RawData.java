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
 * Raw data as two {@code List}s, one of {@link Field}s, and the other of {@link Entry}s.
 *
 * @author John Zeringue
 */
public class RawData {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code List} of {@code Field}s for the {@code RawData}.
     */
    private final List<Field<?>> fields;

    /**
     * The {@code List} of {@code Entry}s for the {@code RawData}.
     */
    private final List<Entry> entries;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code RawData} with the specified {@code Field}s and {@code Entry}s.
     *
     * @param fields    Fields for the RawData
     * @param entries   Entries for the RawData
     */
    public RawData(List<Field<?>> fields, List<Entry> entries) {
        this.fields = fields;
        this.entries = entries;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the list of {@code Field}s for the {@code RawData}.
     *
     * @return  List of Fields
     */
    public List<Field<?>> getFields() {
        return fields;
    }

    /**
     * Returns the list of {@code Entry}s for the {@code RawData}.
     *
     * @return  List of Entries
     */
    public List<Entry> getEntries() {
        return entries;
    }

}
