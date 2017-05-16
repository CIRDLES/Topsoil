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

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.plot.variable.Variable;

/**
 * An interface implemented by classes which act as a binding between {@link Variable}s and {@link Field}s. {@code
 * VariableBinding}s are part of a {@link PlotContext} for a specific plot.
 *
 * @author John Zeringue
 * @param <T> the variable type
 */
public interface VariableBinding<T> {

    /**
     * Returns the {@code Variable} for the binding.
     *
     * @return  Variable of type {@literal <T>}
     */
    public Variable<T> getVariable();


    /**
     * Returns the {@code Field} for the binding.
     *
     * @return Field of type {@literal <T>}
     */
    public Field<T> getField();

    /**
     * Returns the {@code PlotContext} that this binding is associated with.
     *
     * @return  PlotContext
     */
    public PlotContext getContext();

    /**
     * Takes an {@code Entry} and returns the normalized version of the entry's value for the binding's {@code
     * Variable}, using the binding's {@code VariableFormat}.
     *
     * @param entry an Entry containing values
     * @return a normalized {@literal T} value
     */
    public default T getValue(Entry entry) {
        return entry.get(getField()).isPresent() ? entry.get(getField()).get() : null;
    }

}
