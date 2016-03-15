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
 * Created by johnzeringue on 8/30/15.
 */
public class RawData {

    private final List<Field<?>> fields;
    private final List<Entry> entries;

    public RawData(List<Field<?>> fields, List<Entry> entries) {
        this.fields = fields;
        this.entries = entries;
    }

    public List<Field<?>> getFields() {
        return fields;
    }

    public List<Entry> getEntries() {
        return entries;
    }

}
