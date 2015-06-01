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

import org.cirdles.topsoil.dataset.field.Field;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.dataset.entry.Entry;

/**
 *
 * @author John Zeringue
 */
public class SimpleDataset implements Dataset {

    private final List<Field<?>> fields;
    private final ObservableList<Entry> entries;

    private Optional<String> name = Optional.empty();

    public SimpleDataset(List<Field<?>> fields, List<Entry> entries) {
        this.fields = fields;
        this.entries = FXCollections.observableArrayList(entries);
    }

    @Override
    public Optional<String> getName() {
        return name;
    }

    @Override
    public List<Field<?>> getFields() {
        return fields;
    }

    @Override
    public ObservableList<Entry> getEntries() {
        return entries;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = Optional.ofNullable(name);
    }

}
