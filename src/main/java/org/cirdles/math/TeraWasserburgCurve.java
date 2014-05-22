/*
 * Copyright 2014 CIRDLES.
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
package org.cirdles.math;

import static java.lang.Math.*;            // expm1, pow
import static org.cirdles.math.Constant.*; // LAMBDA_235, LAMBDA_238

/**
 *
 * @author zeringuej
 */
public class TeraWasserburgCurve implements ParametricCurve {

    @Override
    public double x(double t) {
        return 1 / expm1(LAMBDA_235.value() * t);
    }

    @Override
    public double y(double t) {
        return expm1(LAMBDA_235.value() * t)
                / (LAMBDA_238.value() / LAMBDA_235.value() * expm1(LAMBDA_238.value() * t));
    }

    @Override
    public double dy_dx(double t) {
        return LAMBDA_235.value() / LAMBDA_238.value()
                * ((LAMBDA_238.value() * (1 + x(t)) - LAMBDA_235.value())
                * pow(1 + 1 / x(t), LAMBDA_235.value() / LAMBDA_238.value())
                / (LAMBDA_238.value() * (1 + x(t))) - 1);
    }

}
