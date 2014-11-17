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
package org.cirdles.topsoil.app.chart.concordia;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.cirdles.javafx.CustomVBox;
import org.cirdles.topsoil.app.builder.TopsoilBuilderFactory;
import org.controlsfx.control.MasterDetailPane;

/**
 * A fairly empty class.
 */
public class ErrorEllipseChartExtendedPanel extends CustomVBox {

    @FXML
    private ToggleGroup concordiaLineToggleGroup;

    public ErrorEllipseChart getChart() {
        ErrorEllipseChart result = null;
        for (Node n : this.getChildrenUnmodifiable()) {
            if (n instanceof MasterDetailPane) {
                result = (ErrorEllipseChart) ((MasterDetailPane) n).masterNodeProperty().get();
            }
        }
        return result;
    }

    public MasterDetailPane getMasterDetailPane() {
        MasterDetailPane result = null;
        for (Node n : this.getChildrenUnmodifiable()) {
            if (n instanceof MasterDetailPane) {
                result = (MasterDetailPane) n;
            }
        }
        return result;
    }
}
