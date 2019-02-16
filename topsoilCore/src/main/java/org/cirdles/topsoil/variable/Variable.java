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

import java.io.Serializable;

/**
 * An interface implemented by classes that act as a variable for plotting.
 *
 * @author John Zeringue
 * @param <T> the variable type
 * @see DependentVariable
 * @see IndependentVariable
 * @see Variables
 */
public interface Variable<T> extends Serializable {

    /**
     * Returns the name of the variable.
     *
     * @return  String name
     */
    String getName();

    String getAbbreviation();

}
