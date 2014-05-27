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
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.cirdles.jfxutils.NodeToSVGConverter;
import org.cirdles.topsoil.builder.TopsoilBuilderFactory;
import org.controlsfx.control.MasterDetailPane;

/**
 * A ToolBar for use with ErrorCharts.
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class ErrorChartToolBar extends ToolBar {

    private ErrorEllipseChart chart;
    private MasterDetailPane customPanelShower;

    @FXML
    private Button customizationButton;
    @FXML
    private ChoiceBox confidenceLevel;
    @FXML
    private CheckBox lockToQ1;

    private final ResourceBundle bundle;

    public ErrorChartToolBar(ErrorEllipseChart chart_arg, MasterDetailPane customPanelShower_args) {

        chart = chart_arg;
        customPanelShower = customPanelShower_args;
        bundle = ResourceBundle.getBundle("org.cirdles.topsoil.Resources");

        FXMLLoader loader = new FXMLLoader(ErrorChartToolBar.class.getResource("errorellipsetoolbar.fxml"),
                                           bundle);
        loader.setRoot(this);
        loader.setController(this);
        loader.setBuilderFactory(new TopsoilBuilderFactory());

        try {
            loader.load();

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
        } catch (IOException ex) {
            Logger.getLogger(ErrorChartToolBar.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void exportToSVG() {
        //start_turnNodeToText(chart); (Tool for developper/ should always be commented before commit)
        NodeToSVGConverter converter = new NodeToSVGConverter();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to SVG");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG Image", "*.svg"));
        File file = fileChooser.showSaveDialog(getScene().getWindow());

        converter.convert(chart, file);
    }

    @FXML
    private void switchCustomPanel() {
        if (customPanelShower.showDetailNodeProperty().get() == true) {
            customPanelShower.showDetailNodeProperty().set(false);
            customizationButton.setText(bundle.getString("showCustomizationPanel"));
        } else {
            customPanelShower.showDetailNodeProperty().set(true);
            customizationButton.setText(bundle.getString("hideCustomizationPanel"));
        }
    }

    @FXML
    private void resetView() {
        chart.getXAxis().autoRangingProperty().set(true);
        chart.getYAxis().autoRangingProperty().set(true);
    }

    /*
     * DEVELOPPER TOOLS
     */
    private void turnNodeToText(Node n, PrintWriter pw, String prefix) {
        //pw.println(prefix+"--");
        pw.println(prefix + n.getClass().getName());
        /*pw.println(prefix+"Layout X: "+n.getLayoutX());
         pw.println(prefix+"Layout Y: "+n.getLayoutY());
         pw.println(prefix+"Transform X: "+n.getTranslateX());
         pw.println(prefix+"Transform Y: "+n.getTranslateY());
         pw.println(prefix+"Transform Z: "+n.getTranslateZ());
         pw.println(prefix+"LocalToParent: "+n.getLocalToParentTransform());
         pw.println(prefix+"LocalToScene: "+n.getLocalToSceneTransform());
         if(n instanceof Label){
         Label l = (Label) n;
         pw.println(prefix+"Text: "+l.getText());
            
         }
         if(!n.getTransforms().isEmpty()){
         pw.println(prefix+"Transforms :");
         for(Transform t : n.getTransforms()){
         pw.println(prefix+"..."+t.toString());
         }
         }
         pw.println(prefix+"--");*/

        if (n instanceof Parent) {
            Parent parent = (Parent) n;
            String new_prefix = prefix + "\t";
            for (Node new_n : parent.getChildrenUnmodifiable()) {
                turnNodeToText(new_n, pw, new_prefix);
            }
        }
    }

    private void start_turnNodeToText(Node n) {
        PrintWriter pw = null;
        try {
            File f = new File("/Users/pfif/Documents/beboptango");
            pw = new PrintWriter(f);
            turnNodeToText(n, pw, "");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ErrorChartToolBar.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }
}
