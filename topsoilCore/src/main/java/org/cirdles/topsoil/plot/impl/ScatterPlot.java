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

import org.cirdles.topsoil.plot.JxBrowserPlot;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.plot.WebViewPlot;

/**
 * A {@code Plot} which handles features for scatter plots.
 *
 * @author Emily Coleman
 */
public class ScatterPlot extends WebViewPlot {

    public ScatterPlot() {
        super(PlotType.SCATTER);
    }

}
