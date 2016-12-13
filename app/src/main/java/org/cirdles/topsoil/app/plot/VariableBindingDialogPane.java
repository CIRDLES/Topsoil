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

import javafx.scene.control.DialogPane;
import org.cirdles.topsoil.app.dataset.Dataset;
import org.cirdles.topsoil.app.dataset.field.Fields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

/**
 *
 * @author John Zeringue
 */
public class VariableBindingDialogPane extends DialogPane {

    private static final String HEADER_TEXT = "Select the column for each variable.";

    private final Dataset dataset;
    private final VariableBindingView<?> variableBindingView;

    public VariableBindingDialogPane(List<Variable> variables, Dataset dataset) {
        this.dataset = dataset;

        variableBindingView
                = new VariableBindingView(variables, dataset.getFields());

        configureThis();
    }

    private void configureThis() {
        setContent(variableBindingView);
        setHeaderText(HEADER_TEXT);
        getButtonTypes().setAll(OK, CANCEL);
    }

    public List<Map<String, Object>> getData() {
        PlotContext plotContext = this.getPlotContext();

        List<Map<String, Object>> data = new ArrayList<>();

        dataset.getEntries().forEach(entry -> {
            Map<String, Object> d = new HashMap<>();

            variableBindingView.getControls().forEach(control -> {
                Variable<?> variable = control.getVariable();
                d.put(variable.getName(), plotContext.getValue(variable, entry).get());
            });

            d.put("Selected", true);

            entry.<Boolean>get(Fields.SELECTED).ifPresent(selected -> {
                d.put("Selected", selected);
            });

            data.add(d);
        });

        return data;
    }

    public PlotContext getPlotContext() {
        PlotContext plotContext = new SimplePlotContext(dataset);

        variableBindingView.getControls().forEach(control -> {
            plotContext.addBinding(
                    control.getVariable(),
                    control.getFieldSelection(),
                    control.getVariableFormatSelection()
            );
        });

        return plotContext;
    }

}
