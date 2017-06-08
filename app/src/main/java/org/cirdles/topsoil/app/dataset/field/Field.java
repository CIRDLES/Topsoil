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
 * An interface implemented by classes that describe fields of data.
 *
 * @author John Zeringue
 * @param <T> the field type
 */
public interface Field<T> {

    /**
     * Returns the name of the {@code Field}.
     *
     * @return  String name
     */
    public String getName();

    /**
     * Returns the {@code StringConverted} for converting the type of the {@code Field} to a {@code String}.
     *
     * @return  StringConverter of type {@literal <T>}
     */
    public StringConverter<T> getStringConverter();

}
