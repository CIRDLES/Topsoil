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
package org.cirdles.topsoil.app.chart;

import javafx.scene.control.ButtonType;
import static javafx.scene.control.ButtonType.OK;
import javafx.scene.control.Dialog;
import org.cirdles.topsoil.app.UncertaintyVariableFormat;
import org.cirdles.topsoil.chart.Chart;
import org.cirdles.topsoil.chart.SimpleVariableContext;
import org.cirdles.topsoil.chart.VariableContext;
import org.cirdles.topsoil.data.Dataset;

public class VariableBindingDialog extends Dialog<VariableContext> {

    private final Dataset dataset;
    private final Chart chart;

    public VariableBindingDialog(Dataset dataset, Chart chart) {
        this.dataset = dataset;
        this.chart = chart;

        setDialogPane(new VariableBindingDialogPane(dataset));
        setResizable(false);
        setResultConverter(this::convertResult);
    }

    VariableContext extractVariableContextFromDialogPane() {
        VariableBindingDialogPane columnSelector
                = (VariableBindingDialogPane) getDialogPane();

        VariableContext variableContext = new SimpleVariableContext(dataset);

        chart.getVariables().ifPresent(variables -> {
            // x
            variableContext.addBinding(
                    variables.get(0), columnSelector.getXSelection());

            // sigma x
            variableContext.addBinding(
                    variables.get(1), columnSelector.getSigmaXSelection(),
                    new UncertaintyVariableFormat(
                            columnSelector.getSigmaXErrorSize(),
                            columnSelector.getSigmaXExpressionType()));

            // y
            variableContext.addBinding(
                    variables.get(2), columnSelector.getYSelection());

            // sigma y
            variableContext.addBinding(
                    variables.get(3), columnSelector.getSigmaYSelection(),
                    new UncertaintyVariableFormat(
                            columnSelector.getSigmaYErrorSize(),
                            columnSelector.getSigmaYExpressionType()));

            // rho
            variableContext.addBinding(
                    variables.get(4), columnSelector.getRhoSelection());
        });

        return variableContext;
    }

    VariableContext convertResult(ButtonType type) {
        if (type == OK) {
            return extractVariableContextFromDialogPane();
        } else {
            return null;
        }
    }

}
