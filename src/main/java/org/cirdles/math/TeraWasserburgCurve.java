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

import static java.lang.Math.*;            // exp, expm1, pow
import static org.cirdles.math.Constant.*; // LAMBDA_235, LAMBDA_238, R238_235S

/**
 *
 * @author zeringuej
 */
public class TeraWasserburgCurve extends ParametricCurve2D {

    private static Function X_COMPONENT = new Function() {

        @Override
        public double of(double t) {
            return 1 / expm1(LAMBDA_238.value() * t);
        }

        @Override
        public Function prime() throws UnsupportedOperationException {
            return new Function() {

                @Override
                public double of(double t) {
                    return -LAMBDA_238.value() * exp(LAMBDA_238.value() * t)
                            / pow(expm1(LAMBDA_238.value() * t), 2);
                }
            };
        }
    };

    private static Function Y_COMPONENT = new Function() {

        @Override
        public double of(double t) {
            return expm1(LAMBDA_235.value() * t)
                    / (R238_235S.value() * expm1(LAMBDA_238.value() * t));
        }

        @Override
        public Function prime() throws UnsupportedOperationException {
            return new Function() {

                @Override
                public double of(double t) {
                    return -(LAMBDA_235.value() * exp(LAMBDA_235.value() * t)
                            - exp(LAMBDA_238.value() * t)
                            * (LAMBDA_238.value() + LAMBDA_235.value() * exp(LAMBDA_235.value() * t)
                            - LAMBDA_238.value() * exp(LAMBDA_235.value() * t)))
                            / (R238_235S.value() * pow(expm1(LAMBDA_238.value() * t), 2));
                }
            };
        }
    };

    public TeraWasserburgCurve() {
        super(X_COMPONENT, Y_COMPONENT);
    }
}
