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
package org.cirdles.topsoil.plot.uth.evolution;

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
public class EvolutionPlotDefaultProperties extends HashMap<String, Object> {

    public EvolutionPlotDefaultProperties() {
        configure();
    }

    private void configure() {
        put(TITLE, "Isochron Plot");
        put(UNCERTAINTY, 2);
        put(X_AXIS, "[230Th/238U]t");
        put(Y_AXIS, "[234Pb/238U]t");
    }

}
