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
package org.cirdles.topsoil.app.dataset.entry;

import org.cirdles.topsoil.app.dataset.field.Field;
import java.util.Optional;

/**
 * An {@code Entry} consists of values for different variables, together creating a single data "entry".
 *
 * @author John Zeringue
 */
public interface Entry {

    /**
     * Returns an {@code Optional} that contains the value associated with the specified {@code Field}, if it exists.
     *
     * @param field the Field in question
     * @param <T>   the type of Field
     * @return  an Optional of type T
     */
    public <T> Optional<T> get(Field<? extends T> field);

    /**
     * Sets the value associated with the specified {@code Field} to the specified value.
     *
     * @param field a Field of type T
     * @param value a T value
     * @param <T>   the type of the Field and value
     */
    public <T> void set(Field<? super T> field, T value);

    /**
     * Adds an {@code EntryListener} to this {@code Entry}.
     *
     * @param listener  EntryListener
     */
    public void addListener(EntryListener listener);

    /**
     * Removes an {@code EntryListener} to this {@code Entry}.
     *
     * @param listener  EntryListener
     */
    public void removeListener(EntryListener listener);

}
