/*
 * Copyright 2017 CIRDLES.
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
package org.cirdles.topsoil.plot.impl;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.plot.DefaultProperties;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.PlotType;

/**
 * A {@code Plot} which handles features for all isotope systems.
 *
 * @author Emily Coleman
 */
public class ScatterPlot extends JavaScriptPlot {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static final String RESOURCE_NAME = "ScatterPlot.js";
    private static final PlotType TYPE = PlotType.SCATTER;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ScatterPlot() {
        super(
                TYPE,
                new DefaultProperties(),
                new ResourceExtractor(ScatterPlot.class).extractResourceAsPath(RESOURCE_NAME)
        );
    }

}
