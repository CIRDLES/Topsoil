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
package org.cirdles.topsoil.chart;

import org.cirdles.topsoil.data.Entry;

/**
 *
 * @author John Zeringue
 * @param <T>
 */
public abstract class DependentVariableFormat<T> extends BaseVariableFormat<T> {

    public DependentVariableFormat(String name) {
        super(name);
    }

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
    
    public abstract T normalize(T variableValue, T dependencyValue);
    
}
