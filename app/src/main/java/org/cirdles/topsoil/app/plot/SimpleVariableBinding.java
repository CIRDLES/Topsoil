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
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.format.VariableFormat;

/**
 * {@code VariableBinding}s are part of a {@link PlotContext} for a specific plot, and allow for values within a
 * context's {@link Dataset} to be normalized based on the {@link VariableFormat} of the associated {@link Variable}.
 *
 * @param <T>   the type of VariableBinding
 */
public class SimpleVariableBinding<T> implements VariableBinding<T> {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code Variable} that the {@code Field} is bound to.
     */
    private final Variable<T> variable;

    /**
     * The {@code Field} that the {@code Variable} is bound to.
     */
    private final Field<T> field;

    /**
     * The format of the {@code Variable}, for normalizing values.
     */
    private final VariableFormat<T> format;

    /**
     * The {@code PlotContext} that this binding is associated with.
     */
    private final PlotContext context;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code SimpleVariableBinding} for the specified {@code Variable<T>}, {@code Field<T>}, and
     * {@code PlotContext}. The first available {@code VariableFormat} for the variable is used.
     *
     * @param variable  a Variable of type {@literal <T>}
     * @param field a Field of type {@literal <T>}
     * @param context   the PlotContext that this binding is associated with
     */
    public SimpleVariableBinding(Variable<T> variable, Field<T> field,
            PlotContext context) {
        this(variable, field, variable.getFormats().get(0), context);
    }

    /**
     * Constructs a new {@code SimpleVariableBinding} for the specified {@code Variable<T>}, {@code Field<T>}, {@code
     * VariableFormat<T>}, and {@code PlotContext}.
     *
     * @param variable  a Variable of type {@literal <T>}
     * @param field a Field of type {@literal <T>}
     * @param format    a VariableFormat of type {@literal <T>}
     * @param context   the PlotContext that this binding is associated with
     */
    public SimpleVariableBinding(Variable<T> variable, Field<T> field,
            VariableFormat<T> format, PlotContext context) {
        this.variable = variable;
        this.field = field;
        this.format = format;
        this.context = context;
    }

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    @Override
    public Variable<T> getVariable() {
        return variable;
    }

    /** {@inheritDoc}
     */
    @Override
    public Field<T> getField() {
        return field;
    }

    /** {@inheritDoc}
     */
    @Override
    public VariableFormat<T> getFormat() {
        return format;
    }

    /** {@inheritDoc}
     */
    @Override
    public PlotContext getContext() {
        return context;
    }

}
