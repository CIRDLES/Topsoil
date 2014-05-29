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
 *
 * @author zeringuej
 */
public class ParametricCurve3D {

    private final Function x;
    private final Function y;
    private final Function z;

    public ParametricCurve3D(Function x, Function y, Function z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Function x() {
        return x;
    }

    public Function y() {
        return y;
    }

    public Function z() {
        return z;
    }

    public Vector3D of(double t) {
        return new Vector3D(x().of(t), y().of(t), z().of(t));
    }

    public ParametricCurve3D prime() throws UnsupportedOperationException {
        return new ParametricCurve3D(x().prime(), y().prime(), z().prime());
    }
}
