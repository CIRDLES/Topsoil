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
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.plot.standard.ScatterPlotPropertiesPanel;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.uth.evolution.EvolutionPlot;
import org.cirdles.topsoil.plot.scatter.ScatterPlot;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by johnzeringue on 1/17/16.
 */
public enum UraniumThoriumPlotType implements PlotType {

    EVOLUTION_PLOT(
            "Evolution Plot",
            EvolutionPlot::new,
            UraniumThoriumPlotType::dummyPropertiesPanel),

    SCATTER_PLOT(
            "Scatter Plot",
            ScatterPlot::new,
            ScatterPlotPropertiesPanel::new);

    private final String name;
    private final Supplier<? extends Plot> plotSupplier;
    private final Function<Plot, ? extends Node> propertiesPanelConstructor;

    UraniumThoriumPlotType(
            String name,
            Supplier<? extends Plot> plotSupplier,
            Function<Plot, ? extends Node> propertiesPanelConstructor) {

        this.name = name;
        this.plotSupplier = plotSupplier;
        this.propertiesPanelConstructor = propertiesPanelConstructor;
    }

    private static Node dummyPropertiesPanel(Plot plot) {
        return new VBox();
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
