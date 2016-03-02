/*
 * Copyright 2016 CIRDLES.
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
package org.cirdles.topsoil.plot.upb.uncertainty;

import java.util.HashMap;

import static org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlotProperties.ELLIPSE_FILL_COLOR;
import static org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlotProperties.LAMBDA_235;
import static org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlotProperties.LAMBDA_238;
import static org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlotProperties.TITLE;
import static org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlotProperties.UNCERTAINTY;
import static org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlotProperties.X_AXIS;
import static org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlotProperties.Y_AXIS;

/**
 * Created by johnzeringue on 2/22/16.
 */
public class UncertaintyEllipsePlotDefaultProperties
        extends HashMap<String, Object> {

    public UncertaintyEllipsePlotDefaultProperties() {
        configure();
    }

    private void configure() {
        put(ELLIPSE_FILL_COLOR, "red");
        put(TITLE, "Uncertainty Ellipse Plot");
        put(UNCERTAINTY, 2);
        put(X_AXIS, "207Pb*/235U");
        put(Y_AXIS, "206Pb*/238U");
        put(LAMBDA_235, 9.8485e-10);
        put(LAMBDA_238, 1.55125e-10);
    }

}
