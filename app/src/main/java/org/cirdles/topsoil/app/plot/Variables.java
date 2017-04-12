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

import static java.util.Arrays.asList;
import static org.cirdles.topsoil.app.plot.VariableFormats.UNCERTAINTY_FORMATS;

/**
 *
 * @author John Zeringue
 */
public final class Variables {

    public static final Variable X;
    public static final Variable SIGMA_X;
    public static final Variable Y;
    public static final Variable SIGMA_Y;
    public static final Variable RHO;
    public static final List<Variable> VARIABLE_LIST;

    static {
        X = new IndependentVariable("x");
        SIGMA_X = new DependentVariable("sigma_x", X, UNCERTAINTY_FORMATS);
        Y = new IndependentVariable("y");
        SIGMA_Y = new DependentVariable("sigma_y", Y, UNCERTAINTY_FORMATS);
        RHO = new IndependentVariable("rho");
        VARIABLE_LIST = asList(
                X,
                Y,
                SIGMA_X,
                SIGMA_Y,
                RHO
        );
    }

}
