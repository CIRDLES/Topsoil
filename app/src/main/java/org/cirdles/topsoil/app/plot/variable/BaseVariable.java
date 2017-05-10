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
package org.cirdles.topsoil.app.plot.variable;

import org.cirdles.topsoil.app.plot.variable.format.IdentityVariableFormat;
import org.cirdles.topsoil.app.plot.variable.format.VariableFormat;

import java.util.Arrays;
import java.util.List;

/**
 * A class that acts as a plotting variable.
 *
 * @param <T>   the type of Variable
 * @see Variable
 * @see DependentVariable
 * @see IndependentVariable
 * @see Variables
 */
public class BaseVariable<T> implements Variable<T> {

    //***********************
    // Attributes
    //***********************

    /**
     * The name of the variable.
     */
    private final String name;

    /**
     * A {@code List} of compatible {@code VariableFormats} for this variable.
     */
    private final List<VariableFormat<T>> formats;

    //***********************
    // Constructors
    //***********************

    /**
     * Creates a new {@code BaseVariable} with the specified name. The {@link IdentityVariableFormat} is used for the
     * variable's formats.
     *
     * @param name  String name
     */
    public BaseVariable(String name) {
        this(name, Arrays.asList(VariableFormat.identity()));
    }

    /**
     * Creates a new {@code BaseVariable} with the specified name and {@code VariableFormat}s.
     *
     * @param name  Stirng name
     * @param formats   List of VariableFormats of type {@literal <T>}
     */
    public BaseVariable(String name, List<VariableFormat<T>> formats) {
        this.name = name;
        this.formats = formats;
    }

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc}
     */
    @Override
    public List<VariableFormat<T>> getFormats() {
        return formats;
    }

}
