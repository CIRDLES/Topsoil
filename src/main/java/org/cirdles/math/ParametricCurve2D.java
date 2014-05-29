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

import static org.cirdles.math.ConstantFunction.*;

/**
 *
 * @author zeringuej
 */
public class ParametricCurve2D extends ParametricCurve3D {

    public ParametricCurve2D(Function x, Function y) {
        super(x, y, ZERO_FUNCTION);
    }
    
    @Override
    public Vector2D of(double t) {
        return new Vector2D(x().of(t), y().of(t));
    }
    
    @Override
    public ParametricCurve2D prime() throws UnsupportedOperationException {
        return new ParametricCurve2D(x().prime(), y().prime());
    }
}
