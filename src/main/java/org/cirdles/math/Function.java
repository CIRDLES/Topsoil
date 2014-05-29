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
 *
 * @author zeringuej
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
    
    public double zero() {
        return zero(1e-5);
    }
    
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
