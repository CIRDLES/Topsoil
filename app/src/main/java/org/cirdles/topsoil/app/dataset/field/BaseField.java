/*
 * Copyright 2014 CIRDLES.
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
package org.cirdles.topsoil.app.dataset.field;

/**
 * An object containing data.
 *
 * @author CIRDLES
 * @param <T> the field type
 */
public abstract class BaseField<T> implements Field<T> {

    //***********************
    // Attributes
    //***********************

    /**
     * The name of the {@code BaseField}.
     */
    private final String name;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code BaseField} with the specified name.
     *
     * @param name  String name
     */
    public BaseField(String name) {
        this.name = name;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the name of the {@code BaseField}.
     *
     * @return String name
     */
    @Override
    public String getName() {
        return name;
    }
}
