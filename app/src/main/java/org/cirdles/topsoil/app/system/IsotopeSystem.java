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
package org.cirdles.topsoil.app.system;

import org.cirdles.topsoil.app.plot.PlotType;
import org.cirdles.topsoil.app.plot.UraniumLeadPlotType;
import org.cirdles.topsoil.app.plot.UraniumThoriumPlotType;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by johnzeringue on 1/17/16.
 */
public enum IsotopeSystem {

    URANIUM_LEAD("U-Pb", UraniumLeadPlotType.values()),
    URANIUM_THORIUM("U-Th", UraniumThoriumPlotType.values());

    private final String name;
    private final List<PlotType> plotTypes;

    IsotopeSystem(String name, PlotType[] plotTypes) {
        this.name = name;
        this.plotTypes = asList(plotTypes);
    }

    public String getName() {
        return name;
    }

    public List<PlotType> getPlotTypes() {
        return plotTypes;
    }

}
