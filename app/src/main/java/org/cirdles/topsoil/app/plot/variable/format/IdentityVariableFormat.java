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
 * A VariableFormat for an IndependentVariable. Because there are no dependencies, normalization through this format
 * does not yield a value different from that of an Entry.
 *
 * @param <T>   the type of the Variable Format
 */
public class IdentityVariableFormat<T> extends BaseVariableFormat<T> {

    /**
     * Constructs a new IdentityVariableFormat with the name "Identity".
     */
    public IdentityVariableFormat() {
        super("Identity");
    }

    /**
     * Returns a value of type T for a bound Field in the specified entry.
     *
     * @param binding   a VariableBinding for a variable and a field in entry
     * @param entry an Entry
     * @return  a normalized value of type T
     */
    @Override
    public T normalize(VariableBinding<T> binding, Entry entry) {
        return entry.get(binding.getField()).get();
    }

}
