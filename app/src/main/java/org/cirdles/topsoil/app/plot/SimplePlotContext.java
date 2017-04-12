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
package org.cirdles.topsoil.app.plot;

import org.cirdles.topsoil.app.dataset.Dataset;
import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.dataset.field.Fields;

import java.util.*;

public class SimplePlotContext implements PlotContext {

    private final Collection<VariableBinding> bindings = new ArrayList<>();
    private final Dataset dataset;

    public SimplePlotContext(Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public Dataset getDataset() {
        return dataset;
    }

    @Override
    public Collection<VariableBinding> getBindings() {
        return bindings;
    }

    @Override
    public <T> void addBinding(Variable<T> variable, Field<T> field, VariableFormat<T> format) {
        bindings.add(new SimpleVariableBinding(variable, field, format, this));
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> data = new ArrayList<>();

        dataset.getEntries().forEach(entry -> {
            Map<String, Object> d = new HashMap<>();

            for (VariableBinding binding : bindings) {
                d.put(binding.getVariable().getName(), getValue(binding.getVariable(), (Entry) entry).get());
            }

            d.put("Selected", true);

            ((Entry) entry).get(Fields.SELECTED).ifPresent(selected -> {
                d.put("Selected", selected);
            });

            data.add(d);
        });

        return data;
    }
}
