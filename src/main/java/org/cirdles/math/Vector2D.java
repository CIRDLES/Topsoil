/*
 * Copyright 2014 zeringuej.
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
 * A two-dimensional vector.
 */
public class Vector2D extends Vector3D {

    /**
     * Creates a new two-dimensional vector with the specified x- and y- components.
     *
     * @param x the x-component
     * @param y the y-component
     */
    public Vector2D(double x, double y) {
        super(x, y, 0);
    }

    /**
     * Returns a new vector representing the result of adding this vector to the other vector passed as the argument.
     * The vectors are added by creating a new vector where each component is the result of adding the corresponding
     * components of the operands.
     *
     * @param vector the other vector
     * @return the sum of this vector and the other
     */
    public Vector2D plus(Vector2D vector) {
        return new Vector2D(x() + vector.x(),
                            y() + vector.y());
    }

    /**
     * Returns a new vector representing the result of subtracting the specified vector from this one. The vectors are
     * subtracted by creating a new vector where each component is the result of subtracting the corresponding
     * components of the operands.
     *
     * @param vector the other vector
     * @return this vector minus the other
     */
    public Vector2D minus(Vector2D vector) {
        return new Vector2D(x() - vector.x(),
                            y() - vector.y());
    }

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public Vector2D times(double scalar) {
        return new Vector2D(scalar * x(),
                            scalar * y());
    }

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public Vector2D dividedBy(double scalar) {
        return new Vector2D(x() / scalar,
                            y() / scalar);
    }

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public Vector2D normalized() {
        return this.dividedBy(this.norm());
    }

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public String toString() {
        return String.format("(%.5f, %.5f)", x(), y());
    }
}
