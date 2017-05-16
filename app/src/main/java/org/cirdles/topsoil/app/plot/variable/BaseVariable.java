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

    //***********************
    // Constructors
    //***********************

    /**
     * Creates a new {@code BaseVariable} with the specified name.
     *
     * @param name  String name
     */
    public BaseVariable(String name) {
        this.name = name;
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

}
