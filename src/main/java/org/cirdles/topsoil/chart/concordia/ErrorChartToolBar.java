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
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import org.cirdles.jfxutils.NodeToSVGConverter;

/**
 * A ToolBar for use with ErrorCharts.
 * 
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */

public class ErrorChartToolBar extends ToolBar {
    public ErrorChartToolBar(ConcordiaChart chart) {
        Button exportToSVG = new Button("Export to SVG");
        exportToSVG.setOnAction((ActionEvent event) -> {
            start_turnNodeToText(chart);
            NodeToSVGConverter converter = new NodeToSVGConverter();
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export to SVG");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File file = fileChooser.showSaveDialog(getScene().getWindow());
            
            converter.convert(chart, file);
        });
        
        getItems().add(exportToSVG);
    }
    
    private void turnNodeToText(Node n, PrintWriter pw, String prefix){
        //pw.println(prefix+"--");
        pw.println(prefix+n.getClass().getName());
        /*pw.println(prefix+"Layout X: "+n.getLayoutX());
        pw.println(prefix+"Layout Y: "+n.getLayoutY());
        pw.println(prefix+"Transform X: "+n.getTranslateX());
        pw.println(prefix+"Transform Y: "+n.getTranslateY());
        pw.println(prefix+"Transform Z: "+n.getTranslateZ());
        pw.println(prefix+"Transforms :");
        for(Transform t : n.getTransforms()){
            pw.println(prefix+"..."+t.toString());
        }*/
        //pw.println(prefix+"--");
        
        if(n instanceof Parent){
            Parent parent = (Parent) n;
            String new_prefix = prefix+"\t";
            for(Node new_n : parent.getChildrenUnmodifiable()){
               turnNodeToText(new_n, pw, new_prefix); 
            }
        }
    }
    
    private void start_turnNodeToText(Node n){
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
