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
package org.cirdles.topsoil.dataset.entry;

import org.cirdles.topsoil.dataset.field.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author John Zeringue
 */
public class SimpleEntry implements Entry {

    private static long numberOfEntries = 0;

    private final long id;
    private final Map<Field, Object> fieldsToValues;
    private final List<EntryListener> listeners;

    public SimpleEntry() {
        id = numberOfEntriesPlusPlus();
        fieldsToValues = new HashMap<>();
        listeners = new ArrayList<>();
    }

    private static long numberOfEntriesPlusPlus() {
        return numberOfEntries++;
    }

    @Override
    public <T> Optional<T> get(Field<? extends T> field) {
        return Optional.ofNullable((T) fieldsToValues.get(field));
    }

    @Override
    public <T> void set(Field<? super T> field, T value) {
        fieldsToValues.put(field, value);

        listeners.forEach(listener -> {
            listener.changed(this, field);
        });
    }

    @Override
    public void addListener(EntryListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(EntryListener listener) {
        listeners.remove(listener);
    }

    public static int compare(Entry left, Entry right) {
        if (left instanceof SimpleEntry && right instanceof SimpleEntry) {
            return Long.compare(
                    ((SimpleEntry) left).id,
                    ((SimpleEntry) right).id);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
