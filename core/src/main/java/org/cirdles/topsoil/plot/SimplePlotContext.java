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
package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.field.Field;

import java.util.ArrayList;
import java.util.Collection;

public class SimplePlotContext implements PlotContext {

    private final Collection<ConstantBinding> constantBindings;
    private final Collection<VariableBinding> variableBindings;
    private final Dataset dataset;

    public SimplePlotContext(Dataset dataset) {
        this.dataset = dataset;

        constantBindings = new ArrayList<>();
        variableBindings = new ArrayList<>();
    }

    @Override
    public Dataset getDataset() {
        return dataset;
    }

    @Override
    public Collection<ConstantBinding> getConstantBindings() {
        return constantBindings;
    }

    @Override
    public Collection<VariableBinding> getVariableBindings() {
        return variableBindings;
    }

    @Override
    public void addBinding(Constant constant, Number value) {
        constantBindings.add(new SimpleConstantBinding(constant, value));
    }

    @Override
    public <T> void addBinding(Variable<T> variable, Field<T> field, VariableFormat<T> format) {
        variableBindings.add(new SimpleVariableBinding(variable, field, format, this));
    }

}
