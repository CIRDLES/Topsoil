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

import java.util.List;

/**
 *
 * @author John Zeringue
 * @param <T> the variable type
 */
public class DependentVariable<T> extends BaseVariable<T> {

    private final Variable<T> dependency;

    public DependentVariable(String name, Variable<T> dependency) {
        super(name);
        this.dependency = dependency;
    }

    public DependentVariable(String name, Variable<T> dependency,
            List<VariableFormat> formats) {
        super(name, formats);
        this.dependency = dependency;
    }

    public Variable<T> getDependency() {
        return dependency;
    }

}
