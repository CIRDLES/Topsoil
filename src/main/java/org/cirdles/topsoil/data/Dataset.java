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
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author John Zeringue
 */
public interface Dataset {

    public static final Dataset EMPTY_DATASET
            = new SimpleDataset(emptyList(), emptyList());
    
    public Optional<String> getName();

    public List<Field<?>> getFields();

    public List<Entry> getEntries();

}