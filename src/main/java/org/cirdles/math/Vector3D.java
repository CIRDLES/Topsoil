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
 *
 * @author zeringuej
 */
public class Vector3D {

    private final double x;
    private final double y;
    private final double z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public Vector3D plus(Vector3D vector) {
        return new Vector3D(x() + vector.x(),
                            y() + vector.y(),
                            z() + vector.z());
    }

    public Vector3D minus(Vector3D vector) {
        return new Vector3D(x() - vector.x(),
                            y() - vector.y(),
                            z() - vector.z());
    }

    public Vector3D times(double scalar) {
        return new Vector3D(scalar * x(),
                            scalar * y(),
                            scalar * z());
    }

    public Vector3D dividedBy(double scalar) {
        return new Vector3D(x() / scalar,
                            y() / scalar,
                            z() / scalar);
    }

    public double dot(Vector3D vector) {
        return x() * vector.x() + y() * vector.y() + z() * vector.z();
    }

    @Override
    public String toString() {
        return String.format("(%.5f, %.5f, %.5f)", x(), y(), z());
    }
}
