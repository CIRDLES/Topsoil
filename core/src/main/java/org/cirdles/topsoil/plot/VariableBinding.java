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
package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.field.Field;

/**
 *
 * @author John Zeringue
 * @param <T> the variable type
 */
public interface VariableBinding<T> {

    public Variable<T> getVariable();

    public Field<T> getField();

    public VariableFormat<T> getFormat();

    public PlotContext getContext();

    public default T getValue(Entry entry) {
        return getFormat().normalize(this, entry);
    }

}
