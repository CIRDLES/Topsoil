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

    public static final IndependentVariable X = IndependentVariable.X;
    public static final DependentVariable SIGMA_X = DependentVariable.SIGMA_X;
    public static final IndependentVariable Y = IndependentVariable.Y;
    public static final DependentVariable SIGMA_Y = DependentVariable.SIGMA_Y;
    public static final IndependentVariable RHO = IndependentVariable.RHO;

    public static final TextVariable LABEL = TextVariable.LABEL;
    public static final TextVariable ALIQUOT = TextVariable.ALIQUOT;

    public static final BooleanVariable SELECTED = BooleanVariable.SELECTED;

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

}
