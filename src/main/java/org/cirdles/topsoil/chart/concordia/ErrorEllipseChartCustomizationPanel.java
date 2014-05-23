/*
 * Copyright 2014 CIRDLES.
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
package org.cirdles.topsoil.chart.concordia;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.Tools;
import org.cirdles.topsoil.chart.concordia.panels.ChartCustomizationPanel;
import org.cirdles.topsoil.chart.concordia.panels.ErrorEllipsesCustomisationPanel;

/**
 *
 * @author pfif
 */
public class ErrorEllipseChartCustomizationPanel extends VBox {

    public static final String NODE_TITLE = "Customization";

    public ErrorEllipseChartCustomizationPanel(ErrorEllipseChart chart) {
        super(10);
        setPadding(new Insets(10));

        ColumnConstraints labelConstraints = new ColumnConstraints();
        labelConstraints.setMinWidth(100);
        //getColumnConstraints().add(labelConstraints);
        getStylesheets().add(ErrorEllipseChartCustomizationPanel.class.getResource("ConcordiaChart.css").toExternalForm());

        //Creaton of the label
        Label title = Tools.label_minsize(NODE_TITLE);
        title.getStyleClass().add("title-panel");

        ErrorEllipsesCustomisationPanel eeCustomizationPane = new ErrorEllipsesCustomisationPanel(chart);
        ChartCustomizationPanel ccCustomizationPane = new ChartCustomizationPanel(chart);

        getChildren().add(title);
        getChildren().add(eeCustomizationPane);
        getChildren().add(ccCustomizationPane);
    }

}
