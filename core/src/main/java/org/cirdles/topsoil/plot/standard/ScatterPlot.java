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
package org.cirdles.topsoil.plot.standard;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.plot.Displayable;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Variable;

import java.util.List;

import static java.util.Arrays.asList;
import static org.cirdles.topsoil.plot.Variables.X;
import static org.cirdles.topsoil.plot.Variables.Y;

/**
 *
 * @author John Zeringue
 */
public class ScatterPlot extends JavaScriptPlot {

    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(ScatterPlot.class);

    private static final String RESOURCE_NAME = "ScatterPlot.js";

    private ScatterPlotPropertiesPanel propertiesPanel;

    public ScatterPlot() {
        super(RESOURCE_EXTRACTOR.extractResourceAsPath(RESOURCE_NAME));
    }

    @Override
    public Displayable getPropertiesPanel() {
        if (propertiesPanel == null) {
            propertiesPanel = new ScatterPlotPropertiesPanel(this);
            initializeFuture.thenRunAsync(propertiesPanel::updateProperties);
        }

        return propertiesPanel;
    }

    @Override
    public List<Variable> getVariables() {
        return asList(X, Y);
    }

}
