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

import static java.lang.Math.*; // abs, sqrt

/**
 * A three-dimensional vector.
 */
public class Vector3D {

    private final double x;
    private final double y;
    private final double z;

    /**
     * Creates a new three-dimensional vector with the given x-, y-, and z-components.
     *
     * @param x the x-component
     * @param y the y-component
     * @param z the z-component
     */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns the x-component
     *
     * @return the x-component
     */
    public double x() {
        return x;
    }

    /**
     * Returns the y-component
     *
     * @return the y-component
     */
    public double y() {
        return y;
    }

    /**
     * Returns the z-component
     *
     * @return the z-component
     */
    public double z() {
        return z;
    }

    /**
     * Returns a new vector representing the result of adding this vector to the other vector passed as the argument.
     * The vectors are added by creating a new vector where each component is the result of adding the corresponding
     * components of the operands.
     *
     * @param vector the other vector
     * @return the sum of this vector and the other
     */
    public Vector3D plus(Vector3D vector) {
        return new Vector3D(x() + vector.x(),
                            y() + vector.y(),
                            z() + vector.z());
    }

    /**
     * Returns a new vector representing the result of subtracting the specified vector from this one. The vectors are
     * subtracted by creating a new vector where each component is the result of subtracting the corresponding
     * components of the operands.
     *
     * @param vector the other vector
     * @return this vector minus the other
     */
    public Vector3D minus(Vector3D vector) {
        return new Vector3D(x() - vector.x(),
                            y() - vector.y(),
                            z() - vector.z());
    }

    /**
     * Returns a new vector representing the result of multiplying this vector by the given scalar. The new vector is
     * calculated by multiplying each of the original components by the scalar.
     *
     * @param scalar the scalar to multiply by
     * @return the result of the scalar multiplication
     */
    public Vector3D times(double scalar) {
        return new Vector3D(scalar * x(),
                            scalar * y(),
                            scalar * z());
    }

    /**
     * Returns a new vector representing the result of dividing this vector by the given scalar. The new vector is
     * calculated by dividing each of the original components by the scalar.
     *
     * @param scalar the scalar to divide by
     * @return the result of the scalar division
     */
    public Vector3D dividedBy(double scalar) {
        return new Vector3D(x() / scalar,
                            y() / scalar,
                            z() / scalar);
    }

    /**
     * Computes the <a href="http://en.wikipedia.org/wiki/Dot_product">dot product</a> of this vector and the given
     * vector.
     *
     * @param vector the other vector
     * @return the dot product
     */
    public double dot(Vector3D vector) {
        return x() * vector.x() + y() * vector.y() + z() * vector.z();
    }

    /**
     * Returns the norm (or magnitude) of this vector using the formula sqrt(x.dot(x)).
     *
     * @return the norm of this vector
     */
    public double norm() {
        return sqrt(this.dot(this));
    }

    /**
     * Returns the normalized form of this vector, a vector of length one in the same direction as this vector.
     *
     * @return the normalized form of this vector
     */
    public Vector3D normalized() {
        return this.dividedBy(this.norm());
    }

    @Override
    public String toString() {
        return String.format("(%.4f,%.4f,%.4f)", x(), y(), z());
    }

    /**
     * Compare this vector to another object and return whether or not they are equal. This vector is considered to be
     * equal to another object if the object is a non-null Vector3D with equivalent x-, y-, and z-components.
     *
     * @param obj the other object
     * @return whether or not this vector equals the other object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Vector3D)) {
            return false;
        }

        Vector3D vector = (Vector3D) obj;
        return x() == vector.x() && y() == vector.y() && z() == vector.z();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }
}
