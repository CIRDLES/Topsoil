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
import org.cirdles.topsoil.chart.concordia.ErrorChartToolBar;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChart;
import org.controlsfx.control.MasterDetailPane;

/**
 *
 * @author pfif
 */
public class ErrorChartToolBarBuilder implements Builder<ErrorChartToolBar> {

    private ErrorEllipseChart chart;
    private MasterDetailPane masterdetailpane;

    @Override
    public ErrorChartToolBar build() {
        return new ErrorChartToolBar(chart, masterdetailpane);
    }

    public ErrorEllipseChart getChart() {
        return chart;
    }

    public void setChart(ErrorEllipseChart chart) {
        this.chart = chart;
    }

    public MasterDetailPane getMasterdetailpane() {
        return masterdetailpane;
    }

    public void setMasterdetailpane(MasterDetailPane masterdetailpane) {
        this.masterdetailpane = masterdetailpane;
    }

}
