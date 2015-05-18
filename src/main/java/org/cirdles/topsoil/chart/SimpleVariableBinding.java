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

import org.cirdles.topsoil.dataset.field.Field;

public class SimpleVariableBinding<T> implements VariableBinding<T> {

    private final Variable<T> variable;
    private final Field<T> field;
    private final VariableFormat<T> format;
    private final VariableContext context;

    public SimpleVariableBinding(Variable<T> variable, Field<T> field,
            VariableContext context) {
        this(variable, field, variable.getFormats().get(0), context);
    }
    
    public SimpleVariableBinding(Variable<T> variable, Field<T> field,
            VariableFormat<T> format, VariableContext context) {
        this.variable = variable;
        this.field = field;
        this.format = format;
        this.context = context;
    }

    @Override
    public Variable<T> getVariable() {
        return variable;
    }

    @Override
    public Field<T> getField() {
        return field;
    }

    @Override
    public VariableFormat<T> getFormat() {
        return format;
    }

    @Override
    public VariableContext getContext() {
        return context;
    }

}
