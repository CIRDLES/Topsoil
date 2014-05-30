/*
 * Copyright 2014 pfif.
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
package org.cirdles.topsoil.builder;

import javafx.util.Builder;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChart;
import org.cirdles.topsoil.chart.concordia.panels.ChartCustomizationPanel;

/**
 *
 * @author pfif
 */
public class ChartCustomizationPanelBuilder implements Builder<ChartCustomizationPanel> {

    private ErrorEllipseChart chart;

    @Override
    public ChartCustomizationPanel build() {
        return new ChartCustomizationPanel(chart);
    }

    public ErrorEllipseChart getChart() {
        return chart;
    }

    public void setChart(ErrorEllipseChart newChart) {
        chart = newChart;
    }

}
