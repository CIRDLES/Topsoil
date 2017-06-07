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
package org.cirdles.topsoil.app.plot.variable;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * A class containing a set of {@link Variable}s used in plotting.
 *
 * @author John Zeringue
 * @see Variable
 */
public final class Variables {

    /**
     * A {@code Variable} that acts as an 'X' variable.
     */
    public static final Variable<Number> X;

    /**
     * A {@code Variable} that acts as an 'σX' variable.
     */
    public static final Variable<Number> SIGMA_X;

    /**
     * A {@code Variable} that acts as an 'Y' variable.
     */
    public static final Variable<Number> Y;

    /**
     * A {@code Variable} that acts as an 'σY' variable.
     */
    public static final Variable<Number> SIGMA_Y;

    /**
     * A {@code Variable} that acts as an 'Corr Coef' or 'rho' variable.
     */
    public static final Variable<Number> RHO;

    /**
     * A {@code List} of the {@code Variable}s defined in this class.
     */
    public static final List<Variable<Number>> VARIABLE_LIST;

    /**
     * A {@code List} of variables that are dependent.
     */
    public static final List<Variable> UNCERTAINTY_VARIABLES;

    static {
        X = new IndependentVariable<>("x");
        SIGMA_X = new DependentVariable<>("sigma_x", X);
        Y = new IndependentVariable<>("y");
        SIGMA_Y = new DependentVariable<>("sigma_y", Y);
        RHO = new IndependentVariable<>("rho");
        VARIABLE_LIST = Collections.unmodifiableList(asList(
                X,
                Y,
                SIGMA_X,
                SIGMA_Y,
                RHO
        ));
        UNCERTAINTY_VARIABLES = Collections.unmodifiableList(asList(
                SIGMA_X,
                SIGMA_Y
        ));
    }

}
