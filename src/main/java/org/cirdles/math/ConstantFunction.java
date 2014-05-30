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
 * A constant function, that is, a function that returns the same value for all inputs.
 */
public class ConstantFunction extends Function {

    /**
     * The zero function, a constant function that always returns zero.
     */
    public static final ConstantFunction ZERO_FUNCTION = new ConstantFunction(0);

    private final double value;

    /**
     * Creates a new constant function that always returns the same value.
     * 
     * @param value the value of the function
     */
    public ConstantFunction(double value) {
        this.value = value;
    }

    /**
     * Returns the value of this function at x, which for constant functions is always their constant values.
     * 
     * @param x the x
     * @return the value of this function
     */
    @Override
    public double of(double x) {
        return value;
    }

    /**
     * Returns a function representing the first derivative of this function. For constant functions, this is always the
     * zero function.
     * 
     * @return 
     */
    @Override
    public Function prime() {
        return ZERO_FUNCTION;
    }

}
