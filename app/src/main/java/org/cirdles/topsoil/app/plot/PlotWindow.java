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
package org.cirdles.topsoil.app.plot;

import com.johnzeringue.extendsfx.layout.CustomVBox;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.cirdles.topsoil.app.util.SVGSaver;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;

/**
 *
 * @author John Zeringue
 */
public class PlotWindow extends CustomVBox<PlotWindow> {

    @FXML
    private ToolBar plotToolBar;
    @FXML
    private HBox plotAndConfig;

    private Plot plot;

    private Node propertiesPanel;

    public PlotWindow(Plot plot, Node propertiesPanel) {
        super(self -> {
            self.plot = plot;
            self.propertiesPanel = propertiesPanel;
        });
    }

    private void initializeToolbar() {
        if (plot instanceof JavaScriptPlot) {
            JavaScriptPlot javaScriptPlot = (JavaScriptPlot) plot;

            Button saveToSVG = new Button("Save as SVG");
            saveToSVG.setOnAction(mouseEvent -> {
                new SVGSaver().save(javaScriptPlot.displayAsSVGDocument());
            });

            Text loadingIndicator = new Text("Loading...");

            javaScriptPlot.getLoadFuture().thenRunAsync(() -> {
                        loadingIndicator.visibleProperty().bind(
                                javaScriptPlot.getWebEngine().getLoadWorker()
                                        .stateProperty().isEqualTo(Worker.State.RUNNING));
                    },
                    Platform::runLater
            );

            plotToolBar.getItems().addAll(saveToSVG, loadingIndicator);
        }
    }

    private void initializePlotAndConfig() {
        try {
            plotAndConfig.getChildren().setAll(
                    plot.displayAsNode(),
                    propertiesPanel);
        } catch (UnsupportedOperationException ex) {
            plotAndConfig.getChildren().setAll(
                    plot.displayAsNode());
        }
    }

    @FXML
    private void initialize() {
        initializeToolbar();
        initializePlotAndConfig();
    }

}
