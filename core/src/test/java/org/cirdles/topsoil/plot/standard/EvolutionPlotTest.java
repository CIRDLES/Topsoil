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
package org.cirdles.topsoil.plot.standard;

import org.cirdles.topsoil.plot.Plot;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by johnzeringue on 1/31/16.
 */
public class EvolutionPlotTest {

    @Rule
    public Timeout timeout = Timeout.seconds(5);

    private Plot plot;

    @Before
    public void setUp() {
        plot = new EvolutionPlot();
    }

    @Test
    public void testGetVariables() throws Exception {
        assertThat(plot.getVariables()).hasSize(4);
    }

}
