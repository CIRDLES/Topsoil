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
package org.cirdles.topsoil.plot.base;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.plot.JavaScriptPlot;

/**
 * A {@code Plot} which handles features for all isotope systems.
 *
 * @author Emily Coleman
 */
public class BasePlot extends JavaScriptPlot {

    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(BasePlot.class);

    private static final String RESOURCE_NAME = "BasePlot.js";

    /**
     * Constructs a new {@code BasePlot}.
     */
    public BasePlot() {
        super(
                RESOURCE_EXTRACTOR.extractResourceAsPath(RESOURCE_NAME),
                new BasePlotDefaultProperties());
    }

}
