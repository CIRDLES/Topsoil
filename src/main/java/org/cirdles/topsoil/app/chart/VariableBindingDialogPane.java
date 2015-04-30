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
package org.cirdles.topsoil.app.chart;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;
import javafx.scene.control.DialogPane;
import org.cirdles.topsoil.app.ExpressionType;
import org.cirdles.topsoil.data.Dataset;
import org.cirdles.topsoil.data.Field;

/**
 *
 * @author John Zeringue
 */
public class VariableBindingDialogPane extends DialogPane {
    
    private static final String HEADER_TEXT = "Select the column for each variable.";
    
    private final VariableBindingView chartInitializationView;

    public VariableBindingDialogPane(Dataset dataset) {
        chartInitializationView = new VariableBindingView(dataset);
        
        setContent(chartInitializationView);
        setHeaderText(HEADER_TEXT);
        getButtonTypes().setAll(OK, CANCEL);
    }
    
    Field<Number> getXSelection() {
        return chartInitializationView.getXSelection();
    }
    
    Field<Number> getSigmaXSelection() {
        return chartInitializationView.getSigmaXSelection();
    }
    
    double getSigmaXErrorSize() {
        return chartInitializationView.getSigmaXErrorSize();
    }
    
    ExpressionType getSigmaXExpressionType() {
        return chartInitializationView.getSigmaXExpressionType();
    }
    
    Field<Number> getYSelection() {
        return chartInitializationView.getYSelection();
    }
    
    Field<Number> getSigmaYSelection() {
        return chartInitializationView.getSigmaYSelection();
    }
    
    double getSigmaYErrorSize() {
        return chartInitializationView.getSigmaYErrorSize();
    }
    
    ExpressionType getSigmaYExpressionType() {
        return chartInitializationView.getSigmaYExpressionType();
    }
    
    Field<Number> getRhoSelection() {
        return chartInitializationView.getRhoSelection();
    }
    
}
