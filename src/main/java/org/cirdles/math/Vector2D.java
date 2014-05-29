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
public class Vector2D extends Vector3D {

    public Vector2D(double x, double y) {
        super(x, y, 0);
    }

    public Vector2D plus(Vector2D vector) {
        return new Vector2D(x() + vector.x(),
                            y() + vector.y());
    }

    public Vector2D minus(Vector2D vector) {
        return new Vector2D(x() - vector.x(),
                            y() - vector.y());
    }

    @Override
    public Vector2D times(double scalar) {
        return new Vector2D(scalar * x(),
                            scalar * y());
    }

    @Override
    public Vector2D dividedBy(double scalar) {
        return new Vector2D(x() / scalar,
                            y() / scalar);
    }

    @Override
    public String toString() {
        return String.format("(%.5f, %.5f)", x(), y());
    }
}
