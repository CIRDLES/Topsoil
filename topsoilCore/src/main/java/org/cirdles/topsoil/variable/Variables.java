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

import java.util.*;

import static java.util.Arrays.asList;

/**
 * A class containing various sets of {@link Variable}s.
 *
 * @author John Zeringue
 * @see Variable
 */
public final class Variables {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    public static final Variable<Number> X = new Variable<>("x", "X", "x", 0.0);
    public static final DependentVariable SIGMA_X = new DependentVariable("sigma_x", "σX", "sigma_x", 0.0, X);
    public static final Variable<Number> Y = new Variable<>("y", "Y", "y", 0.0);
    public static final DependentVariable SIGMA_Y = new DependentVariable("sigma_y", "σY", "sigma_y", 0.0, Y);
    public static final Variable<Number> RHO = new Variable<>("rho", "rho", "rho", 0.0);

    public static final Variable<String> LABEL = new Variable<>("label", "label", "label", "row");
    public static final Variable<String> ALIQUOT = new Variable<>("aliquot", "alqt.", "aliquot", "aliquot");

    public static final Variable<Boolean> SELECTED = new Variable<>("selected", "selected", "selected", true);

    public static final List<Variable<?>> ALL = Collections.unmodifiableList(asList(
            X,
            SIGMA_X,
            Y,
            SIGMA_Y,
            RHO,
            LABEL,
            ALIQUOT,
            SELECTED
    ));

    public static final List<Variable<?>> NUMBER_TYPE = Collections.unmodifiableList(asList(
            X, SIGMA_X, Y, SIGMA_Y, RHO
    ));

    /**
     * Returns the {@code Variable} object with the specified {@code String} key.
     *
     * @param key   String key
     * @return      Variable with key
     */
    public static Variable<?> variableForKey(String key) {
        for (Variable<?> variable : ALL) {
            if (variable.getKeyString().equals(key)) {
                return variable;
            }
        }
        return null;
    }

}
