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

/**
 * Mathematical constants to be used program-wide.
 */
public enum Constant {

    LAMBDA_235(9.8485e-10), LAMBDA_238(1.55125e-10), R238_235S(137.88);

    private final double value;

    /**
     * Creates a new constant with the given value.
     *
     * @param value the value of the constant
     */
    private Constant(double value) {
        this.value = value;
    }

    /**
     * Returns the value of this constant.
     *
     * @return the value of this constant
     */
    public double value() {
        return value;
    }
}
