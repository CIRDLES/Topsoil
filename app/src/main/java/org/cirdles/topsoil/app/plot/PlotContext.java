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
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.format.VariableFormat;
import org.cirdles.topsoil.plot.Plot;

import java.util.*;

/**
 * An interface implemented by classes which act as context for a {@link Plot}, specifically about the
 * {@link VariableBinding}s for a table's {@link Field}s.
 *
 * @author John Zeringue
 * @see SimplePlotContext
 */
public interface PlotContext {

    /**
     * Returns the {@code Dataset} for the {@code PlotContext}.
     *
     * @return  Dataset
     */
    Dataset getDataset();

    /**
     * Returns a {@code Collection} of the {@code VariableBinding}s for this context's {@code Plot}.
     *
     * @return  List of VariableBindings
     */
    Collection<VariableBinding> getBindings();

    /**
     * Returns an {@code Optional} containing the {@code VariableBinding} for the specified {@code Variable}, if it
     * exists.
     *
     * @param variable  Variable
     * @param <T>   the type of the Variable and VariableBinding
     * @return  Optional of type {@literal VariableBinding<T>}
     */
    default <T> Optional<VariableBinding<T>> getBindingForVariable(Variable<T> variable) {
        return getBindings().stream()
                .filter(binding -> {
                    return Objects.equals(binding.getVariable(), variable);
                })
                .findFirst()
                // cast binding type
                .map(binding -> (VariableBinding<T>) binding);
    }

    /**
     * Adds a new {@code VariableBinding} for the specified {@code Variable} and {@code Field} to the {@code
     * PlotContext}. Takes the first available {@code VariableFormat}.
     *
     * @param variable  the Variable to bind
     * @param field the Field to bind
     * @param <T>   the type of variable and field
     */
    default <T> void addBinding(Variable<T> variable, Field<T> field) {
        addBinding(variable, field, variable.getFormats().get(0));
    }

    /**
     * Returns an {@code Optional} containing the value for a specific {@code Variable} in an {@code Entry}, if it
     * exists.
     *
     * @param variable  Variable
     * @param entry Entry containing values
     * @param <T>   type of Variable
     * @return  Optional of type {@literal <T>}
     */
    default <T> Optional<T> getValue(Variable<T> variable, Entry entry) {
        return getBindingForVariable(variable).map(binding -> {
            return binding.getFormat().normalize(binding, entry);
        });
    }

    /**
     * Adds a new {@code VariableBinding} for the specified {@code Variable}, {@code Field}, and {@code VariableFormat}
     * to the {@code PlotContext}.
     *
     * @param variable  the Variable to bind
     * @param field the Field to bind
     * @param format the VariableFormat of variable
     * @param <T>   the type of variable and field
     */
    <T> void addBinding(Variable<T> variable, Field<T> field, VariableFormat<T> format);

    /**
     * Returns the data from the {@code Dataset} as a {@code List} of {@code Map}s of {@code String}s to {@code
     * Object}s.
     *
     * @return  data as a {@literal List<Map<String, Object>>}
     */
    List<Map<String, Object>> getData();

}
