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
package org.cirdles.topsoil.app.dataset.field;

import javafx.util.StringConverter;

/**
 * A {@code Field} that contains {@code Boolean}s.
 *
 * @author parizotclement
 */
public class BooleanField extends BaseField<Boolean> {

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code BooleanField} with the specified name.
     *
     * @param name String name
     */
    public BooleanField(String name) {
        super(name);
    }

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    @Override
    public StringConverter<Boolean> getStringConverter() {
        return new StringConverter<Boolean>() {

            @Override
            public String toString(Boolean object) {
                return object.toString();
            }

            @Override
            public Boolean fromString(String string) {
                return Boolean.valueOf(string);
            }

        };
    }

}
