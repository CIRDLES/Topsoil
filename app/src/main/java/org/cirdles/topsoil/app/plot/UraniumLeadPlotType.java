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
package org.cirdles.topsoil.app.plot;

import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.standard.ScatterPlot;
import org.cirdles.topsoil.plot.standard.UncertaintyEllipsePlot;

import java.util.function.Supplier;

/**
 * Created by johnzeringue on 1/17/16.
 */
public enum UraniumLeadPlotType implements PlotType {

    SCATTER_PLOT("Scatter Plot", ScatterPlot::new),

    UNCERTAINTY_ELLIPSE_PLOT(
            "Uncertainty Ellipse Plot",
            UncertaintyEllipsePlot::new);

    private final String name;
    private final Supplier<Plot> plotSupplier;

    UraniumLeadPlotType(String name, Supplier<Plot> plotSupplier) {
        this.name = name;
        this.plotSupplier = plotSupplier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Plot newInstance() {
        return plotSupplier.get();
    }

}
