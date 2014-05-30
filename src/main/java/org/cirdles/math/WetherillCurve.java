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
import static org.cirdles.math.Constant.*; // LAMBDA_235, LAMBDA_238

/**
 *
 * @author zeringuej
 */
public class WetherillCurve extends ParametricCurve2D {
    
    private static Function X_COMPONENT = new Function() { // x(t)

            @Override
            public double of(double t) {
                return expm1(LAMBDA_235.value() * t);
            }

            @Override
            public Function prime() {
                return new Function() {

                    @Override
                    public double of(double t) {
                        return LAMBDA_235.value() * exp(LAMBDA_235.value() * t);
                    }
                };
            }
        };
    
    private static Function Y_COMPONENT = new Function() { // y(t)

            @Override
            public double of(double t) {
                return expm1(LAMBDA_238.value() * t);
            }

            @Override
            public Function prime() {
                return new Function() {

                    @Override
                    public double of(double t) {
                        return LAMBDA_238.value() * exp(LAMBDA_238.value() * t);
                    }
                };
            }
        };

    public WetherillCurve() {
        super(X_COMPONENT, Y_COMPONENT);
    }
}
