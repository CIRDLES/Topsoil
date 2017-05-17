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

/**
 * A custom listener for an {@code Entry} instance.
 *
 * @author parizotclement
 */
@FunctionalInterface
public interface EntryListener {

    /**
     * Called when a value in an {@code Entry} is changed.
     *
     * @param entry the Entry that was changed
     * @param field the Field whose value was affected
     */
    public void changed(Entry entry, Field field);

}
