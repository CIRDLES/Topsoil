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

import static java.lang.Math.*; // abs, random

/**
 * A generic function from R to R.
 */
public abstract class Function {

    /**
     * Returns the value of this function at x.
     *
     * @param x a value in the domain of the function
     * @return
     */
    public abstract double of(double x);

    /**
     * Returns another function representing the first derivative of the function.
     *
     * @return the function's first derivative
     * @throws UnsupportedOperationException if the function's derivative is not specified or if the function is not
     * differentiable.
     */
    public Function prime() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the function resulting from adding another function to this one. Both the value and derivative of the new
     * function are formed by adding the values and derivatives of the operands, respectively.
     *
     * @param function the other function
     * @return a new function resulting from the addition of this function and the other
     */
    public Function plus(Function function) {
        return new Function() {

            @Override
            public double of(double x) {
                return Function.this.of(x) + function.of(x);
            }

            @Override
            public Function prime() throws UnsupportedOperationException {
                return Function.this.prime().plus(function.prime());
            }
        };
    }

    /**
     * Returns the function resulting from subtracting another function from this one. Both the value and derivative of
     * the new function are formed by subtracting the values and derivatives of the operands, respectively.
     *
     * @param function the other function
     * @return a new function resulting from subtracting the other function from this one
     */
    public Function minus(Function function) {
        return new Function() {

            @Override
            public double of(double x) {
                return Function.this.of(x) - function.of(x);
            }

            @Override
            public Function prime() throws UnsupportedOperationException {
                return Function.this.prime().minus(function.prime());
            }
        };
    }

    /**
     * Finds a zero for this function using the <a href="http://en.wikipedia.org/wiki/Newton's_method">Newton-Raphson
     * method</a> to a fixed precision. If the derivative of this function becomes too small to safely divide, the
     * algorithm will terminate early.
     *
     * @return a zero for this function
     */
    public double zero() {
        return zero(1e-10);
    }

    /**
     * Finds a zero for this function using the <a href="http://en.wikipedia.org/wiki/Newton's_method">Newton-Raphson
     * method</a> to the given precision. If the derivative of this function becomes too small to safely divide, the
     * algorithm will terminate early.
     *
     * @param precision the precision to be used for finding a zero
     * @return a zero of this function
     */
    public double zero(double precision) {
        double epsilon = 1e-14;
        double x0, x1 = 1;

        while (this.prime().of(x1) == 0) {
            x1 += random();
        }

        do {
            x0 = x1;

            if (abs(this.prime().of(x0)) < epsilon) {
                break;
            }

            x1 = x0 - this.of(x0) / this.prime().of(x0);
        } while (abs(x1 - x0) / abs(x1) > precision);

        return x1;
    }
}
