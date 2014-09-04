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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToolBar;
import javafx.util.StringConverter;
import org.cirdles.topsoil.builder.TopsoilBuilderFactory;
import org.cirdles.topsoil.chart.concordia.panels.SVGExportDialog;
import org.controlsfx.control.MasterDetailPane;

/**
 * A ToolBar for use with ErrorCharts.
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class ErrorChartToolBar extends ToolBar {

    private final ErrorEllipseChart chart;
    private final MasterDetailPane masterDetailPane;

    @FXML private Button customizationButton;
    @FXML private ChoiceBox confidenceLevel;
    @FXML private CheckBox lockToQ1;

    @FXML private ResourceBundle resources;

    public ErrorChartToolBar(ErrorEllipseChart chart, MasterDetailPane masterDetailPane) {
        this.chart = chart;
        this.masterDetailPane = masterDetailPane;

        FXMLLoader loader = new FXMLLoader(ErrorChartToolBar.class.getResource("errorellipsetoolbar.fxml"),
                                           ResourceBundle.getBundle("org.cirdles.topsoil.Resources"));
        loader.setRoot(this);
        loader.setController(this);
        loader.setBuilderFactory(new TopsoilBuilderFactory());

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ErrorChartToolBar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initialize() {

        chart.confidenceLevel().bind(confidenceLevel.valueProperty());
        Map<Number, String> confidenceLevels = new HashMap<>();
        confidenceLevels.put(1, "1\u03c3");
        confidenceLevels.put(2, "2\u03c3");
        confidenceLevels.put(2.4477, "95%");
        confidenceLevel.getItems().addAll(confidenceLevels.keySet());
        confidenceLevel.getSelectionModel().select(1);
        confidenceLevel.setConverter(new StringConverter<Number>() {

            @Override
            public String toString(Number object) {
                return confidenceLevels.get(object);
            }

            /*
             * Unused by ChoiceBox
             */
            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        lockToQ1.selectedProperty().set(true);
        chart.lockToQ1Property().bindBidirectional(lockToQ1.selectedProperty());
    }

    @FXML
    private void exportToSVG() {
        //start_turnNodeToText(chart); (Tool for developper/ should always be commented before commit)
        SVGExportDialog exportpanel = new SVGExportDialog(this, chart);
        exportpanel.show();
    }

    @FXML
    private void switchCustomPanel() {
        if (masterDetailPane.showDetailNodeProperty().get() == true) {
            masterDetailPane.showDetailNodeProperty().set(false);
            customizationButton.setText(resources.getString("showCustomizationPanel"));
        } else {
            masterDetailPane.showDetailNodeProperty().set(true);
            customizationButton.setText(resources.getString("hideCustomizationPanel"));
        }
    }

    @FXML
    private void resetView() {
        chart.resetView();
    }

    /*
     * DEVELOPER TOOLS
     */
    private void nodeToText(Node n, PrintWriter pw, String prefix) {
        pw.println(prefix + n.getClass().getName());

        if (n instanceof Parent) {
            Parent parent = (Parent) n;
            String new_prefix = prefix + "\t";
            for (Node new_n : parent.getChildrenUnmodifiable()) {
                nodeToText(new_n, pw, new_prefix);
            }
        }
    }

    private void nodeToText(Node n) {
        PrintWriter pw = null;
        try {
            File f = new File("/Users/pfif/Documents/beboptango");
            pw = new PrintWriter(f);
            nodeToText(n, pw, "");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ErrorChartToolBar.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }
}
