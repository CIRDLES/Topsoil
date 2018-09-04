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
package org.cirdles.topsoil.variable;

/**
 * A {@code BaseVariable} that is dependent, i.e. it is dependent on some other {@code Variable} that affects the way
 * it normalizes a value.
 *
 * @author John Zeringue
 * @param <T> the variable type
 */
public class DependentVariable<T> extends BaseVariable<T> {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code Variable} that this variable depends on.
     */
    private final Variable<T> dependency;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code DependentVariable} with the specified name and dependency.
     *
     * @param name  String name
     * @param dependency    a Variable that this variable is dependent on
     */
    public DependentVariable(String name, Variable<T> dependency) {
        super(name);
        this.dependency = dependency;
    }

    public DependentVariable(String name, String abbreviation, Variable<T> dependency) {
        super(name, abbreviation);
        this.dependency = dependency;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the {@code Variable} that this variable depends on.
     *
     * @return  Variable dependency
     */
    public Variable<T> getDependency() {
        return dependency;
    }

}
