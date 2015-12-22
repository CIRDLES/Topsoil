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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.field.Field;

/**
 *
 * @author John Zeringue
 */
public interface VariableContext {

    public Collection<Variable> getVariables();

    public Dataset getDataset();

    public Collection<VariableBinding> getBindings();

    public default <T> Optional<VariableBinding<T>> getBindingForVariable(Variable<T> variable) {
        return getBindings().stream()
                .filter(binding -> {
                    return Objects.equals(binding.getVariable(), variable);
                })
                .findFirst()
                // cast binding type
                .map(binding -> (VariableBinding<T>) binding);
    }

    public default <T> void addBinding(Variable<T> variable, Field<T> field) {
        addBinding(variable, field, variable.getFormats().get(0));
    }

    public default <T> Optional<T> getValue(Variable<T> variable, Entry entry) {
        return getBindingForVariable(variable).map(binding -> {
            return binding.getFormat().normalize(binding, entry);
        });
    }

    public <T> void addBinding(Variable<T> variable, Field<T> field, VariableFormat<T> format);

}
