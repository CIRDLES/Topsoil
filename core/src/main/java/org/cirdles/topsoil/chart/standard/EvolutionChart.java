/*
 * Copyright 2015 CIRDLES.
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
package org.cirdles.topsoil.chart.standard;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.chart.Displayable;
import org.cirdles.topsoil.chart.JavaFXDisplayable;
import org.cirdles.topsoil.chart.JavaScriptChart;

/**
 * Created by johnzeringue on 12/1/15.
 */
public class EvolutionChart extends JavaScriptChart {

    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(ScatterplotChart.class);

    private static final String RESOURCE_NAME = "EvolutionChart.js";

    public EvolutionChart() {
        super(RESOURCE_EXTRACTOR.extractResourceAsPath(RESOURCE_NAME));
    }

    @Override
    public Displayable getPropertiesPanel() {
        return new JavaFXDisplayable() {

            @Override
            public Node displayAsNode() {
                return new VBox();
            }

        };
    }

}
