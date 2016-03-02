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

import javafx.scene.Node;
import org.cirdles.topsoil.app.plot.standard.ScatterPlotPropertiesPanel;
import org.cirdles.topsoil.app.plot.standard.UncertaintyEllipsePlotPropertiesPanel;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.scatter.ScatterPlot;
import org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlot;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by johnzeringue on 1/17/16.
 */
public enum UraniumLeadPlotType implements PlotType {

    SCATTER_PLOT(
            "Scatter Plot",
            ScatterPlot::new,
            ScatterPlotPropertiesPanel::new),

    UNCERTAINTY_ELLIPSE_PLOT(
            "Uncertainty Ellipse Plot",
            UncertaintyEllipsePlot::new,
            UncertaintyEllipsePlotPropertiesPanel::new);

    private final String name;
    private final Supplier<Plot> plotSupplier;
    private final Function<Plot, Node> propertiesPanelConstructor;

    UraniumLeadPlotType(
            String name,
            Supplier<Plot> plotSupplier,
            Function<Plot, Node> propertiesPanelConstructor) {

        this.name = name;
        this.plotSupplier = plotSupplier;
        this.propertiesPanelConstructor = propertiesPanelConstructor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Plot newInstance() {
        return plotSupplier.get();
    }

    @Override
    public Node newPropertiesPanel(Plot plot) {
        return propertiesPanelConstructor.apply(plot);
    }

}
