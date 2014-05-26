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


import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.builder.TopsoilBuilderFactory;
import org.controlsfx.control.MasterDetailPane;

/**
 * A fairly empty class.
 */
public class ErrorEllipseChartExtendedPanel extends VBox {

    public ErrorEllipseChartExtendedPanel() {
        //setPadding(new Insets(10));
        FXMLLoader loader = new FXMLLoader(ErrorEllipseChartExtendedPanel.class.getResource("errorellipsechartextendedpanel.fxml"),
                                           ResourceBundle.getBundle("org.cirdles.topsoil.Resources"));
        loader.setRoot(this);
        loader.setController(this);
        loader.setBuilderFactory(new TopsoilBuilderFactory());
        
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ErrorEllipseChartExtendedPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ErrorEllipseChart getChart(){
        ErrorEllipseChart retour = null;
        for(Node n : this.getChildrenUnmodifiable()){
            if(n instanceof MasterDetailPane){
                retour = (ErrorEllipseChart) ((MasterDetailPane) n).masterNodeProperty().get();
            }
        }
        return retour;
    }
    
    public MasterDetailPane getMasterDetailPane(){
        MasterDetailPane retour = null;
        for(Node n : this.getChildrenUnmodifiable()){
            if(n instanceof MasterDetailPane){
                retour = (MasterDetailPane) n;
            }
        }
        return retour;
    }
}
