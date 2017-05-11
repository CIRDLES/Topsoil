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
package org.cirdles.topsoil.app.plot.variable.format;

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.plot.VariableBinding;

/**
 * An interface for classes that specify the format that a variable uses.
 *
 * @author John Zeringue
 */
public interface VariableFormat<T> {

    /**
     * Returns the name of the VariableFormat.
     *
     * @return  String name
     */
    public String getName();

    /**
     * Returns an item from the specified entry for the Field that is bound through the specified VariableBinding.
     * The value may be altered depending on the needs of the format. For example, if in Entry e, Field f was bound
     * to Variable X by VariableBinding b, normalize(b, e) may/would alter the value in e associated with f, and
     * return it.
     *
     * @param binding   a VariableBinding for a variable and a field in entry
     * @param entry an Entry
     * @return  an altered item from Entry
     */
    public T normalize(VariableBinding<T> binding, Entry entry);

    /**
     * Returns a new IdentityVariableFormat instance.
     *
     * @param <T>   the type of the VariableFormat
     * @return  a new IdentityVariableFormat
     */
    public static <T> VariableFormat<T> identity() {
        return new IdentityVariableFormat<>();
    }

}
