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
import org.cirdles.topsoil.plot.Constant;
import org.cirdles.topsoil.plot.Displayable;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.UraniumLeadConstant;
import org.cirdles.topsoil.plot.Variable;

import java.util.List;

import static java.util.Arrays.asList;
import static org.cirdles.topsoil.plot.Variables.RHO;
import static org.cirdles.topsoil.plot.Variables.SIGMA_X;
import static org.cirdles.topsoil.plot.Variables.SIGMA_Y;
import static org.cirdles.topsoil.plot.Variables.X;
import static org.cirdles.topsoil.plot.Variables.Y;

/**
 *
 * @author John Zeringue
 */
public class UncertaintyEllipsePlot extends JavaScriptPlot {

    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(UncertaintyEllipsePlot.class);

    private static final String RESOURCE_NAME = "UncertaintyEllipsePlot.js";

    private UncertaintyEllipsePlotPropertiesPanel propertiesPanel;

    public UncertaintyEllipsePlot() {
        super(RESOURCE_EXTRACTOR.extractResourceAsPath(RESOURCE_NAME));
    }

    @Override
    public List<Constant> getConstants() {
        return asList(UraniumLeadConstant.values());
    }

    @Override
    public Displayable getPropertiesPanel() {
        if (propertiesPanel == null) {
            propertiesPanel = new UncertaintyEllipsePlotPropertiesPanel(this);
            initializeFuture.thenRunAsync(propertiesPanel::updateProperties);
        }

        return propertiesPanel;
    }

    @Override
    public List<Variable> getVariables() {
        return asList(X, SIGMA_X, Y, SIGMA_Y, RHO);
    }

}
