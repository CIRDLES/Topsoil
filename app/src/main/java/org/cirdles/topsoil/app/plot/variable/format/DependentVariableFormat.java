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
package org.cirdles.topsoil.app.plot.variable.format;

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.plot.VariableBinding;
import org.cirdles.topsoil.app.plot.variable.DependentVariable;

/**
 * An abstract class for a VariableFormat of a DependentVariable.
 *
 * @author John Zeringue
 * @param <T> the variable type
 */
public abstract class DependentVariableFormat<T> extends BaseVariableFormat<T> {

    /**
     * Constructs a new DependentVariableFormat with the specified name.
     *
     * @param name  String name
     */
    public DependentVariableFormat(String name) {
        super(name);
    }

    /**
     * Normalizes the value of the bound Field in entry by the dependency in the DependentVariable. This method is
     * incomplete without implementation of normalize(T variableValue, T dependencyValue).
     *
     * @param binding   the VariableBinding between a Field and a DependentVariable
     * @param entry an Entry
     * @return  a normalized value of type T
     */
    @Override
    public final T normalize(VariableBinding<T> binding, Entry entry) {
        T variableValue = entry.get(binding.getField()).get();
        T dependencyValue;

        if (binding.getVariable() instanceof DependentVariable) {
            DependentVariable<T> dependentVariable
                    = (DependentVariable<T>) binding.getVariable();
            dependencyValue = binding.getContext()
                    .getValue(dependentVariable.getDependency(), entry).get();
        } else {
            throw new IllegalArgumentException();
        }

        return normalize(variableValue, dependencyValue);
    }

    /**
     * Normalizes the value for a DependentVariable by the value of its dependency.
     *
     * @param variableValue the type T value of the DependentVariable
     * @param dependencyValue the type T value of the variable's dependency
     * @return  a normalized value of type T
     */
    public abstract T normalize(T variableValue, T dependencyValue);

}
