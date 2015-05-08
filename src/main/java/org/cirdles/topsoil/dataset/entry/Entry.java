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
package org.cirdles.topsoil.dataset.entry;

import org.cirdles.topsoil.dataset.field.Field;
import java.util.Optional;

/**
 *
 * @author John Zeringue
 */
public interface Entry {

    public <T> Optional<T> get(Field<? extends T> field);

    public <T> void set(Field<? super T> field, T value);

    public void addListener(EntryListener listener);

    public void removeListener(EntryListener listener);

}
