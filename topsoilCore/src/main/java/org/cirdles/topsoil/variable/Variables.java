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

    public static final List<Variable<?>> ALL;
    static {
        ALL = Collections.unmodifiableList(asList(
                IndependentVariable.X,
                DependentVariable.SIGMA_X,
                IndependentVariable.Y,
                DependentVariable.SIGMA_Y,
                IndependentVariable.RHO
        ));
        Map<String, Variable> abbrs = new HashMap<>();
        ALL.forEach(v -> abbrs.put(v.getAbbreviation(), v));
        ABBREVIATIONS = Collections.unmodifiableMap(abbrs);
    }

    private static final Map<String, Variable> ABBREVIATIONS;

    public static Variable fromAbbreviation(String abbr) {
        return ABBREVIATIONS.get(abbr);
    }

}
