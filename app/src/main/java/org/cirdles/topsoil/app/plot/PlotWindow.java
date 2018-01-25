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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.cirdles.topsoil.plot.internal.SVGSaver;
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

    public PlotWindow(Plot plot) {
        super(self -> {
            self.plot = plot;
        });
    }

    public Plot getPlot() {
        return this.plot;
    }

    private void initializeToolbar() {
        if (plot instanceof JavaScriptPlot) {
            JavaScriptPlot javaScriptPlot = (JavaScriptPlot) plot;

            Button saveToSVG = new Button("Save as SVG");
            saveToSVG.setOnAction(mouseEvent -> {
                new SVGSaver().save(javaScriptPlot.displayAsSVGDocument());
            });

            Button recenter = new Button("Re-center");
            recenter.setOnAction(mouseEvent -> {
                javaScriptPlot.recenter();
            });
            
            Button snapToCorners = new Button("Snap to Corners");
            snapToCorners.setOnAction(mouseEvent -> {
                javaScriptPlot.snapToCorners();
            });

            Text loadingIndicator = new Text("Loading...");

            javaScriptPlot.getLoadFuture().thenRunAsync(() -> {
                        loadingIndicator.visibleProperty().bind(
                                javaScriptPlot.getWebEngine().getLoadWorker()
                                        .stateProperty().isEqualTo(Worker.State.RUNNING));
                    },
                    Platform::runLater
            );

            plotToolBar.getItems().addAll(saveToSVG, recenter, snapToCorners, loadingIndicator);
        }
    }

    private void initializePlotAndConfig() {
        Node node = plot.displayAsNode();
        AnchorPane anchor = new AnchorPane(node);
        plotAndConfig.getChildren().setAll(anchor);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);

        HBox.setHgrow(anchor, Priority.ALWAYS);
    }

    @FXML
    private void initialize() {
        initializeToolbar();
        initializePlotAndConfig();
        VBox.setVgrow(plotAndConfig, Priority.ALWAYS);
    }

}
